import SwiftUI
import UniformTypeIdentifiers

struct DocumentListView: View {
    @EnvironmentObject var documentManager: DocumentManager
    @EnvironmentObject var authService: AuthenticationService
    @State private var selectedCategory: DocumentCategory? = nil
    @State private var showingAddDocument = false
    @State private var showingDocumentPicker = false
    @State private var searchText = ""
    @State private var shareItem: String?
    @State private var showingShareSheet = false
    
    var filteredDocuments: [Document] {
        var documents = documentManager.documents
        
        if let category = selectedCategory {
            documents = documents.filter { $0.category == category }
        }
        
        if !searchText.isEmpty {
            documents = documents.filter { 
                $0.name.localizedCaseInsensitiveContains(searchText) ||
                $0.fileExtension.localizedCaseInsensitiveContains(searchText)
            }
        }
        
        return documents.sorted { $0.modifiedDate > $1.modifiedDate }
    }
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                SearchBar(text: $searchText)
                    .padding(.horizontal)
                    .padding(.top, 10)
                
                CategoryFilterView(selectedCategory: $selectedCategory)
                    .padding(.horizontal)
                
                if documentManager.isLoading {
                    Spacer()
                    ProgressView("Loading documents...")
                    Spacer()
                } else if filteredDocuments.isEmpty {
                    Spacer()
                    VStack(spacing: 20) {
                        Image(systemName: "doc.text")
                            .font(.system(size: 50))
                            .foregroundColor(.gray)
                        Text("No documents found")
                            .font(.headline)
                            .foregroundColor(.gray)
                        Text(searchText.isEmpty ? "Add your first document to get started" : "Try adjusting your search criteria")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .padding()
                    Spacer()
                } else {
                    List {
                        ForEach(filteredDocuments) { document in
                            DocumentRowView(document: document)
                                .swipeActions(edge: .trailing, allowsFullSwipe: false) {
                                    Button(role: .destructive) {
                                        deleteDocument(document)
                                    } label: {
                                        Label("Delete", systemImage: "trash")
                                    }
                                    
                                    Button {
                                        shareDocument(document)
                                    } label: {
                                        Label("Share", systemImage: "square.and.arrow.up")
                                    }
                                    .tint(.blue)
                                }
                        }
                    }
                    .listStyle(PlainListStyle())
                }
            }
            .navigationTitle("Documents")
            .navigationBarItems(
                leading: welcomeText,
                trailing: HStack {
                    Button(action: { showingDocumentPicker = true }) {
                        Image(systemName: "doc.badge.plus")
                            .font(.title2)
                    }
                    Button(action: { showingAddDocument = true }) {
                        Image(systemName: "plus")
                            .font(.title2)
                    }
                }
            )
            .sheet(isPresented: $showingAddDocument) {
                AddDocumentView()
            }
            .sheet(isPresented: $showingDocumentPicker) {
                DocumentPickerView { url in
                    importDocument(from: url)
                }
            }
            .sheet(isPresented: $showingShareSheet) {
                if let shareItem = shareItem {
                    ActivityViewController(activityItems: [shareItem])
                }
            }
        }
    }
    
    private var welcomeText: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text("Welcome,")
                .font(.caption)
                .foregroundColor(.secondary)
            Text(authService.currentUser?.username.capitalized ?? "")
                .font(.headline)
                .fontWeight(.semibold)
        }
    }
    
    private var addButton: some View {
        Button(action: { showingAddDocument = true }) {
            Image(systemName: "plus")
                .font(.title2)
        }
    }
    
    private func importDocument(from url: URL) {
        guard url.startAccessingSecurityScopedResource() else { return }
        defer { url.stopAccessingSecurityScopedResource() }
        
        do {
            let data = try Data(contentsOf: url)
            let fileName = url.deletingPathExtension().lastPathComponent
            let fileExtension = url.pathExtension
            
            let category: DocumentCategory
            switch fileExtension.lowercased() {
            case "pdf", "doc", "docx":
                category = .legal
            case "jpg", "jpeg", "png":
                category = .medical
            case "xls", "xlsx", "csv":
                category = .financial
            default:
                category = .corporate
            }
            
            documentManager.addDocument(
                name: fileName,
                extension: fileExtension,
                data: data,
                category: category
            )
        } catch {
            print("Failed to import document: \(error)")
        }
    }
    
    private func deleteDocument(_ document: Document) {
        documentManager.deleteDocument(document)
    }
    
    private func shareDocument(_ document: Document) {
        let shareLink = documentManager.shareDocument(document)
        shareItem = shareLink
        showingShareSheet = true
    }
}

