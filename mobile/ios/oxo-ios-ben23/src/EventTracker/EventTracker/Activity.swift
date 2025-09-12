import Foundation
import SwiftUI

struct Activity: Identifiable, Codable {
    let id = UUID()
    let title: String
    let category: ActivityCategory
    let duration: Double // in hours
    let timestamp: Date
    let notes: String
    let location: String?
    
    init(title: String, category: ActivityCategory, duration: Double, notes: String = "", location: String? = nil) {
        self.title = title
        self.category = category
        self.duration = duration
        self.timestamp = Date()
        self.notes = notes
        self.location = location
    }
}

enum ActivityCategory: String, CaseIterable, Codable {
    case work = "Work"
    case exercise = "Exercise"
    case meals = "Meals"
    case social = "Social"
    case learning = "Learning"
    case entertainment = "Entertainment"
    case health = "Health"
    case travel = "Travel"
    
    var color: Color {
        switch self {
        case .work: return .blue
        case .exercise: return .green
        case .meals: return .orange
        case .social: return .purple
        case .learning: return .red
        case .entertainment: return .pink
        case .health: return .mint
        case .travel: return .cyan
        }
    }
    
    var icon: String {
        switch self {
        case .work: return "briefcase.fill"
        case .exercise: return "figure.run"
        case .meals: return "fork.knife"
        case .social: return "person.2.fill"
        case .learning: return "book.fill"
        case .entertainment: return "tv.fill"
        case .health: return "heart.fill"
        case .travel: return "car.fill"
        }
    }
}
