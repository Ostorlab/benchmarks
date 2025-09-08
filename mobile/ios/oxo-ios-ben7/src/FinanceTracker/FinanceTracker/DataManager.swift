//
//  DataManager.swift
//  FinanceTracker
//
//  Created by Alaeddine Mesbahi on 9/8/25.
//

import Foundation
import SwiftUI

class DataManager: ObservableObject {
    @Published var expenses: [Expense] = []
    @Published var accounts: [Account] = []
    @Published var transfers: [Transfer] = []
    
    init() {
        loadSampleData()
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
    
    // MARK: - Sample Data
    private func loadSampleData() {
        // Sample accounts
        accounts = [
            Account(name: "Main Checking", balance: 2500.00, accountType: .checking),
            Account(name: "Savings Account", balance: 8750.00, accountType: .savings),
            Account(name: "Credit Card", balance: -450.00, accountType: .credit)
        ]
        
        // Sample expenses
        expenses = [
            Expense(amount: 45.50, description: "Grocery shopping", category: .food, date: Calendar.current.date(byAdding: .day, value: -2, to: Date()) ?? Date()),
            Expense(amount: 12.00, description: "Bus fare", category: .transportation, date: Calendar.current.date(byAdding: .day, value: -1, to: Date()) ?? Date()),
            Expense(amount: 89.99, description: "Monthly phone bill", category: .utilities)
        ]
        
        // Sample transfers
        transfers = [
            Transfer(fromAccount: "Main Checking", toAccount: "Savings Account", amount: 500.00, description: "Monthly savings", date: Calendar.current.date(byAdding: .day, value: -3, to: Date()) ?? Date())
        ]
    }
}
