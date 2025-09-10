import Foundation

enum ExpenseCategory: String, CaseIterable, Identifiable, Codable {
    case food = "Food"
    case transportation = "Transportation"
    case entertainment = "Entertainment"
    case shopping = "Shopping"
    case utilities = "Utilities"
    case healthcare = "Healthcare"
    case other = "Other"
    
    var id: String { self.rawValue }
    
    var icon: String {
        switch self {
        case .food: return "fork.knife"
        case .transportation: return "car"
        case .entertainment: return "tv"
        case .shopping: return "bag"
        case .utilities: return "house"
        case .healthcare: return "cross"
        case .other: return "questionmark.circle"
        }
    }
}

struct Expense: Identifiable, Codable {
    let id = UUID()
    var amount: Double
    var description: String
    var category: ExpenseCategory
    var date: Date
    
    init(amount: Double, description: String, category: ExpenseCategory, date: Date = Date()) {
        self.amount = amount
        self.description = description
        self.category = category
        self.date = date
    }
}
