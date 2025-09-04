// File: ContentView.swift
import SwiftUI

struct ContentView: View {
    // Access the authentication store from the environment
    @EnvironmentObject var authStore: AuthStore

    var body: some View {
        // Show the Vault if authenticated, otherwise show the PIN entry
        if authStore.isAuthenticated {
            VaultView()
        } else {
            PINEntryView()
        }
    }
}

#Preview {
    ContentView()
        .environmentObject(AuthStore())
}
