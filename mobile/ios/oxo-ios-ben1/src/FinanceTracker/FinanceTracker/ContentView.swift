import SwiftUI

struct ContentView: View {
    @StateObject private var dataManager = DataManager()
    @StateObject private var currencyService = CurrencyService()
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            // Budget Overview
            BudgetOverviewView()
                .tabItem {
                    Image(systemName: "chart.pie.fill")
                    Text("Budget")
                }
                .tag(0)
            
            // Expense Entry
            ExpenseEntryView()
                .tabItem {
                    Image(systemName: "plus.circle.fill")
                    Text("Add Expense")
                }
                .tag(1)
            
            // Reports
            ExpenseReportsView()
                .tabItem {
                    Image(systemName: "doc.text.fill")
                    Text("Reports")
                }
                .tag(2)
            
            // Currency Converter
            CurrencyConverterView()
                .tabItem {
                    Image(systemName: "dollarsign.circle.fill")
                    Text("Convert")
                }
                .tag(3)
            
            // Settings
            SettingsView()
                .tabItem {
                    Image(systemName: "gear.circle.fill")
                    Text("Settings")
                }
                .tag(4)
        }
        .environmentObject(dataManager)
        .environmentObject(currencyService)
        .accentColor(.blue)
    }
}

#Preview {
    ContentView()
}
