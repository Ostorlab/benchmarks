import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var dataManager: DataManager
    @State private var showingAddAccount = false
    @State private var newAccountName = ""
    @State private var newAccountType: Account.AccountType = .checking
    @State private var newAccountBalance = ""
    @State private var showingAlert = false
    @State private var alertMessage = ""
    @AppStorage("userName") private var userName = "User"
    @AppStorage("enableNotifications") private var enableNotifications = true
    @AppStorage("enableBiometrics") private var enableBiometrics = false
    
    var body: some View {
        NavigationView {
            List {
                // User Profile Section
                Section("Profile") {
                    HStack {
                        Image(systemName: "person.circle.fill")
                            .font(.largeTitle)
                            .foregroundColor(.blue)
                        
                        VStack(alignment: .leading) {
                            TextField("Your Name", text: $userName)
                                .font(.headline)
                            Text("Finance Tracker User")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    .padding(.vertical, 8)
                }
                
                // Account Management
                Section("Account Management") {
                    ForEach(dataManager.accounts) { account in
                        HStack {
                            Image(systemName: account.accountType.icon)
                                .foregroundColor(.blue)
                                .frame(width: 24)
                            
                            VStack(alignment: .leading) {
                                Text(account.name)
                                    .font(.subheadline)
                                Text(account.accountType.rawValue)
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            
                            Spacer()
                            
                            Text("$\(account.balance, specifier: "%.2f")")
                                .font(.subheadline)
                                .fontWeight(.medium)
                                .foregroundColor(account.balance >= 0 ? .primary : .red)
                        }
                        .padding(.vertical, 2)
                    }
                    
                    Button(action: { showingAddAccount = true }) {
                        HStack {
                            Image(systemName: "plus.circle.fill")
                                .foregroundColor(.green)
                            Text("Add New Account")
                                .foregroundColor(.primary)
                        }
                    }
                }
                
                // App Settings
                Section("App Settings") {
                    HStack {
                        Image(systemName: "bell.fill")
                            .foregroundColor(.orange)
                            .frame(width: 24)
                        Text("Notifications")
                        Spacer()
                        Toggle("", isOn: $enableNotifications)
                    }
                    
                    HStack {
                        Image(systemName: "faceid")
                            .foregroundColor(.blue)
                            .frame(width: 24)
                        Text("Biometric Authentication")
                        Spacer()
                        Toggle("", isOn: $enableBiometrics)
                    }
                }
                
                // Statistics
                Section("Statistics") {
                    HStack {
                        Image(systemName: "chart.pie.fill")
                            .foregroundColor(.green)
                            .frame(width: 24)
                        VStack(alignment: .leading) {
                            Text("Total Expenses")
                            Text("$\(dataManager.totalExpenses, specifier: "%.2f")")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    HStack {
                        Image(systemName: "banknote.fill")
                            .foregroundColor(.blue)
                            .frame(width: 24)
                        VStack(alignment: .leading) {
                            Text("Total Balance")
                            Text("$\(dataManager.totalBalance, specifier: "%.2f")")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    HStack {
                        Image(systemName: "arrow.left.arrow.right")
                            .foregroundColor(.purple)
                            .frame(width: 24)
                        VStack(alignment: .leading) {
                            Text("Total Transfers")
                            Text("\(dataManager.transfers.count) transfers")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                
                // About
                Section("About") {
                    HStack {
                        Image(systemName: "info.circle.fill")
                            .foregroundColor(.gray)
                            .frame(width: 24)
                        Text("Version")
                        Spacer()
                        Text("1.0.0")
                            .foregroundColor(.secondary)
                    }
                    
                    HStack {
                        Image(systemName: "envelope.fill")
                            .foregroundColor(.blue)
                            .frame(width: 24)
                        Text("Support")
                        Spacer()
                        Text("support@financetracker.com")
                            .foregroundColor(.secondary)
                            .font(.caption)
                    }
                }
            }
            .navigationTitle("Settings")
            .sheet(isPresented: $showingAddAccount) {
                addAccountSheet
            }
            .alert("Message", isPresented: $showingAlert) {
                Button("OK") { }
            } message: {
                Text(alertMessage)
            }
        }
    }
    
    private var addAccountSheet: some View {
        NavigationView {
            Form {
                Section("Account Details") {
                    TextField("Account Name", text: $newAccountName)
                    
                    Picker("Account Type", selection: $newAccountType) {
                        ForEach(Account.AccountType.allCases) { type in
                            HStack {
                                Image(systemName: type.icon)
                                Text(type.rawValue)
                            }
                            .tag(type)
                        }
                    }
                    .pickerStyle(SegmentedPickerStyle())
                    
                    HStack {
                        Text("$")
                            .foregroundColor(.secondary)
                        TextField("Initial Balance", text: $newAccountBalance)
                            .keyboardType(.decimalPad)
                    }
                }
            }
            .navigationTitle("Add Account")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        showingAddAccount = false
                        clearForm()
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Add") {
                        addNewAccount()
                    }
                    .disabled(newAccountName.isEmpty)
                }
            }
        }
    }
    
    private func addNewAccount() {
        let balance = Double(newAccountBalance) ?? 0.0
        
        let newAccount = Account(
            name: newAccountName.trimmingCharacters(in: .whitespacesAndNewlines),
            balance: balance,
            accountType: newAccountType
        )
        
        dataManager.addAccount(newAccount)
        
        alertMessage = "Account '\(newAccount.name)' added successfully!"
        showingAlert = true
        showingAddAccount = false
        clearForm()
    }
    
    private func clearForm() {
        newAccountName = ""
        newAccountType = .checking
        newAccountBalance = ""
    }
}

#Preview {
    SettingsView()
        .environmentObject(DataManager())
}
