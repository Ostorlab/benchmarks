//
//  DocumentListView.swift
//  DocViewer
//
//  Created by elyousfi on 11/09/2025.
//

import SwiftUI

struct DocumentListView: View {
    @EnvironmentObject var documentManager: DocumentManager
    @State private var showingFilters = false
    @State private var showingSortOptions = false
    @State private var selectedDocument: Document?
    @State private var showingAddDocument = false
    
    var body: some View {
        NavigationView {
            VStack {
                if documentManager.isLoading {
                    ProgressView("Loading documents...")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    List {
                        // Quick Stats
                        Section {
                            HStack {
                                VStack(alignment: .leading) {
                                    Text("\(documentManager.documents.count)")
                                        .font(.title2)
                                        .fontWeight(.bold)
                                    Text("Total Documents")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                                
                                Spacer()
                                
                                VStack(alignment: .trailing) {
                                    Text("\(documentManager.documents.filter { $0.isFavorite }.count)")
                                        .font(.title2)
                                        .fontWeight(.bold)
                                        .foregroundColor(.yellow)
                                    Text("Favorites")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                                
                                Spacer()
                                
                                VStack(alignment: .trailing) {
                                    Text("\(documentManager.documents.filter { $0.isShared }.count)")
                                        .font(.title2)
                                        .fontWeight(.bold)
                                        .foregroundColor(.green)
                                    Text("Shared")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                            }
                            .padding(.vertical, 8)
                        }
                        
                        // Recent Documents
                        if !documentManager.documents.isEmpty {
                            Section("Recent Documents") {
                                ForEach(documentManager.filteredDocuments.prefix(5), id: \.id) { document in
                                    DocumentRowView(document: document)
                                        .onTapGesture {
                                            selectedDocument = document
                                        }
                                        .contextMenu {
                                            Button(action: {
                                                documentManager.toggleFavorite(for: document)
                                            }) {
                                                Label(
                                                    document.isFavorite ? "Remove from Favorites" : "Add to Favorites",
                                                    systemImage: document.isFavorite ? "star.slash" : "star"
                                                )
                                            }
                                            
                                            Button(action: {
                                                // Share document
                                            }) {
                                                Label("Share", systemImage: "square.and.arrow.up")
                                            }
                                            
                                            Button(role: .destructive, action: {
                                                documentManager.deleteDocument(document)
                                            }) {
                                                Label("Delete", systemImage: "trash")
                                            }
                                        }
                                }
                            }
                        }
                        
                        // All Documents
                        if documentManager.filteredDocuments.count > 5 {
                            Section("All Documents") {
                                ForEach(documentManager.filteredDocuments.dropFirst(5), id: \.id) { document in
                                    DocumentRowView(document: document)
                                        .onTapGesture {
                                            selectedDocument = document
                                        }
                                        .contextMenu {
                                            Button(action: {
                                                documentManager.toggleFavorite(for: document)
                                            }) {
                                                Label(
                                                    document.isFavorite ? "Remove from Favorites" : "Add to Favorites",
                                                    systemImage: document.isFavorite ? "star.slash" : "star"
                                                )
                                            }
                                            
                                            Button(action: {
                                                // Share document
                                            }) {
                                                Label("Share", systemImage: "square.and.arrow.up")
                                            }
                                            
                                            Button(role: .destructive, action: {
                                                documentManager.deleteDocument(document)
                                            }) {
                                                Label("Delete", systemImage: "trash")
                                            }
                                        }
                                }
                            }
                        }
                    }
                    .refreshable {
                        documentManager.refreshDocuments()
                    }
                }
            }
            .navigationTitle("Documents")
            .navigationBarItems(
                leading: Button(action: {
                    showingFilters.toggle()
                }) {
                    Image(systemName: "line.horizontal.3.decrease.circle")
                },
                trailing: HStack {
                    Button(action: {
                        showingSortOptions.toggle()
                    }) {
                        Image(systemName: "arrow.up.arrow.down.circle")
                    }
                    
                    Button(action: {
                        showingAddDocument = true
                    }) {
                        Image(systemName: "plus.circle")
                    }
                }
            )
        }
        .sheet(item: $selectedDocument) { document in
            DocumentDetailView(document: document)
        }
        .actionSheet(isPresented: $showingSortOptions) {
            ActionSheet(
                title: Text("Sort Documents"),
                buttons: DocumentManager.SortOption.allCases.map { option in
                    .default(Text(option.rawValue)) {
                        documentManager.sortOption = option
                    }
                } + [.cancel()]
            )
        }
        .sheet(isPresented: $showingFilters) {
            FilterView()
                .environmentObject(documentManager)
        }
        .sheet(isPresented: $showingAddDocument) {
            AddDocumentView()
                .environmentObject(documentManager)
        }
    }
}

struct AddDocumentView: View {
    @EnvironmentObject var documentManager: DocumentManager
    @Environment(\.presentationMode) var presentationMode
    
    @State private var documentName = ""
    @State private var selectedType: DocumentType = .pdf
    @State private var author = ""
    @State private var tags = ""
    @State private var showingDocumentPicker = false
    @State private var showingImagePicker = false
    
    var body: some View {
        NavigationView {
            Form {
                Section("Document Details") {
                    TextField("Document Name", text: $documentName)
                    
                    Picker("Document Type", selection: $selectedType) {
                        ForEach(DocumentType.allCases, id: \.self) { type in
                            Text(type.displayName).tag(type)
                        }
                    }
                    
                    TextField("Author", text: $author)
                    
                    TextField("Tags (comma separated)", text: $tags)
                }
                
                Section("Import from Device") {
                    Button("Import Documents") {
                        showingDocumentPicker = true
                    }
                    .foregroundColor(.purple)
                    
                    Button("Import Photos") {
                        showingImagePicker = true
                    }
                    .foregroundColor(.orange)
                }
            }
            .navigationTitle("Add Document")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(
                leading: Button("Cancel") {
                    presentationMode.wrappedValue.dismiss()
                },
                trailing: Button("Create") {
                    createCustomDocument()
                    presentationMode.wrappedValue.dismiss()
                }
                .disabled(documentName.isEmpty)
            )
        }
        .sheet(isPresented: $showingDocumentPicker) {
            DocumentPicker { url in
                importDocument(from: url)
                presentationMode.wrappedValue.dismiss()
            }
        }
        .sheet(isPresented: $showingImagePicker) {
            ImagePicker { image in
                importImage(image)
                presentationMode.wrappedValue.dismiss()
            }
        }
        .onAppear {
            if let currentUser = AuthenticationManager.shared.currentUser {
                author = currentUser.fullName
            }
        }
    }
    

    
    private func createCustomDocument() {
        let document = Document(
            name: documentName,
            type: selectedType,
            size: Int64.random(in: 1024...1024*1024),
            dateCreated: Date(),
            dateModified: Date(),
            author: author.isEmpty ? "User" : author,
            url: nil,
            isShared: false,
            tags: tags.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) },
            isFavorite: false
        )
        documentManager.addDocument(document)
    }
    
    private func importDocument(from url: URL) {
        // Get file attributes
        let fileName = url.lastPathComponent
        let fileExtension = url.pathExtension.lowercased()
        
        // Determine document type from extension
        let docType: DocumentType
        switch fileExtension {
        case "pdf":
            docType = .pdf
        case "doc", "docx":
            docType = .word
        case "xls", "xlsx":
            docType = .excel
        case "ppt", "pptx":
            docType = .powerpoint
        case "txt":
            docType = .text
        case "html", "htm":
            docType = .html
        case "jpg", "jpeg", "png", "gif":
            docType = .image
        default:
            docType = .text
        }
        
        // Get file size
        var fileSize: Int64 = 0
        do {
            let attributes = try FileManager.default.attributesOfItem(atPath: url.path)
            fileSize = attributes[.size] as? Int64 ?? 0
        } catch {
            fileSize = Int64.random(in: 1024...5*1024*1024)
        }
        
        let document = Document(
            name: fileName,
            type: docType,
            size: fileSize,
            dateCreated: Date(),
            dateModified: Date(),
            author: author.isEmpty ? "User" : author,
            url: url,
            isShared: false,
            tags: ["imported"],
            isFavorite: false
        )
        documentManager.addDocument(document)
    }
    
    private func importImage(_ image: UIImage) {
        let fileName = "Image_\(Date().timeIntervalSince1970).jpg"
        
        // Estimate image size
        let imageData = image.jpegData(compressionQuality: 0.8) ?? Data()
        let fileSize = Int64(imageData.count)
        
        let document = Document(
            name: fileName,
            type: .image,
            size: fileSize,
            dateCreated: Date(),
            dateModified: Date(),
            author: author.isEmpty ? "User" : author,
            url: nil,
            isShared: false,
            tags: ["photo", "imported"],
            isFavorite: false
        )
        documentManager.addDocument(document)
    }
}

struct DocumentRowView: View {
    let document: Document
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: document.iconName)
                .font(.title2)
                .foregroundColor(document.iconColor)
                .frame(width: 40, height: 40)
                .background(document.iconColor.opacity(0.1))
                .cornerRadius(8)
            
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text(document.name)
                        .font(.headline)
                        .lineLimit(1)
                    
