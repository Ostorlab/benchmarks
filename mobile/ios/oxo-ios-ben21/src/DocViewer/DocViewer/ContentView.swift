//
//  ContentView.swift
//  DocViewer
//
//  Created by elyousfi on 11/09/2025.
//

import SwiftUI

struct ContentView: View {
    var body: some View {
        MainTabView()
    }
}

#Preview {
    ContentView()
        .environmentObject(DocumentManager.shared)
        .environmentObject(AuthenticationManager.shared)
}
