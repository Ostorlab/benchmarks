import SwiftUI

@main
struct WeatherNowApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(WeatherService.shared)
        }
    }
}
