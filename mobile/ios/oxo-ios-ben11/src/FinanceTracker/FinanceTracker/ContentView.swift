import SwiftUI

struct ContentView: View {
    @EnvironmentObject var dataManager: DataManager
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            // Accounts Tab
            AccountsView()
                .environmentObject(dataManager)
                .tabItem {
                    Image(systemName: "creditcard.fill")
                    Text("Accounts")
                }
                .tag(0)
            
            // Social Tab
            SocialView()
                .environmentObject(dataManager)
                .tabItem {
                    Image(systemName: "person.2.fill")
                    Text("Social")
                }
                .tag(1)
            
            // Settings Tab
            SettingsView()
                .environmentObject(dataManager)
                .tabItem {
                    Image(systemName: "gearshape.fill")
                    Text("Settings")
                }
                .tag(2)
            
            // Investments Tab
            InvestmentsView()
                .environmentObject(dataManager)
                .tabItem {
                    Image(systemName: "chart.line.uptrend.xyaxis")
                    Text("Invest")
                }
                .tag(3)
        }
    }
}

// MARK: - Accounts View
struct AccountsView: View {
    @EnvironmentObject var dataManager: DataManager
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                // Account Balances
                VStack(alignment: .leading, spacing: 10) {
                    Text("Account Balances")
                        .font(.headline)
                    
                    ForEach(dataManager.accounts) { account in
                        HStack {
                            Text(account.name)
                            Spacer()
                            Text("$\(account.balance, specifier: "%.2f")")
                                .foregroundColor(account.balance >= 0 ? .green : .red)
                        }
                        .padding(.horizontal)
                    }
                }
                .padding()
                .background(Color.gray.opacity(0.1))
                .cornerRadius(10)
                
                // External Transfers
                if !dataManager.externalTransfers.isEmpty {
                    VStack(alignment: .leading) {
                        Text("External Transfers")
                            .font(.headline)
                        
                        ForEach(dataManager.externalTransfers) { transfer in
                            HStack {
                                Text("To: \(transfer.toAccount)")
                                Spacer()
                                Text("-$\(transfer.amount, specifier: "%.2f")")
                                    .foregroundColor(.red)
                            }
                        }
                    }
                    .padding()
                    .background(Color.red.opacity(0.1))
                    .cornerRadius(10)
                }
                
                Spacer()
            }
            .padding()
            .navigationTitle("Finance Tracker")
        }
    }
}

// MARK: - Social View
struct SocialView: View {
    @EnvironmentObject var dataManager: DataManager
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                // Followed Users
                VStack(alignment: .leading) {
                    Text("Following (\(dataManager.followedUsers.count))")
                        .font(.headline)
                    
                    if dataManager.followedUsers.isEmpty {
                        Text("No users followed")
                            .foregroundColor(.gray)
                    } else {
                        ForEach(dataManager.followedUsers, id: \.self) { user in
                            Text("👤 \(user)")
                        }
                    }
                }
                .padding()
                .background(Color.blue.opacity(0.1))
                .cornerRadius(10)
                
                // Blocked Users
                VStack(alignment: .leading) {
                    Text("Blocked (\(dataManager.blockedUsers.count))")
                        .font(.headline)
                    
                    if dataManager.blockedUsers.isEmpty {
                        Text("No users blocked")
                            .foregroundColor(.gray)
                    } else {
                        ForEach(dataManager.blockedUsers, id: \.self) { user in
                            Text("🚫 \(user)")
                        }
                    }
                }
                .padding()
                .background(Color.orange.opacity(0.1))
                .cornerRadius(10)
                
                Spacer()
            }
            .padding()
            .navigationTitle("Social")
        }
    }
}

// MARK: - Investments View
struct InvestmentsView: View {
    @EnvironmentObject var dataManager: DataManager
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                // Stock Holdings
                VStack(alignment: .leading) {
                    Text("Stock Holdings")
                        .font(.headline)
                    
                    ForEach(dataManager.stockHoldings.keys.sorted(), id: \.self) { symbol in
                        if let amount = dataManager.stockHoldings[symbol], amount > 0 {
                            HStack {
                                    Text(symbol)
                                        .font(.system(.body, design: .monospaced))
                                Spacer()
                                Text("$\(amount, specifier: "%.2f")")
                                    .foregroundColor(.green)
                            }
                        }
                    }
                }
                .padding()
                .background(Color.green.opacity(0.1))
                .cornerRadius(10)
                
                Spacer()
            }
            .padding()
            .navigationTitle("Investments")
        }
    }
}

#Preview {
    ContentView()
        .environmentObject(DataManager())
}
