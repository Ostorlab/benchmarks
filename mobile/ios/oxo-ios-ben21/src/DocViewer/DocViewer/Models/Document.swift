import Foundation
import SwiftUI

struct Document: Identifiable, Codable {
    let id = UUID()
    let name: String
    let type: DocumentType
    let size: Int64
    let dateCreated: Date
    let dateModified: Date
    let author: String
    let url: URL?
    var isShared: Bool
    var tags: [String]
    var isFavorite: Bool
    
    var formattedSize: String {
        ByteCountFormatter.string(fromByteCount: size, countStyle: .file)
    }
    
    var formattedDate: String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: dateModified)
    }
    
    var iconName: String {
        switch type {
        case .pdf:
            return "doc.richtext"
        case .word:
            return "doc.text"
        case .excel:
            return "tablecells"
        case .powerpoint:
            return "play.rectangle"
        case .image:
            return "photo"
        case .text:
            return "doc.plaintext"
        case .html:
            return "globe"
        }
    }
    
    var iconColor: Color {
        switch type {
        case .pdf:
            return .red
        case .word:
            return .blue
        case .excel:
            return .green
        case .powerpoint:
            return .orange
        case .image:
            return .purple
        case .text:
            return .gray
        case .html:
            return .indigo
        }
    }
}

enum DocumentType: String, CaseIterable, Codable {
    case pdf = "pdf"
    case word = "docx"
    case excel = "xlsx"
    case powerpoint = "pptx"
    case image = "jpg"
    case text = "txt"
    case html = "html"
    
    var displayName: String {
        switch self {
        case .pdf:
            return "PDF Document"
        case .word:
            return "Word Document"
        case .excel:
            return "Excel Spreadsheet"
        case .powerpoint:
            return "PowerPoint Presentation"
        case .image:
            return "Image File"
        case .text:
            return "Text Document"
        case .html:
            return "Web Document"
        }
    }
}

struct CloudFolder: Identifiable, Codable {
    let id = UUID()
    let name: String
    let documentCount: Int
    let dateModified: Date
    var isShared: Bool
    
    var formattedDate: String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        return formatter.string(from: dateModified)
    }
}
