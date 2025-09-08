//
//  ExpenseEntryView.swift
//  FinanceTracker
//
//  Created by Alaeddine Mesbahi on 9/8/25.
//

import SwiftUI

struct ExpenseEntryView: View {
    @EnvironmentObject var dataManager: DataManager
    @State private var amount: String = ""
    @State private var description: String = ""
    @State private var selectedCategory: ExpenseCategory = .food
    @State private var showingAlert = false
    @State private var alertMessage = ""
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                // Summary Card
                VStack(spacing: 8) {
                    Text("Total Expenses")
                        .font(.headline)
                        .foregroundColor(.secondary)
                    
                    Text("$\(dataManager.totalExpenses, specifier: "%.2f")")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                    
                    Text("Total Balance: $\(dataManager.totalBalance, specifier: "%.2f")")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(12)
                .padding(.horizontal)
                
                // Add Expense Form
                Form {
                    Section("New Expense") {
                        HStack {
                            Text("$")
                                .font(.title2)
                                .foregroundColor(.secondary)
                            TextField("0.00", text: $amount)
                                .keyboardType(.decimalPad)
                                .font(.title2)
                        }
                        
                        TextField("Description", text: $description)
                        
                        Picker("Category", selection: $selectedCategory) {
                            ForEach(ExpenseCategory.allCases) { category in
                                HStack {
                                    Image(systemName: category.icon)
                                    Text(category.rawValue)
                                }
                                .tag(category)
                            }
                        }
                        .pickerStyle(MenuPickerStyle())
                    }
                    
                    Section {
                        Button(action: addExpense) {
                            HStack {
                                Spacer()
                                Text("Add Expense")
                                    .fontWeight(.semibold)
                                Spacer()
                            }
                        }
                        .disabled(amount.isEmpty || description.isEmpty)
                    }
                }
                
                // Recent Expenses
                if !dataManager.expenses.isEmpty {
                    VStack(alignment: .leading) {
                        Text("Recent Expenses")
                            .font(.headline)
                            .padding(.horizontal)
                        
                        List {
                            ForEach(dataManager.expenses.prefix(5)) { expense in
                                HStack {
                                    Image(systemName: expense.category.icon)
                                        .foregroundColor(.blue)
                                        .frame(width: 24)
                                    
                                    VStack(alignment: .leading) {
                                        Text(expense.description)
                                            .font(.subheadline)
                                        Text(expense.category.rawValue)
                                            .font(.caption)
                                            .foregroundColor(.secondary)
                                    }
                                    
                                    Spacer()
                                    
                                    Text("$\(expense.amount, specifier: "%.2f")")
                                        .font(.subheadline)
                                        .fontWeight(.medium)
                                }
                                .padding(.vertical, 2)
                            }
                            .onDelete(perform: dataManager.deleteExpense)
                        }
                        .frame(maxHeight: 200)
                    }
                }
                
                Spacer()
            }
            .navigationTitle("Finance Tracker")
            .alert("Message", isPresented: $showingAlert) {
                Button("OK") { }
            } message: {
                Text(alertMessage)
            }
        }
    }
    
    private func addExpense() {
        guard let amountValue = Double(amount), amountValue > 0 else {
            alertMessage = "Please enter a valid amount"
            showingAlert = true
            return
        }
        
        guard !description.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            alertMessage = "Please enter a description"
            showingAlert = true
            return
        }
        
        let newExpense = Expense(
            amount: amountValue,
            description: description.trimmingCharacters(in: .whitespacesAndNewlines),
            category: selectedCategory
        )
        
        dataManager.addExpense(newExpense)
        
        // Clear form
        amount = ""
        description = ""
        selectedCategory = .food
        
        alertMessage = "Expense added successfully!"
        showingAlert = true
    }
}

#Preview {
    ExpenseEntryView()
        .environmentObject(DataManager())
}
