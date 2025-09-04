import Foundation
import SwiftUI

struct Expense: Identifiable, Codable {
    let id = UUID()
    var amount: Double
    var description: String
    var category: ExpenseCategory
    var date: Date
    var currency: String
    
    init(amount: Double, description: String, category: ExpenseCategory, date: Date = Date(), currency: String = "USD") {
        self.amount = amount
        self.description = description
        self.category = category
        self.date = date
        self.currency = currency
    }
}

enum ExpenseCategory: String, CaseIterable, Codable {
    case food = "Food & Dining"
    case transportation = "Transportation"
    case shopping = "Shopping"
    case entertainment = "Entertainment"
    case bills = "Bills & Utilities"
    case healthcare = "Healthcare"
    case travel = "Travel"
    case other = "Other"
    
    var icon: String {
        switch self {
        case .food: return "fork.knife"
        case .transportation: return "car.fill"
        case .shopping: return "bag.fill"
        case .entertainment: return "gamecontroller.fill"
        case .bills: return "doc.text.fill"
        case .healthcare: return "cross.fill"
        case .travel: return "airplane"
        case .other: return "ellipsis.circle.fill"
        }
    }
    
    var color: Color {
        switch self {
        case .food: return .orange
        case .transportation: return .blue
        case .shopping: return .purple
        case .entertainment: return .pink
        case .bills: return .red
        case .healthcare: return .green
        case .travel: return .cyan
        case .other: return .gray
        }
    }
}