                    Spacer()
                    
                    if document.isFavorite {
                        Image(systemName: "star.fill")
                            .foregroundColor(.yellow)
                            .font(.caption)
                    }
                    
                    if document.isShared {
                        Image(systemName: "person.2.fill")
                            .foregroundColor(.green)
                            .font(.caption)
                    }
                }
                
                HStack {
                    Text(document.author)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    
                    Spacer()
                    
                    Text(document.formattedSize)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                HStack {
                    Text(document.formattedDate)
                        .font(.caption)
                        .foregroundColor(.secondary)
                    
                    Spacer()
                    
                    if !document.tags.isEmpty {
                        HStack(spacing: 4) {
                            ForEach(document.tags.prefix(2), id: \.self) { tag in
                                Text(tag)
                                    .font(.caption2)
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 2)
                                    .background(Color.blue.opacity(0.1))
                                    .foregroundColor(.blue)
                                    .cornerRadius(4)
                            }
                            if document.tags.count > 2 {
                                Text("+\(document.tags.count - 2)")
                                    .font(.caption2)
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                }
            }
        }
        .padding(.vertical, 4)
    }
}

import UniformTypeIdentifiers

struct DocumentPicker: UIViewControllerRepresentable {
    let onDocumentPicked: (URL) -> Void
    
