import Foundation

struct User: Codable {
    let id: UUID
    let username: String
    let email: String
    let passwordHash: String
    let role: UserRole
    let createdDate: Date
    let lastLoginDate: Date?
    let isActive: Bool
}

enum UserRole: String, CaseIterable, Codable {
    case admin = "Administrator"
    case manager = "Manager"
    case user = "User"
    case guest = "Guest"
    
    var permissions: [Permission] {
        switch self {
        case .admin:
            return Permission.allCases
        case .manager:
            return [.read, .write, .share, .backup]
        case .user:
            return [.read, .write, .share]
        case .guest:
            return [.read]
        }
    }
}

enum Permission: String, CaseIterable {
    case read = "Read"
    case write = "Write"
    case delete = "Delete"
    case share = "Share"
    case backup = "Backup"
    case admin = "Admin"
}
