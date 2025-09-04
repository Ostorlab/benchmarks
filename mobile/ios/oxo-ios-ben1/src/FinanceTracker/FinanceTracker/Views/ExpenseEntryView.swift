//
//  ExpenseEntryView.swift
//  FinanceTracker
//
//  Created by Alaeddine Mesbahi on 9/4/25.
//

import SwiftUI

struct ExpenseEntryView: View {
    @EnvironmentObject var dataManager: DataManager
    @EnvironmentObject var currencyService: CurrencyService
    
    @State private var amount: String = ""
    @State private var description: String = ""
    @State private var selectedCategory: ExpenseCategory = .food
    @State private var selectedDate = Date()
    @State private var selectedCurrency = "USD"
    @State private var showingSuccessAlert = false
    @State private var showingErrorAlert = false
    @State private var errorMessage = ""
    
    private let supportedCurrencies = ["USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF"]
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Expense Details")) {
                    // Amount Input
                    HStack {
                        Text("Amount")
                        Spacer()
                        TextField("0.00", text: $amount)
                            .keyboardType(.decimalPad)
                            .multilineTextAlignment(.trailing)
                            .font(.headline)
                    }
                    
                    // Currency Selector
                    HStack {
                        Text("Currency")
                        Spacer()
                        Picker("Currency", selection: $selectedCurrency) {
                            ForEach(supportedCurrencies, id: \.self) { currency in
                                Text(currency).tag(currency)
                            }
                        }
                        .pickerStyle(MenuPickerStyle())
                    }
                    
                    // Description
                    HStack {
                        Text("Description")
                        TextField("What did you buy?", text: $description)
                            .multilineTextAlignment(.trailing)
                    }
                    
                    // Date Picker
                    DatePicker("Date", selection: $selectedDate, displayedComponents: [.date])
                }
                
                Section(header: Text("Category")) {
                    LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 2), spacing: 12) {
                        ForEach(ExpenseCategory.allCases, id: \.self) { category in
                            CategorySelectionCard(
                                category: category,
                                isSelected: selectedCategory == category
                            ) {
                                selectedCategory = category
                            }
                        }
                    }
                    .padding(.vertical, 8)
                }
                
                Section(header: Text("Recent Expenses")) {
                    ForEach(recentExpenses.prefix(3)) { expense in
                        ExpenseRowView(expense: expense)
                    }
                }
                
                Section {
                    Button(action: addExpense) {
                        HStack {
                            Spacer()
                            if currencyService.isLoading {
                                ProgressView()
                                    .scaleEffect(0.8)
                                Text("Converting...")
                                    .fontWeight(.semibold)
                            } else {
                                Image(systemName: "plus.circle.fill")
                                Text("Add Expense")
                                    .fontWeight(.semibold)
                            }
                            Spacer()
                        }
                        .foregroundColor(.white)
                        .padding()
                        .background(isValidInput ? Color.blue : Color.gray)
                        .cornerRadius(10)
                    }
                    .disabled(!isValidInput || currencyService.isLoading)
                    .listRowBackground(Color.clear)
                }
            }
            .navigationTitle("Add Expense")
            .alert("Expense Added", isPresented: $showingSuccessAlert) {
                Button("OK") { clearForm() }
            } message: {
                Text("Your expense has been added successfully!")
            }
            .alert("Error", isPresented: $showingErrorAlert) {
                Button("OK") { }
            } message: {
                Text(errorMessage)
            }
        }
    }
    
    private var isValidInput: Bool {
        guard let amountValue = Double(amount), amountValue > 0 else { return false }
        return !description.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }
    
    private var recentExpenses: [Expense] {
        dataManager.expenses
            .sorted(by: { $0.date > $1.date })
    }
    
    private func addExpense() {
        guard let amountValue = Double(amount) else {
            showError("Please enter a valid amount")
            return
        }
        
        Task {
            do {
                // Convert currency if needed
                var finalAmount = amountValue
                if selectedCurrency != "USD" {
                    finalAmount = try await currencyService.convertCurrency(
                        from: selectedCurrency,
                        to: "USD",
                        amount: amountValue
                    )
                }
                
                // Create and add expense
                let expense = Expense(
                    amount: finalAmount,
                    description: description.trimmingCharacters(in: .whitespacesAndNewlines),
                    category: selectedCategory,
                    date: selectedDate,
                    currency: "USD" // Store everything in USD after conversion
                )
                
                await MainActor.run {
                    dataManager.addExpense(expense)
                    showingSuccessAlert = true
                }
                
            } catch {
                await MainActor.run {
                    showError("Failed to convert currency: \(error.localizedDescription)")
                }
            }
        }
    }
    
    private func clearForm() {
        amount = ""
        description = ""
        selectedCategory = .food
        selectedDate = Date()
        selectedCurrency = "USD"
    }
    
    private func showError(_ message: String) {
        errorMessage = message
        showingErrorAlert = true
    }
}

struct CategorySelectionCard: View {
    let category: ExpenseCategory
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(spacing: 8) {
                Circle()
                    .fill(isSelected ? category.color : category.color.opacity(0.3))
                    .frame(width: 40, height: 40)
                    .overlay(
                        Image(systemName: category.icon)
                            .foregroundColor(isSelected ? .white : category.color)
                            .font(.title3)
                    )
                
                Text(category.rawValue)
                    .font(.caption)
                    .foregroundColor(.primary)
                    .multilineTextAlignment(.center)
                    .lineLimit(2)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 12)
            .background(
                RoundedRectangle(cornerRadius: 8)
                    .fill(isSelected ? category.color.opacity(0.1) : Color(.systemBackground))
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(isSelected ? category.color : Color.clear, lineWidth: 2)
                    )
            )
        }
        .buttonStyle(PlainButtonStyle())
    }
}

struct ExpenseRowView: View {
    let expense: Expense
    
    var body: some View {
        HStack(spacing: 12) {
            Circle()
                .fill(expense.category.color.opacity(0.2))
                .frame(width: 32, height: 32)
                .overlay(
                    Image(systemName: expense.category.icon)
                        .foregroundColor(expense.category.color)
                        .font(.caption)
                )
            
            VStack(alignment: .leading, spacing: 2) {
                Text(expense.description)
                    .font(.subheadline)
                    .foregroundColor(.primary)
                    .lineLimit(1)
                
                Text(expense.category.rawValue)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            VStack(alignment: .trailing, spacing: 2) {
                Text("$\(expense.amount, specifier: "%.2f")")
                    .font(.subheadline)
                    .fontWeight(.medium)
                    .foregroundColor(.primary)
                
                Text(expense.date, style: .date)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.vertical, 4)
    }
}

#Preview {
    ExpenseEntryView()
        .environmentObject(DataManager())
        .environmentObject(CurrencyService())
}
