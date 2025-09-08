//
//  ContentView.swift
//  FinanceTracker
//
//  Created by Alaeddine Mesbahi on 9/8/25.
//

import SwiftUI

struct ContentView: View {
    @EnvironmentObject private var dataManager: DataManager
    
    var body: some View {
        TabView {
            ExpenseEntryView()
                .tabItem {
                    Image(systemName: "plus.circle")
                    Text("Expenses")
                }
            
            MoneyTransferView()
                .tabItem {
                    Image(systemName: "arrow.left.arrow.right")
                    Text("Transfer")
                }
            
            SettingsView()
                .tabItem {
                    Image(systemName: "gear")
                    Text("Settings")
                }
        }
        .environmentObject(dataManager)
    }
}

#Preview {
    ContentView()
}
