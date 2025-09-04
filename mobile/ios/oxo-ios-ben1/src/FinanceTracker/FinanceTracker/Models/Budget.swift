//
//  Budget.swift
//  FinanceTracker
//
//  Created by Alaeddine Mesbahi on 9/4/25.
//

import Foundation

struct Budget: Identifiable, Codable {
    let id = UUID()
    var category: ExpenseCategory
    var monthlyLimit: Double
    var spent: Double
    var currency: String
    
    init(category: ExpenseCategory, monthlyLimit: Double, currency: String = "USD") {
        self.category = category
        self.monthlyLimit = monthlyLimit
        self.spent = 0.0
        self.currency = currency
    }
    
    var remaining: Double {
        return monthlyLimit - spent
    }
    
    var percentageUsed: Double {
        guard monthlyLimit > 0 else { return 0 }
        return min(spent / monthlyLimit, 1.0)
    }
    
    var isOverBudget: Bool {
        return spent > monthlyLimit
    }
}
