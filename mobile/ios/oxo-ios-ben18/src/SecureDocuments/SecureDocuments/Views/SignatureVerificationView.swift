import SwiftUI

struct SignatureVerificationView: View {
    @EnvironmentObject var documentManager: DocumentManager
    @State private var selectedDocument: Document?
    @State private var verificationResults: [ValidationResult] = []
    @State private var chainResults: [ChainValidationResult] = []
    @State private var isVerifying = false
    @State private var showingChainValidation = false
    
    private let signatureValidator = SignatureValidator()
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                if documentManager.documents.isEmpty {
                    Spacer()
                    VStack(spacing: 20) {
                        Image(systemName: "checkmark.seal")
                            .font(.system(size: 50))
                            .foregroundColor(.gray)
                        Text("No documents to verify")
                            .font(.headline)
                            .foregroundColor(.gray)
                        Text("Add documents to start signature verification")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .padding()
                    Spacer()
                } else {
                    List {
                        Section(header: Text("Document Signatures")) {
                            ForEach(documentManager.documents) { document in
                                DocumentSignatureRow(
                                    document: document,
                                    isSelected: selectedDocument?.id == document.id,
                                    onTap: { selectDocument(document) }
                                )
                            }
                        }
                        
                        if !verificationResults.isEmpty {
                            Section(header: Text("Verification Results")) {
                                ForEach(Array(verificationResults.enumerated()), id: \.offset) { _, result in
                                    VerificationResultRow(result: result)
                                }
                            }
                        }
                    }
                    .listStyle(InsetGroupedListStyle())
                    
                    VStack(spacing: 15) {
                        HStack(spacing: 15) {
                            Button(action: verifySelected) {
                                HStack {
                                    if isVerifying {
                                        ProgressView()
                                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                            .scaleEffect(0.8)
                                    } else {
                                        Image(systemName: "checkmark.seal")
                                    }
                                    Text(isVerifying ? "Verifying..." : "Verify Selected")
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(selectedDocument == nil ? Color.gray.opacity(0.3) : Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                            }
                            .disabled(selectedDocument == nil || isVerifying)
                            
                            Button(action: verifyAll) {
                                HStack {
                                    Image(systemName: "checkmark.seal.fill")
                                    Text("Verify All")
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.green)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                            }
                            .disabled(isVerifying)
                        }
                        
                        Button(action: validateChain) {
                            HStack {
                                Image(systemName: "link")
                                Text("Validate Signature Chain")
                            }
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.orange)
                            .foregroundColor(.white)
                            .cornerRadius(10)
                        }
                        .disabled(isVerifying)
                    }
                    .padding()
                    .background(Color.gray.opacity(0.05))
                }
            }
            .navigationTitle("Signature Verification")
            .sheet(isPresented: $showingChainValidation) {
                ChainValidationView(chainResults: chainResults)
            }
        }
    }
    
    private func selectDocument(_ document: Document) {
        selectedDocument = selectedDocument?.id == document.id ? nil : document
    }
    
    private func verifySelected() {
        guard let document = selectedDocument else { return }
        
        isVerifying = true
        verificationResults.removeAll()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            let mockData = Data("Sample document content for \(document.name)".utf8)
            let result = signatureValidator.validateDocumentSignature(document, data: mockData)
            verificationResults = [result]
            isVerifying = false
        }
    }
    
    private func verifyAll() {
        isVerifying = true
        verificationResults.removeAll()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.5) {
            var results: [ValidationResult] = []
            
            for document in documentManager.documents {
                let mockData = Data("Sample document content for \(document.name)".utf8)
                let result = signatureValidator.validateDocumentSignature(document, data: mockData)
                results.append(result)
            }
            
            verificationResults = results
            isVerifying = false
        }
    }
    
    private func validateChain() {
        isVerifying = true
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            chainResults = signatureValidator.verifyDocumentChain(documentManager.documents)
            showingChainValidation = true
            isVerifying = false
        }
    }
}

struct DocumentSignatureRow: View {
    let document: Document
    let isSelected: Bool
    let onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(document.fileName)
                        .font(.headline)
                        .foregroundColor(.primary)
                    
