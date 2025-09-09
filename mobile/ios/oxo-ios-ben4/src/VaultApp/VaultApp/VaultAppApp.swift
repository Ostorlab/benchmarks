//
//  VaultAppApp.swift
//  VaultApp
//
//  Created by aitougrram on 5/9/2025.
//

import SwiftUI

@main
struct VaultAppApp: App {
    // Create shared instances of our stores
    @StateObject private var authStore = AuthStore()
    @StateObject private var secretsStore = SecretsStore()
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(authStore)
                .environmentObject(secretsStore)
        }
    }
}
