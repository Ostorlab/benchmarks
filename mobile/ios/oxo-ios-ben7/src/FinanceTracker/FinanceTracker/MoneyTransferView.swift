//
//  MoneyTransferView.swift
//  FinanceTracker
//
//  Created by Alaeddine Mesbahi on 9/8/25.
//

import SwiftUI

struct MoneyTransferView: View {
    @EnvironmentObject var dataManager: DataManager
    @State private var amount: String = ""
    @State private var description: String = ""
    @State private var fromAccount: String = ""
    @State private var toAccount: String = ""
    @State private var showingAlert = false
    @State private var alertMessage = ""
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                // Accounts Overview
                VStack(alignment: .leading, spacing: 12) {
                    Text("Accounts")
                        .font(.headline)
                        .padding(.horizontal)
                    
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 12) {
                            ForEach(dataManager.accounts) { account in
                                VStack(alignment: .leading, spacing: 8) {
                                    HStack {
                                        Image(systemName: account.accountType.icon)
                                            .foregroundColor(.blue)
                                        Text(account.name)
                                            .font(.subheadline)
                                            .fontWeight(.medium)
                                    }
                                    
                                    Text("$\(account.balance, specifier: "%.2f")")
                                        .font(.title2)
                                        .fontWeight(.bold)
                                        .foregroundColor(account.balance >= 0 ? .primary : .red)
                                }
                                .padding()
                                .background(Color(.systemGray6))
                                .cornerRadius(12)
                                .frame(width: 160)
                            }
                        }
                        .padding(.horizontal)
                    }
                }
                
                // Transfer Form
                Form {
                    Section("Transfer Details") {
                        Picker("From Account", selection: $fromAccount) {
                            Text("Select Account").tag("")
                            ForEach(dataManager.accounts, id: \.name) { account in
                                Text(account.name).tag(account.name)
                            }
                        }
                        .pickerStyle(MenuPickerStyle())
                        
                        Picker("To Account", selection: $toAccount) {
                            Text("Select Account").tag("")
                            ForEach(dataManager.accounts, id: \.name) { account in
                                if account.name != fromAccount {
                                    Text(account.name).tag(account.name)
                                }
                            }
                        }
                        .pickerStyle(MenuPickerStyle())
                        
                        HStack {
                            Text("$")
                                .font(.title2)
                                .foregroundColor(.secondary)
                            TextField("0.00", text: $amount)
                                .keyboardType(.decimalPad)
                                .font(.title2)
                        }
                        
                        TextField("Description (optional)", text: $description)
                    }
                    
                    Section {
                        Button(action: processTransfer) {
                            HStack {
                                Spacer()
                                Text("Transfer Money")
                                    .fontWeight(.semibold)
                                Spacer()
                            }
                        }
                        .disabled(amount.isEmpty || fromAccount.isEmpty || toAccount.isEmpty || fromAccount == toAccount)
                    }
                }
                
                // Recent Transfers
                if !dataManager.transfers.isEmpty {
                    VStack(alignment: .leading) {
                        Text("Recent Transfers")
                            .font(.headline)
                            .padding(.horizontal)
                        
                        List {
                            ForEach(dataManager.transfers.prefix(5)) { transfer in
                                VStack(alignment: .leading, spacing: 4) {
                                    HStack {
                                        Text("\(transfer.fromAccount) → \(transfer.toAccount)")
                                            .font(.subheadline)
                                            .fontWeight(.medium)
                                        
                                        Spacer()
                                        
                                        Text("$\(transfer.amount, specifier: "%.2f")")
                                            .font(.subheadline)
                                            .fontWeight(.medium)
                                            .foregroundColor(.blue)
                                    }
                                    
                                    if !transfer.description.isEmpty {
                                        Text(transfer.description)
                                            .font(.caption)
                                            .foregroundColor(.secondary)
                                    }
                                    
                                    Text(transfer.date, style: .date)
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                                .padding(.vertical, 2)
                            }
                        }
                        .frame(maxHeight: 200)
                    }
                }
                
                Spacer()
            }
            .navigationTitle("Money Transfer")
            .alert("Message", isPresented: $showingAlert) {
                Button("OK") { }
            } message: {
                Text(alertMessage)
            }
        }
    }
    
    private func processTransfer() {
        guard let amountValue = Double(amount), amountValue > 0 else {
            alertMessage = "Please enter a valid amount"
            showingAlert = true
            return
        }
        
        guard !fromAccount.isEmpty && !toAccount.isEmpty else {
            alertMessage = "Please select both accounts"
            showingAlert = true
            return
        }
        
        guard fromAccount != toAccount else {
            alertMessage = "Cannot transfer to the same account"
            showingAlert = true
            return
        }
        
        // Check if source account has sufficient funds
        if let sourceAccount = dataManager.accounts.first(where: { $0.name == fromAccount }) {
            if sourceAccount.balance < amountValue {
                alertMessage = "Insufficient funds in \(fromAccount)"
                showingAlert = true
                return
            }
        }
        
        let newTransfer = Transfer(
            fromAccount: fromAccount,
            toAccount: toAccount,
            amount: amountValue,
            description: description.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? 
                        "Transfer from \(fromAccount) to \(toAccount)" : description.trimmingCharacters(in: .whitespacesAndNewlines)
        )
        
        dataManager.addTransfer(newTransfer)
        
        // Clear form
        amount = ""
        description = ""
        fromAccount = ""
        toAccount = ""
        
        alertMessage = "Transfer completed successfully!"
        showingAlert = true
    }
}

#Preview {
    MoneyTransferView()
        .environmentObject(DataManager())
}
