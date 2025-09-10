import SwiftUI

struct AddDocumentView: View {
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject var documentManager: DocumentManager
    
    @State private var documentName = ""
    @State private var selectedCategory: DocumentCategory = .corporate
    @State private var selectedExtension = "pdf"
    @State private var isUploading = false
    
    private let fileExtensions = ["pdf", "docx", "xlsx", "txt", "jpg", "png"]
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Document Details")) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Document Name")
                            .font(.headline)
                        TextField("Enter document name", text: $documentName)
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Category")
                            .font(.headline)
                        Picker("Category", selection: $selectedCategory) {
                            ForEach(DocumentCategory.allCases, id: \.self) { category in
                                HStack {
                                    Image(systemName: category.icon)
                                    Text(category.rawValue)
                                }.tag(category)
                            }
                        }
                        .pickerStyle(MenuPickerStyle())
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("File Type")
                            .font(.headline)
                        Picker("File Extension", selection: $selectedExtension) {
                            ForEach(fileExtensions, id: \.self) { ext in
                                Text(".\(ext)").tag(ext)
                            }
                        }
                        .pickerStyle(MenuPickerStyle())
                    }
                }
                
                Section(header: Text("Preview")) {
                    VStack(alignment: .leading, spacing: 10) {
                        HStack {
                            Image(systemName: selectedCategory.icon)
                                .foregroundColor(.blue)
                                .font(.title2)
                            VStack(alignment: .leading) {
                                Text("\(documentName.isEmpty ? "Document Name" : documentName).\(selectedExtension)")
                                    .font(.headline)
                                Text(selectedCategory.rawValue)
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                        }
                        
                        Text("This document will be processed with:")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        VStack(alignment: .leading, spacing: 4) {
                            HStack {
                                Image(systemName: "checkmark.circle")
                                    .foregroundColor(.green)
                                Text("MD5 integrity verification")
                                    .font(.caption)
                            }
                            HStack {
                                Image(systemName: "checkmark.circle")
                                    .foregroundColor(.green)
                                Text("SHA-1 digital signature")
                                    .font(.caption)
                            }
                            HStack {
                                Image(systemName: "checkmark.circle")
                                    .foregroundColor(.green)
                                Text("Automatic backup with checksums")
                                    .font(.caption)
                            }
                        }
                        .padding(.leading, 10)
                    }
                    .padding(.vertical, 5)
                }
                
                Section {
                    Button(action: addDocument) {
                        HStack {
                            if isUploading {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle())
                                    .scaleEffect(0.8)
                            }
                            Text(isUploading ? "Processing..." : "Add Document")
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(documentName.isEmpty ? Color.gray.opacity(0.3) : Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                    }
                    .disabled(documentName.isEmpty || isUploading)
                    .buttonStyle(PlainButtonStyle())
                }
            }
            .navigationTitle("Add Document")
            .navigationBarItems(
                leading: Button("Cancel") {
                    presentationMode.wrappedValue.dismiss()
                },
                trailing: EmptyView()
            )
        }
    }
    
    private func addDocument() {
        isUploading = true
        
        let sampleContent = generateSampleContent()
        let documentData = Data(sampleContent.utf8)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            documentManager.addDocument(
                name: documentName,
                extension: selectedExtension,
                data: documentData,
                category: selectedCategory
            )
            
            isUploading = false
            presentationMode.wrappedValue.dismiss()
        }
    }
    
    private func generateSampleContent() -> String {
        switch selectedCategory {
        case .legal:
            return """
            CONFIDENTIAL LEGAL DOCUMENT
            
            This is a sample legal document for \(documentName).
            Contains sensitive legal information and agreements.
            
            Document created: \(Date())
            Category: Legal
            
            [Sample legal content would be here]
            """
        case .medical:
            return """
            MEDICAL RECORD - CONFIDENTIAL
            
            Patient Document: \(documentName)
            Date: \(Date())
            
            This document contains protected health information.
            
            [Sample medical record content would be here]
            """
        case .financial:
            return """
            FINANCIAL REPORT
            
            Document: \(documentName)
            Generated: \(Date())
            
            Confidential financial information and analysis.
            
            [Sample financial data would be here]
            """
        case .corporate:
            return """
            CORPORATE DOCUMENT
            
            Title: \(documentName)
            Created: \(Date())
            
            Internal corporate information and procedures.
            
            [Sample corporate content would be here]
            """
        case .personal:
            return """
            PERSONAL DOCUMENT
            
            Document: \(documentName)
            Date: \(Date())
            
            Personal information and data.
            
            [Sample personal content would be here]
            """
        }
    }
}
