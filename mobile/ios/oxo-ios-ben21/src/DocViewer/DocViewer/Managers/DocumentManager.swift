//
//  DocumentManager.swift
//  DocViewer
//
//  Created by elyousfi on 11/09/2025.
//

import Foundation
import Combine

class DocumentManager: ObservableObject {
    static let shared = DocumentManager()
    
    @Published var documents: [Document] = []
    @Published var folders: [CloudFolder] = []
    @Published var isLoading = false
    @Published var searchText = ""
    @Published var selectedTags: Set<String> = []
    @Published var sortOption: SortOption = .dateModified
    
    private let documentsKey = "saved_documents"
    private let foldersKey = "saved_folders"
    
    enum SortOption: String, CaseIterable {
        case name = "Name"
        case dateModified = "Date Modified"
        case dateCreated = "Date Created"
        case size = "Size"
        case type = "Type"
    }
    
    var filteredDocuments: [Document] {
        var filtered = documents
        
        if !searchText.isEmpty {
            filtered = filtered.filter { doc in
                doc.name.localizedCaseInsensitiveContains(searchText) ||
                doc.author.localizedCaseInsensitiveContains(searchText) ||
                doc.tags.joined().localizedCaseInsensitiveContains(searchText)
            }
        }
        
        if !selectedTags.isEmpty {
            filtered = filtered.filter { doc in
                !Set(doc.tags).isDisjoint(with: selectedTags)
            }
        }
        
        switch sortOption {
        case .name:
            filtered.sort { $0.name < $1.name }
        case .dateModified:
            filtered.sort { $0.dateModified > $1.dateModified }
        case .dateCreated:
            filtered.sort { $0.dateCreated > $1.dateCreated }
        case .size:
            filtered.sort { $0.size > $1.size }
        case .type:
            filtered.sort { $0.type.rawValue < $1.type.rawValue }
        }
        
        return filtered
    }
    
    var allTags: [String] {
        Array(Set(documents.flatMap { $0.tags })).sorted()
    }
    
    private init() {
        loadDocuments()
        loadFolders()
    }
    
    func loadDocuments() {
        if let data = UserDefaults.standard.data(forKey: documentsKey),
           let decoded = try? JSONDecoder().decode([Document].self, from: data) {
            documents = decoded
        }
    }
    
    func loadFolders() {
        if let data = UserDefaults.standard.data(forKey: foldersKey),
           let decoded = try? JSONDecoder().decode([CloudFolder].self, from: data) {
            folders = decoded
        }
    }
    
    func saveDocuments() {
        if let encoded = try? JSONEncoder().encode(documents) {
            UserDefaults.standard.set(encoded, forKey: documentsKey)
        }
    }
    
    func saveFolders() {
        if let encoded = try? JSONEncoder().encode(folders) {
            UserDefaults.standard.set(encoded, forKey: foldersKey)
        }
    }
    
    func addDocument(_ document: Document) {
        documents.append(document)
        saveDocuments()
    }
    
    func deleteDocument(_ document: Document) {
        documents.removeAll { $0.id == document.id }
        saveDocuments()
    }
    
    func toggleFavorite(for document: Document) {
        if let index = documents.firstIndex(where: { $0.id == document.id }) {
            documents[index].isFavorite.toggle()
            saveDocuments()
        }
    }
    
    func refreshDocuments() {
        isLoading = true
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            self.isLoading = false
        }
    }
    

}
