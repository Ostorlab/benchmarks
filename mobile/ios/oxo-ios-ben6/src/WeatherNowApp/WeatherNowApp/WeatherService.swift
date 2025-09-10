import Foundation
import Network
import Combine

class WeatherService: NSObject, ObservableObject {
    static let shared = WeatherService()

    private let geocodingURL = "https://geocoding-api.open-meteo.com/v1/search"
    private let weatherURL = "https://api.open-meteo.com/v1/forecast"

    private lazy var urlSession: URLSession = {
        let config = URLSessionConfiguration.default
        config.timeoutIntervalForRequest = 30
        config.timeoutIntervalForResource = 60
        return URLSession(configuration: config, delegate: self, delegateQueue: nil)
    }()

    override init() {
        super.init()
    }

    func fetchWeather(for city: String) async throws -> WeatherData {
        let encodedCity = city.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? city
        let geocodingURLString = "\(geocodingURL)?name=\(encodedCity)&count=1&language=en&format=json"

        guard let geocodingAPIURL = URL(string: geocodingURLString) else {
            throw WeatherError.invalidURL
        }

        do {
            let (geocodingData, geocodingResponse) = try await urlSession.data(from: geocodingAPIURL)

            guard let httpResponse = geocodingResponse as? HTTPURLResponse,
                  httpResponse.statusCode == 200 else {
                throw WeatherError.invalidResponse
            }

            let location = try parseGeocodingData(from: geocodingData)

            let weatherURLString = "\(weatherURL)?latitude=\(location.latitude)&longitude=\(location.longitude)&current_weather=true&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m,surface_pressure&timezone=auto"

            guard let weatherAPIURL = URL(string: weatherURLString) else {
                throw WeatherError.invalidURL
            }

            let (weatherData, weatherResponse) = try await urlSession.data(from: weatherAPIURL)

            guard let weatherHTTPResponse = weatherResponse as? HTTPURLResponse,
                  weatherHTTPResponse.statusCode == 200 else {
                throw WeatherError.invalidResponse
            }

            return try parseWeatherData(from: weatherData, cityName: location.name, country: location.country)
        } catch {
            throw WeatherError.networkError
        }
    }

    private func parseGeocodingData(from data: Data) throws -> LocationData {
        struct GeocodingResponse: Codable {
            let results: [LocationResult]?
        }

        struct LocationResult: Codable {
            let name: String
            let latitude: Double
            let longitude: Double
            let country: String
        }

        let decoder = JSONDecoder()
        let response = try decoder.decode(GeocodingResponse.self, from: data)

        guard let firstResult = response.results?.first else {
            throw WeatherError.decodingError
        }

        return LocationData(
            name: firstResult.name,
            latitude: firstResult.latitude,
            longitude: firstResult.longitude,
            country: firstResult.country
        )
    }

    private func parseWeatherData(from data: Data, cityName: String, country: String) throws -> WeatherData {
        struct WeatherResponse: Codable {
            let current_weather: CurrentWeather
            let hourly: HourlyData
        }

        struct CurrentWeather: Codable {
            let temperature: Double
            let windspeed: Double
            let weathercode: Int
        }

        struct HourlyData: Codable {
            let relative_humidity_2m: [Int]
            let surface_pressure: [Double]
        }

        let decoder = JSONDecoder()
        let response = try decoder.decode(WeatherResponse.self, from: data)

        let current = response.current_weather
        let humidity = response.hourly.relative_humidity_2m.first ?? 50
        let pressure = Int(response.hourly.surface_pressure.first ?? 1013)

        let (description, iconName) = weatherDescription(for: current.weathercode)

        return WeatherData(
            cityName: cityName,
            country: country,
            temperature: current.temperature,
            feelsLike: current.temperature + 2,
            humidity: humidity,
            pressure: pressure,
            windSpeed: current.windspeed,
            description: description,
            iconName: iconName
        )
    }

    private func weatherDescription(for code: Int) -> (String, String) {
        switch code {
        case 0:
            return ("clear sky", "sun.max")
        case 1, 2, 3:
            return ("partly cloudy", "cloud.sun")
        case 45, 48:
            return ("fog", "cloud.fog")
        case 51, 53, 55:
            return ("drizzle", "cloud.drizzle")
        case 61, 63, 65:
            return ("rain", "cloud.rain")
        case 71, 73, 75:
            return ("snow", "snow")
        case 95, 96, 99:
            return ("thunderstorm", "cloud.bolt.rain")
        default:
            return ("partly cloudy", "cloud.sun")
        }
    }
}

struct LocationData {
    let name: String
    let latitude: Double
    let longitude: Double
    let country: String
}

extension WeatherService: URLSessionDelegate {
    func urlSession(_ session: URLSession, didReceive challenge: URLAuthenticationChallenge, completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {

        completionHandler(.useCredential, URLCredential(trust: challenge.protectionSpace.serverTrust!))
    }
}

extension WeatherService: URLSessionTaskDelegate {
    func urlSession(_ session: URLSession, task: URLSessionTask, didReceive challenge: URLAuthenticationChallenge, completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {

        completionHandler(.useCredential, URLCredential(trust: challenge.protectionSpace.serverTrust!))
    }
}

enum WeatherError: Error {
    case invalidURL
    case invalidResponse
    case networkError
    case decodingError
}
