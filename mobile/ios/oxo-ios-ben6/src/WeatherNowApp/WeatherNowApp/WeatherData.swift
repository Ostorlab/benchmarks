import Foundation

struct WeatherData: Codable {
    let cityName: String
    let country: String
    let temperature: Double
    let feelsLike: Double
    let humidity: Int
    let pressure: Int
    let windSpeed: Double
    let description: String
    let iconName: String

    init(cityName: String, country: String, temperature: Double, feelsLike: Double, humidity: Int, pressure: Int, windSpeed: Double, description: String, iconName: String) {
        self.cityName = cityName
        self.country = country
        self.temperature = temperature
        self.feelsLike = feelsLike
        self.humidity = humidity
        self.pressure = pressure
        self.windSpeed = windSpeed
        self.description = description
        self.iconName = iconName
    }
}
