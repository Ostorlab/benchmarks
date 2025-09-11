//
//  Document.swift
//  DocuShare Pro
//
//  Created by Ostorlab Ostorlab on 9/11/25.
//

import Foundation

struct Document {
    let id: UUID
    let title: String
    let url: URL
    let dateAdded: Date
    let fileType: String
    
    init(title: String, url: URL) {
        self.id = UUID()
        self.title = title
        self.url = url
        self.dateAdded = Date()
        
        if let pathExtension = url.pathExtension.isEmpty ? nil : url.pathExtension.lowercased() {
            switch pathExtension {
            case "pdf":
                self.fileType = "PDF Document"
            case "doc", "docx":
                self.fileType = "Word Document"
            case "txt":
                self.fileType = "Text Document"
            case "html", "htm":
                self.fileType = "HTML Document"
            default:
                self.fileType = "Document"
            }
        } else {
            self.fileType = "Document"
        }
    }
}