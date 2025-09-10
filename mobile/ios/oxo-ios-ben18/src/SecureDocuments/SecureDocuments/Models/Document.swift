import Foundation

struct Document: Identifiable, Codable {
    let id = UUID()
    let name: String
    let fileExtension: String
    let size: Int64
    let createdDate: Date
    let modifiedDate: Date
    let hash: String
    let digitalSignature: String?
    let category: DocumentCategory
    let isShared: Bool
    let shareToken: String?
    
    var fileName: String {
        return "\(name).\(fileExtension)"
    }
    
    var formattedSize: String {
        let formatter = ByteCountFormatter()
        formatter.allowedUnits = [.useKB, .useMB, .useGB]
        return formatter.string(fromByteCount: size)
    }
}

enum DocumentCategory: String, CaseIterable, Codable {
    case legal = "Legal"
    case medical = "Medical"
    case financial = "Financial"
    case corporate = "Corporate"
    case personal = "Personal"
    
    var icon: String {
        switch self {
        case .legal: return "hammer.fill"
        case .medical: return "cross.fill"
        case .financial: return "dollarsign.circle.fill"
        case .corporate: return "building.2.fill"
        case .personal: return "person.fill"
        }
    }
}
