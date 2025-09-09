//
//  ContentView.swift
//  VaultApp
//
//  Created by aitougrram on 5/9/2025.
//

import SwiftUI

struct ContentView: View {
    @EnvironmentObject var authStore: AuthStore
    
    var body: some View {
        if !authStore.isPINSet {
            PINSetupView()
        } else if authStore.isAuthenticated {
            VaultView()
        } else {
            PINEntryView()
        }
    }
}

#Preview {
    ContentView()
        .environmentObject(AuthStore())
        .environmentObject(SecretsStore())
}
