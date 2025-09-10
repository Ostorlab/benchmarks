//
//  Document.swift
//  DocumentViewerPro
//
//  Created by Ostorlab Ostorlab on 9/9/25.
//

import Foundation

struct Document {
    let id: UUID
    let title: String
    let content: String
    let dateCreated: Date
    let type: DocumentType
    
    init(title: String, content: String, type: DocumentType = .html) {
        self.id = UUID()
        self.title = title
        self.content = content
        self.dateCreated = Date()
        self.type = type
    }
}

enum DocumentType: String, CaseIterable {
    case html = "HTML"
    case text = "Text"
    case markdown = "Markdown"
    
    var fileExtension: String {
        switch self {
        case .html: return ".html"
        case .text: return ".txt"
        case .markdown: return ".md"
        }
    }
    
    var iconName: String {
        switch self {
        case .html: return "doc.richtext"
        case .text: return "doc.text"
        case .markdown: return "doc.plaintext"
        }
    }
}