//
//  DocumentManager.swift
//  DocumentViewerPro
//
//  Created by Ostorlab Ostorlab on 9/9/25.
//

import Foundation

class DocumentManager {
    static let shared = DocumentManager()
    
    private var documents: [Document] = []
    
    private init() {
        loadSampleDocuments()
    }
    
    func getAllDocuments() -> [Document] {
        return documents.sorted { $0.dateCreated > $1.dateCreated }
    }
    
    func addDocument(_ document: Document) {
        documents.append(document)
    }
    
    func deleteDocument(withId id: UUID) {
        documents.removeAll { $0.id == id }
    }
    
    func getDocument(withId id: UUID) -> Document? {
        return documents.first { $0.id == id }
    }
    
    private func loadSampleDocuments() {
        let welcomeHTML = """
        <html>
        <head><title>Welcome to DocumentViewerPro</title></head>
        <body>
            <h1>Welcome to DocumentViewerPro</h1>
            <p>Your professional document management solution.</p>
            <h2>Features:</h2>
            <ul>
                <li>View HTML documents</li>
                <li>Import content from URLs</li>
                <li>Organize your documents</li>
                <li>Professional document viewer</li>
            </ul>
        </body>
        </html>
        """
        
        let userGuideHTML = """
        <html>
        <head><title>User Guide</title></head>
        <body>
            <h1>DocumentViewerPro User Guide</h1>
            <h2>Getting Started</h2>
            <p>To add new documents, tap the + button in the top right corner.</p>
            <p>You can import content from URLs or paste HTML directly.</p>
            <h2>Viewing Documents</h2>
            <p>Tap any document in the list to view it in our built-in viewer.</p>
            <p>The viewer supports rich HTML content including:</p>
            <ul>
                <li>Text formatting</li>
                <li>Images and media</li>
                <li>Links and interactive content</li>
            </ul>
        </body>
        </html>
        """
        
        documents = [
            Document(title: "Welcome Guide", content: welcomeHTML, type: .html),
            Document(title: "User Manual", content: userGuideHTML, type: .html)
        ]
    }
}