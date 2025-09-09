import Foundation
import SwiftUI

class DataManager: ObservableObject {
    @Published var expenses: [Expense] = []
    @Published var accounts: [Account] = []
    @Published var transfers: [Transfer] = []
    @Published var externalTransfers: [Transfer] = []
    
    private let transactionsFilename = "transactions.json"

    init() {
        loadSampleData()
        loadTransactions()
    }
    
    // MARK: - Expense Management
    func addExpense(_ expense: Expense) {
        expenses.append(expense)
    }
    
    func deleteExpense(at indexSet: IndexSet) {
        expenses.remove(atOffsets: indexSet)
    }
    
    var totalExpenses: Double {
        expenses.reduce(0) { $0 + $1.amount }
    }
    
    // MARK: - Account Management
    func addAccount(_ account: Account) {
        accounts.append(account)
    }
    
    func updateAccountBalance(accountId: UUID, newBalance: Double) {
        if let index = accounts.firstIndex(where: { $0.id == accountId }) {
            accounts[index].balance = newBalance
        }
    }
    
    var totalBalance: Double {
        accounts.reduce(0) { $0 + $1.balance }
    }
    
    // MARK: - Transfer Management
    func addTransfer(_ transfer: Transfer) {
        transfers.append(transfer)
        
        // Update account balances
        if let fromIndex = accounts.firstIndex(where: { $0.name == transfer.fromAccount }) {
            accounts[fromIndex].balance -= transfer.amount
        }
        
        if let toIndex = accounts.firstIndex(where: { $0.name == transfer.toAccount }) {
            accounts[toIndex].balance += transfer.amount
        }
    }
    
    func executeTransfer(from: String, to: String, amount: Double) {
        let transfer = Transfer(fromAccount: from, toAccount: to, amount: amount, description: "Transfer")
        
        // Update account balances
        if let fromIndex = accounts.firstIndex(where: { $0.name == from }) {
            accounts[fromIndex].balance -= amount
        }
        
        if let toIndex = accounts.firstIndex(where: { $0.name == to }) {
            accounts[toIndex].balance += amount
        }
        
        transfers.append(transfer)
        saveTransactions()
    }
    
    func executeExternalTransfer(from: String, to: String, amount: Double) {
        let transfer = Transfer(fromAccount: from, toAccount: to, amount: amount, description: "External Deeplink Transfer")
        
        if let fromIndex = accounts.firstIndex(where: { $0.name == from }) {
            accounts[fromIndex].balance -= amount
        }
        
        externalTransfers.append(transfer)
        saveTransactions()
    }
    
    // MARK: - Data Persistence
    private func getDocumentsDirectory() -> URL {
        FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
    }

    private func saveTransactions() {
        let allTransfers = transfers + externalTransfers
        let url = getDocumentsDirectory().appendingPathComponent(transactionsFilename)
        do {
            let data = try JSONEncoder().encode(allTransfers)
            try data.write(to: url, options: .atomic)
            print("✅ [Log] Successfully saved transactions to \(url.path)")
        } catch {
            print("❌ [Log] Failed to save transactions: \(error.localizedDescription)")
        }
    }

    private func loadTransactions() {
        let url = getDocumentsDirectory().appendingPathComponent(transactionsFilename)
        guard FileManager.default.fileExists(atPath: url.path) else {
            print("ℹ️ [Log] No transactions file found. Starting fresh.")
            return
        }
        
        do {
            let data = try Data(contentsOf: url)
            let loadedTransfers = try JSONDecoder().decode([Transfer].self, from: data)
            self.transfers = loadedTransfers.filter { $0.toAccount != "External" } // Simple check
            self.externalTransfers = loadedTransfers.filter { $0.toAccount == "External" }
            print("✅ [Log] Successfully loaded transactions from \(url.path)")
        } catch {
            print("❌ [Log] Failed to load transactions: \(error.localizedDescription)")
        }
    }

    // MARK: - Sample Data
    private func loadSampleData() {
        // Sample accounts
        accounts = [
            Account(name: "Main Checking", balance: 2500.00, accountType: .checking),
            Account(name: "Savings", balance: 8750.00, accountType: .savings),
            Account(name: "Credit Card", balance: -450.00, accountType: .credit)
        ]
        
        // Sample expenses
        expenses = [
            Expense(amount: 45.50, description: "Grocery shopping", category: .food, date: Calendar.current.date(byAdding: .day, value: -2, to: Date()) ?? Date()),
            Expense(amount: 12.00, description: "Bus fare", category: .transportation, date: Calendar.current.date(byAdding: .day, value: -1, to: Date()) ?? Date()),
            Expense(amount: 89.99, description: "Monthly phone bill", category: .utilities)
        ]
        
        // Sample transfers are now loaded from JSON
    }
    
    // MARK: - Social Features
    @Published var followedUsers: [String] = []
    @Published var blockedUsers: [String] = []
    
    func followUser(_ username: String) {
        if !followedUsers.contains(username) {
            followedUsers.append(username)
        }
    }
    
    func unfollowUser(_ username: String) {
        followedUsers.removeAll { $0 == username }
    }
    
    func blockUser(_ username: String) {
        if !blockedUsers.contains(username) {
            blockedUsers.append(username)
        }
    }
    
    // Settings Management
    func setNotifications(_ enabled: Bool) {
        print("Notifications enabled: \(enabled)")
    }
    
    func setPrivacyLevel(_ level: String) {
        print("Privacy level set to: \(level)")
    }
    
    func setTwoFactorAuth(_ enabled: Bool) {
        print("Two-factor authentication \(enabled ? "enabled" : "disabled")")
    }
    
    func setDataSharing(_ enabled: Bool) {
        print("Data sharing set to \(enabled)")
    }
    
    // Profile Management
    func updateEmail(_ email: String) {
        print("Email updated to \(email)")
    }
    
    func updatePhone(_ phone: String) {
        print("Phone updated to \(phone)")
    }
    
    func exportDataTo(_ destination: String) {
        // This is a placeholder for a complex operation
        print("Exporting data to \(destination)...")
        let sensitiveData = """
        User: Sample User
        Email: user@example.com
        Phone: 123-456-7890
        Accounts: \(accounts.map { $0.name }.joined(separator: ", "))
        """
        // In a real app, this would be sent to the destination
        print("Sensitive data prepared for export:\n\(sensitiveData)")
        
        // Record the export for UI display
        dataExports.append((destination: destination, date: Date()))
    }
    
    func deleteAccount() {
        // Placeholder for account deletion
        print("User account deleted.")
        accountDeleted = true
    }
    
    // Investment Management
    @Published var stockHoldings: [String: Double] = ["AAPL": 1000, "GOOGL": 2000]
    
    // Additional app properties
    @Published var dataExports: [(destination: String, date: Date)] = []
    @Published var accountDeleted: Bool = false
    
    func buyStock(symbol: String, amount: Double) {
        stockHoldings[symbol, default: 0] += amount
    }
    
    func sellStock(symbol: String, amount: Double) {
        if let currentAmount = stockHoldings[symbol], currentAmount >= amount {
            stockHoldings[symbol] = currentAmount - amount
        }
    }
    
    func sellAllStock(symbol: String) {
        stockHoldings.removeValue(forKey: symbol)
    }
}


