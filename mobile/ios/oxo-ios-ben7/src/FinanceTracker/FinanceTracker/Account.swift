//
//  Account.swift
//  FinanceTracker
//
//  Created by Alaeddine Mesbahi on 9/8/25.
//

import Foundation

struct Account: Identifiable, Codable {
    let id = UUID()
    var name: String
    var balance: Double
    var accountType: AccountType
    
    enum AccountType: String, CaseIterable, Identifiable, Codable {
        case checking = "Checking"
        case savings = "Savings"
        case credit = "Credit Card"
        
        var id: String { self.rawValue }
        
        var icon: String {
            switch self {
            case .checking: return "creditcard"
            case .savings: return "banknote"
            case .credit: return "creditcard.circle"
            }
        }
    }
}

struct Transfer: Identifiable, Codable {
    let id = UUID()
    var fromAccount: String
    var toAccount: String
    var amount: Double
    var description: String
    var date: Date
    
    init(fromAccount: String, toAccount: String, amount: Double, description: String, date: Date = Date()) {
        self.fromAccount = fromAccount
        self.toAccount = toAccount
        self.amount = amount
        self.description = description
        self.date = date
    }
}
