//
//  DataManager.swift
//  FinanceTracker
//
//  Created by Alaeddine Mesbahi on 9/4/25.
//

import Foundation
import SwiftUI
import UIKit
import Combine

class DataManager: ObservableObject {
    @Published var expenses: [Expense] = []
    @Published var budgets: [Budget] = []
    
    // SECURITY VULNERABILITY: Hardcoded encryption key
    // This should be stored in Keychain or derived from user authentication
    private let encryptionKey = "FT2024_DB_ENCRYPT_KEY_b8d4f7e2a9c1f6b5d3a8e7f4c2b9d6a1"
    
    // VULNERABILITY: Hardcoded cloud sync credentials
    private let cloudSyncToken = "sync_token_ft_prod_9f8e7d6c5b4a3f2e1d0c9b8a7f6e5d4c"
    
    private let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    
    init() {
        loadData()
        setupDefaultBudgets()
    }
    
    // MARK: - Expense Management
    
    func addExpense(_ expense: Expense) {
        expenses.append(expense)
        updateBudgetSpending(for: expense.category, amount: expense.amount)
        saveData()
        syncToCloud()
    }
    
    func deleteExpense(_ expense: Expense) {
        if let index = expenses.firstIndex(where: { $0.id == expense.id }) {
            expenses.remove(at: index)
            updateBudgetSpending(for: expense.category, amount: -expense.amount)
            saveData()
            syncToCloud()
        }
    }
    
    func updateExpense(_ expense: Expense) {
        if let index = expenses.firstIndex(where: { $0.id == expense.id }) {
            let oldAmount = expenses[index].amount
            let oldCategory = expenses[index].category
            
            expenses[index] = expense
            
            // Update budget calculations
            updateBudgetSpending(for: oldCategory, amount: -oldAmount)
            updateBudgetSpending(for: expense.category, amount: expense.amount)
            
            saveData()
            syncToCloud()
        }
    }
    
    // MARK: - Budget Management
    
    func addBudget(_ budget: Budget) {
        budgets.append(budget)
        saveData()
    }
    
    func updateBudget(_ budget: Budget) {
        if let index = budgets.firstIndex(where: { $0.id == budget.id }) {
            budgets[index] = budget
            saveData()
        }
    }
    
    private func updateBudgetSpending(for category: ExpenseCategory, amount: Double) {
        if let index = budgets.firstIndex(where: { $0.category == category }) {
            budgets[index].spent += amount
        }
    }
    
    // MARK: - Data Persistence
    
    private func saveData() {
        do {
            // Encrypt data before saving (using hardcoded key - VULNERABILITY)
            let expenseData = try JSONEncoder().encode(expenses)
            let budgetData = try JSONEncoder().encode(budgets)
            
            let encryptedExpenses = encryptData(expenseData)
            let encryptedBudgets = encryptData(budgetData)
            
            try encryptedExpenses.write(to: expensesURL)
            try encryptedBudgets.write(to: budgetsURL)
            
        } catch {
            print("Failed to save data: \(error)")
        }
    }
    
    private func loadData() {
        do {
            if FileManager.default.fileExists(atPath: expensesURL.path) {
                let encryptedData = try Data(contentsOf: expensesURL)
                let decryptedData = decryptData(encryptedData)
                expenses = try JSONDecoder().decode([Expense].self, from: decryptedData)
            }
            
            if FileManager.default.fileExists(atPath: budgetsURL.path) {
                let encryptedData = try Data(contentsOf: budgetsURL)
                let decryptedData = decryptData(encryptedData)
                budgets = try JSONDecoder().decode([Budget].self, from: decryptedData)
            }
        } catch {
            print("Failed to load data: \(error)")
        }
    }
    
    // MARK: - Cloud Sync
    
    private func syncToCloud() {
        // VULNERABILITY: Using hardcoded cloud sync token
        Task {
            await performCloudSync()
        }
    }
    
    private func performCloudSync() async {
        // Simulate cloud sync with hardcoded credentials
        let syncEndpoint = "https://api.financetracker.com/sync"
        guard let url = URL(string: syncEndpoint) else { return }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(cloudSyncToken)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        do {
            let syncData = SyncData(
                expenses: expenses,
                budgets: budgets,
                deviceId: getDeviceId(),
                timestamp: Date()
            )
            
            request.httpBody = try JSONEncoder().encode(syncData)
            
            // In a real app, this would make the actual network call
            print("Syncing data to cloud with token: \(cloudSyncToken)")
            
        } catch {
            print("Cloud sync failed: \(error)")
        }
    }
    
    // MARK: - Encryption (Vulnerable Implementation)
    
    private func encryptData(_ data: Data) -> Data {
        // Simple XOR encryption with hardcoded key (VULNERABILITY)
        let keyData = encryptionKey.data(using: .utf8) ?? Data()
        var encrypted = Data()
        
        for (index, byte) in data.enumerated() {
            let keyByte = keyData[index % keyData.count]
            encrypted.append(byte ^ keyByte)
        }
        
        return encrypted
    }
    
    private func decryptData(_ data: Data) -> Data {
        // XOR decryption (same as encryption with XOR)
        return encryptData(data)
    }
    
    // MARK: - Helper Methods
    
    private func setupDefaultBudgets() {
        if budgets.isEmpty {
            let defaultBudgets = [
                Budget(category: .food, monthlyLimit: 500),
                Budget(category: .transportation, monthlyLimit: 200),
                Budget(category: .entertainment, monthlyLimit: 150),
                Budget(category: .bills, monthlyLimit: 800)
            ]
            budgets = defaultBudgets
            saveData()
        }
    }
    
    private func getDeviceId() -> String {
        // VULNERABILITY: Using a predictable device ID generation
        return "FT_DEVICE_\(UIDevice.current.identifierForVendor?.uuidString ?? "UNKNOWN")_\(encryptionKey.suffix(8))"
    }
    
    private var expensesURL: URL {
        documentsDirectory.appendingPathComponent("expenses.data")
    }
    
    private var budgetsURL: URL {
        documentsDirectory.appendingPathComponent("budgets.data")
    }
}

// MARK: - Supporting Types

struct SyncData: Codable {
    let expenses: [Expense]
    let budgets: [Budget]
    let deviceId: String
    let timestamp: Date
}
