//
//  DocumentStorage.swift
//  DocuShare Pro
//
//  Created by Ostorlab Ostorlab on 9/11/25.
//

import Foundation

class DocumentStorage {
    static let shared = DocumentStorage()
    
    private init() {
        loadSampleDocuments()
    }
    
    var documents: [Document] = []
    
    func addDocument(_ document: Document) {
        documents.append(document)
    }
    
    func removeDocument(at index: Int) {
        documents.remove(at: index)
    }
    
    private func loadSampleDocuments() {
        let sampleDocs = [
            Document(title: "Q3 Financial Report", url: URL(string: "https://docutech.com/reports/q3-2024.pdf")!),
            Document(title: "Employee Handbook", url: URL(string: "https://docutech.com/hr/handbook.pdf")!),
            Document(title: "Project Specifications", url: URL(string: "https://docutech.com/projects/spec.docx")!)
        ]
        documents.append(contentsOf: sampleDocs)
    }
}