struct DocumentPickerView: UIViewControllerRepresentable {
    let onDocumentPicked: (URL) -> Void
    
    func makeUIViewController(context: Context) -> UIDocumentPickerViewController {
        let picker = UIDocumentPickerViewController(forOpeningContentTypes: [
            UTType.pdf,
            UTType.plainText,
            UTType.image,
            UTType.spreadsheet,
            UTType.presentation,
            .init("com.microsoft.word.doc")!,
            .init("org.openxmlformats.wordprocessingml.document")!
        ])
        picker.delegate = context.coordinator
        picker.allowsMultipleSelection = false
        return picker
    }
    
    func updateUIViewController(_ uiViewController: UIDocumentPickerViewController, context: Context) {}
    
    func makeCoordinator() -> Coordinator {
        Coordinator(onDocumentPicked: onDocumentPicked)
    }
    
    class Coordinator: NSObject, UIDocumentPickerDelegate {
        let onDocumentPicked: (URL) -> Void
        
        init(onDocumentPicked: @escaping (URL) -> Void) {
            self.onDocumentPicked = onDocumentPicked
        }
        
        func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
            if let url = urls.first {
                onDocumentPicked(url)
            }
        }
    }
}

struct ActivityViewController: UIViewControllerRepresentable {
    let activityItems: [Any]
    
    func makeUIViewController(context: Context) -> UIActivityViewController {
        let controller = UIActivityViewController(activityItems: activityItems, applicationActivities: nil)
        return controller
    }
    
    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}

struct SearchBar: View {
    @Binding var text: String
    
    var body: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.secondary)
            
            TextField("Search documents...", text: $text)
                .textFieldStyle(PlainTextFieldStyle())
            
            if !text.isEmpty {
                Button(action: { text = "" }) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(.secondary)
                }
            }
        }
        .padding(.horizontal, 15)
        .padding(.vertical, 10)
        .background(Color.gray.opacity(0.1))
        .cornerRadius(10)
    }
}

struct CategoryFilterView: View {
    @Binding var selectedCategory: DocumentCategory?
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 10) {
                CategoryButton(
                    title: "All",
                    icon: "doc.text",
                    isSelected: selectedCategory == nil
                ) {
                    selectedCategory = nil
                }
                
                ForEach(DocumentCategory.allCases, id: \.self) { category in
                    CategoryButton(
                        title: category.rawValue,
                        icon: category.icon,
                        isSelected: selectedCategory == category
                    ) {
                        selectedCategory = selectedCategory == category ? nil : category
                    }
                }
            }
            .padding(.horizontal)
        }
        .padding(.vertical, 10)
    }
}

struct CategoryButton: View {
    let title: String
    let icon: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 6) {
                Image(systemName: icon)
                    .font(.caption)
                Text(title)
                    .font(.caption)
                    .fontWeight(.medium)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(isSelected ? Color.blue : Color.gray.opacity(0.1))
            .foregroundColor(isSelected ? .white : .primary)
            .cornerRadius(20)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

struct DocumentRowView: View {
    let document: Document
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(document.fileName)
                        .font(.headline)
                        .lineLimit(1)
                    
                    HStack(spacing: 15) {
                        Label(document.category.rawValue, systemImage: document.category.icon)
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        Label(document.formattedSize, systemImage: "doc")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        if document.isShared {
                            Label("Shared", systemImage: "person.2")
                                .font(.caption)
                                .foregroundColor(.blue)
                        }
                    }
                }
                
                Spacer()
                
                VStack(alignment: .trailing, spacing: 4) {
                    if let signature = document.digitalSignature {
                        Image(systemName: "checkmark.seal.fill")
                            .foregroundColor(.green)
                            .font(.title3)
                    } else {
                        Image(systemName: "exclamationmark.triangle")
                            .foregroundColor(.orange)
                            .font(.title3)
                    }
                    
                    Text(formatDate(document.modifiedDate))
                        .font(.caption2)
                        .foregroundColor(.secondary)
                }
            }
            
            HStack {
                Text("Hash: \(document.hash.prefix(16))...")
                    .font(.caption2)
                    .foregroundColor(.secondary)
                    .lineLimit(1)
                
                Spacer()
                
                if let signature = document.digitalSignature {
                    Text("Signed: \(signature.prefix(12))...")
                        .font(.caption2)
                        .foregroundColor(.green)
                        .lineLimit(1)
                }
            }
        }
        .padding(.vertical, 4)
    }
    
    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .short
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}
