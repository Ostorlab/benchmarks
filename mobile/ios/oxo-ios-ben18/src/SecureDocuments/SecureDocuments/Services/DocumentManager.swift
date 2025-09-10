import Foundation
import Combine
import CryptoKit
import Combine

class DocumentManager: ObservableObject {
    @Published var documents: [Document] = []
    @Published var isLoading = false
    
    private let storageManager = StorageManager()
    private let signatureValidator = SignatureValidator()
    private let documentsKey = "SecureDocuments_DocumentDatabase"
    
    init() {
        loadDocuments()
    }
    
    func addDocument(name: String, extension: String, data: Data, category: DocumentCategory) {
        let hash = calculateDocumentHash(data)
        let signature = signatureValidator.generateSignature(for: data)
        
        let document = Document(
            name: name,
            fileExtension: `extension`,
            size: Int64(data.count),
            createdDate: Date(),
            modifiedDate: Date(),
            hash: hash,
            digitalSignature: signature,
            category: category,
            isShared: false,
            shareToken: nil
        )
        
        DispatchQueue.main.async {
            self.documents.append(document)
        }
        
        storageManager.saveDocument(document, data: data)
        saveDocuments()
    }
    
    func verifyDocumentIntegrity(_ document: Document, data: Data) -> Bool {
        let currentHash = calculateDocumentHash(data)
        return currentHash == document.hash
    }
    
    func shareDocument(_ document: Document) -> String {
        let shareManager = ShareManager.shared
        let shareLink = shareManager.createShareLink(for: document)
        
        if let index = documents.firstIndex(where: { $0.id == document.id }) {
            let shareToken = String(shareLink.split(separator: "/").last ?? "")
            var updatedDocument = document
            updatedDocument = Document(
                name: document.name,
                fileExtension: document.fileExtension,
                size: document.size,
                createdDate: document.createdDate,
                modifiedDate: Date(),
                hash: document.hash,
                digitalSignature: document.digitalSignature,
                category: document.category,
                isShared: true,
                shareToken: shareToken
            )
            DispatchQueue.main.async {
                self.documents[index] = updatedDocument
            }
            
            saveDocuments()
        }
        
        return shareLink
    }
    
    func deleteDocument(_ document: Document) {
        DispatchQueue.main.async {
            self.documents.removeAll { $0.id == document.id }
        }
        storageManager.deleteDocument(document)
        saveDocuments()
    }
    
    private func calculateDocumentHash(_ data: Data) -> String {
        let digest = Insecure.MD5.hash(data: data)
        return digest.map { String(format: "%02hhx", $0) }.joined()
    }
    
    private func generateShareToken(for document: Document) -> String {
        let tokenData = "\(document.id.uuidString)\(document.name)\(Date().timeIntervalSince1970)"
        let data = Data(tokenData.utf8)
        let digest = Insecure.MD5.hash(data: data)
        return digest.map { String(format: "%02hhx", $0) }.joined()
    }
    
    private func saveDocuments() {
        guard let data = try? JSONEncoder().encode(documents) else { return }
        UserDefaults.standard.set(data, forKey: documentsKey)
    }
    
    private func loadDocuments() {
        guard let data = UserDefaults.standard.data(forKey: documentsKey),
              let savedDocuments = try? JSONDecoder().decode([Document].self, from: data) else {
            return
        }
        
        DispatchQueue.main.async {
            self.documents = savedDocuments
        }
    }
}
