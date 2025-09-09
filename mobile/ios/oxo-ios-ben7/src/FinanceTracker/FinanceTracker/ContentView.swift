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
