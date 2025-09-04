// File: VaultApp.swift
import SwiftUI

@main
struct VaultApp_BruteForceApp: App {
    // Create the authentication store and provide it to the whole app
    @StateObject private var authStore = AuthStore()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(authStore) // Provide it here
        }
        // Note: No background/foreground tracking needed for this vulnerability
    }
}
