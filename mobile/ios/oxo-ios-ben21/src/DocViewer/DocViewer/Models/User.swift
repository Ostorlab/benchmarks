//
//  User.swift
//  DocViewer
//
//  Created by elyousfi on 11/09/2025.
//

import Foundation

struct User: Identifiable, Codable {
    let id: UUID
    let email: String
    let fullName: String
    let company: String
    let plan: SubscriptionPlan
    let storageUsed: Int64
    let storageLimit: Int64
    let dateJoined: Date
    var profileImageURL: URL?
    
    var storageUsedPercentage: Double {
        return Double(storageUsed) / Double(storageLimit)
    }
    
    var formattedStorageUsed: String {
        ByteCountFormatter.string(fromByteCount: storageUsed, countStyle: .file)
    }
    
    var formattedStorageLimit: String {
        ByteCountFormatter.string(fromByteCount: storageLimit, countStyle: .file)
    }
}

enum SubscriptionPlan: String, CaseIterable, Codable {
    case free = "free"
    case professional = "professional"
    case enterprise = "enterprise"
    
    var displayName: String {
        switch self {
        case .free:
            return "Free"
        case .professional:
            return "Professional"
        case .enterprise:
            return "Enterprise"
        }
    }
    
    var storageLimit: Int64 {
        switch self {
        case .free:
            return 2_000_000_000 // 2GB
        case .professional:
            return 100_000_000_000 // 100GB
        case .enterprise:
            return 1_000_000_000_000 // 1TB
        }
    }
}