    func makeUIViewController(context: Context) -> UIDocumentPickerViewController {
        let picker = UIDocumentPickerViewController(forOpeningContentTypes: [
            .pdf,
            .plainText,
            .html,
            .image,
            UTType(filenameExtension: "doc") ?? .data,
            UTType(filenameExtension: "docx") ?? .data,
            UTType(filenameExtension: "xls") ?? .data,
            UTType(filenameExtension: "xlsx") ?? .data,
            UTType(filenameExtension: "ppt") ?? .data,
            UTType(filenameExtension: "pptx") ?? .data
        ])
        
        picker.delegate = context.coordinator
        picker.allowsMultipleSelection = false
        
        return picker
    }
    
    func updateUIViewController(_ uiViewController: UIDocumentPickerViewController, context: Context) {}
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, UIDocumentPickerDelegate {
        let parent: DocumentPicker
        
        init(_ parent: DocumentPicker) {
            self.parent = parent
        }
        
        func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
            guard let url = urls.first else { return }
            parent.onDocumentPicked(url)
        }
    }
}

struct ImagePicker: UIViewControllerRepresentable {
    let onImagePicked: (UIImage) -> Void
    
    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        picker.sourceType = .photoLibrary
        return picker
    }
    
    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
        let parent: ImagePicker
        
        init(_ parent: ImagePicker) {
            self.parent = parent
        }
        
        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
            if let image = info[.originalImage] as? UIImage {
                parent.onImagePicked(image)
            }
            picker.dismiss(animated: true)
        }
    }
}