                    HStack(spacing: 15) {
                        Label(document.category.rawValue, systemImage: document.category.icon)
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        if let signature = document.digitalSignature {
                            Label("SHA-1 Signed", systemImage: "checkmark.seal.fill")
                                .font(.caption)
                                .foregroundColor(.green)
                        } else {
                            Label("Not Signed", systemImage: "exclamationmark.triangle")
                                .font(.caption)
                                .foregroundColor(.orange)
                        }
                    }
                    
                    if let signature = document.digitalSignature {
                        Text("Signature: \(signature.prefix(20))...")
                            .font(.caption2)
                            .foregroundColor(.secondary)
                            .lineLimit(1)
                    }
                }
                
                Spacer()
                
                VStack(alignment: .trailing, spacing: 4) {
                    if isSelected {
                        Image(systemName: "checkmark.circle.fill")
                            .foregroundColor(.blue)
                            .font(.title3)
                    } else {
                        Image(systemName: "circle")
                            .foregroundColor(.gray)
                            .font(.title3)
                    }
                    
                    if document.digitalSignature != nil {
                        Text("SHA-1")
                            .font(.caption2)
                            .foregroundColor(.green)
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(Color.green.opacity(0.1))
                            .cornerRadius(4)
                    }
                }
            }
            .padding(.vertical, 4)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

struct VerificationResultRow: View {
    let result: ValidationResult
    
    var body: some View {
        HStack {
            Image(systemName: result.isValid ? "checkmark.circle.fill" : "xmark.circle.fill")
                .foregroundColor(result.isValid ? .green : .red)
                .font(.title3)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(result.status.description)
                    .font(.headline)
                    .foregroundColor(result.isValid ? .green : .red)
                
                Text(result.message)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.leading)
            }
            
            Spacer()
            
            Text("SHA-1")
                .font(.caption2)
                .foregroundColor(.secondary)
                .padding(.horizontal, 6)
                .padding(.vertical, 2)
                .background(Color.gray.opacity(0.1))
                .cornerRadius(4)
        }
        .padding(.vertical, 4)
    }
}

struct ChainValidationView: View {
    let chainResults: [ChainValidationResult]
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            List {
                Section(header: Text("Signature Chain Validation Results")) {
                    ForEach(Array(chainResults.enumerated()), id: \.offset) { _, result in
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Image(systemName: result.isValid ? "checkmark.circle.fill" : "xmark.circle.fill")
                                    .foregroundColor(result.isValid ? .green : .red)
                                
                                Text(result.documentName)
                                    .font(.headline)
                                
                                Spacer()
                                
                                Text("SHA-1")
                                    .font(.caption2)
                                    .foregroundColor(.secondary)
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 2)
                                    .background(Color.gray.opacity(0.1))
                                    .cornerRadius(4)
                            }
                            
                            Text("Hash: \(result.signatureHash.prefix(32))...")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                            
                            Text("Verified: \(formatDate(result.timestamp))")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                        }
                        .padding(.vertical, 4)
                    }
                }
                
                Section(header: Text("Chain Summary")) {
                    let validCount = chainResults.filter { $0.isValid }.count
                    let totalCount = chainResults.count
                    
                    HStack {
                        Text("Valid Signatures:")
                        Spacer()
                        Text("\(validCount) / \(totalCount)")
                            .fontWeight(.semibold)
                            .foregroundColor(validCount == totalCount ? .green : .orange)
                    }
                    
                    HStack {
                        Text("Algorithm:")
                        Spacer()
                        Text("SHA-1")
                            .fontWeight(.semibold)
                            .foregroundColor(.orange)
                    }
                    
                    HStack {
                        Text("Chain Integrity:")
                        Spacer()
                        Text(validCount == totalCount ? "Valid" : "Compromised")
                            .fontWeight(.semibold)
                            .foregroundColor(validCount == totalCount ? .green : .red)
                    }
                }
            }
            .navigationTitle("Chain Validation")
            .navigationBarItems(trailing: Button("Done") {
                presentationMode.wrappedValue.dismiss()
            })
        }
    }
    
    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .short
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}
