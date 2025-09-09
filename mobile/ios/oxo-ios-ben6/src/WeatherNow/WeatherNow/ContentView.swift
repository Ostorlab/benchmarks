import SwiftUI
import CoreLocation

struct ContentView: View {
    @EnvironmentObject var weatherService: WeatherService
    @State private var searchText = ""
    @State private var currentWeather: WeatherData?
    @State private var isLoading = false
    @State private var errorMessage = ""

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    SearchBar(text: $searchText, onSearchButtonClicked: fetchWeather)

                    if isLoading {
                        ProgressView("Loading weather...")
                            .frame(height: 100)
                    } else if let weather = currentWeather {
                        WeatherCard(weather: weather)
                    } else if !errorMessage.isEmpty {
                        ErrorView(message: errorMessage)
                    } else {
                        PlaceholderView()
                    }

                    Spacer()
                }
                .padding()
            }
            .navigationTitle("WeatherNow")
            .refreshable {
                if !searchText.isEmpty {
                    await fetchWeatherAsync()
                }
            }
        }
        .onAppear {
            searchText = "New York"
            fetchWeather()
        }
    }

    private func fetchWeather() {
        guard !searchText.isEmpty else { return }

        isLoading = true
        errorMessage = ""

        Task {
            await fetchWeatherAsync()
        }
    }

    private func fetchWeatherAsync() async {
        do {
            let weather = try await weatherService.fetchWeather(for: searchText)
            DispatchQueue.main.async {
                self.currentWeather = weather
                self.isLoading = false
            }
        } catch {
            DispatchQueue.main.async {
                self.errorMessage = "Failed to fetch weather data"
                self.isLoading = false
            }
        }
    }
}

struct SearchBar: View {
    @Binding var text: String
    let onSearchButtonClicked: () -> Void

    var body: some View {
        HStack {
            TextField("Enter city name", text: $text)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .onSubmit {
                    onSearchButtonClicked()
                }

            Button("Search") {
                onSearchButtonClicked()
            }
            .buttonStyle(.borderedProminent)
        }
    }
}

struct WeatherCard: View {
    let weather: WeatherData

    var body: some View {
        VStack(spacing: 15) {
            HStack {
                VStack(alignment: .leading) {
                    Text(weather.cityName)
                        .font(.title)
                        .fontWeight(.bold)
                    Text(weather.country)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                Spacer()
                Image(systemName: weather.iconName)
                    .font(.system(size: 50))
                    .foregroundColor(.blue)
            }

            HStack {
                VStack(alignment: .leading) {
                    Text("\(Int(weather.temperature))°")
                        .font(.system(size: 60, weight: .thin))
                    Text(weather.description)
                        .font(.headline)
                        .textCase(.capitalized)
                }
                Spacer()
            }

            LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 2), spacing: 10) {
                WeatherDetailView(title: "Feels like", value: "\(Int(weather.feelsLike))°")
                WeatherDetailView(title: "Humidity", value: "\(weather.humidity)%")
                WeatherDetailView(title: "Wind", value: "\(weather.windSpeed) mph")
                WeatherDetailView(title: "Pressure", value: "\(weather.pressure) mb")
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(15)
        .shadow(radius: 5)
    }
}

struct WeatherDetailView: View {
    let title: String
    let value: String

    var body: some View {
        VStack {
            Text(title)
                .font(.caption)
                .foregroundColor(.secondary)
            Text(value)
                .font(.headline)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 8)
        .background(Color(.secondarySystemBackground))
        .cornerRadius(8)
    }
}

struct ErrorView: View {
    let message: String

    var body: some View {
        VStack {
            Image(systemName: "exclamationmark.triangle")
                .font(.system(size: 50))
                .foregroundColor(.orange)
            Text(message)
                .font(.headline)
                .multilineTextAlignment(.center)
        }
        .padding()
    }
}

struct PlaceholderView: View {
    var body: some View {
        VStack {
            Image(systemName: "cloud.sun")
                .font(.system(size: 80))
                .foregroundColor(.blue)
            Text("Search for a city to see the weather")
                .font(.headline)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
        }
        .padding()
    }
}
