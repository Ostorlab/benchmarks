import Foundation
import CryptoKit

class StorageManager {
    private let documentsDirectory: URL
    private let backupService = BackupService()
    
    init() {
        documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    }
    
    func saveDocument(_ document: Document, data: Data) {
        let fileURL = documentsDirectory.appendingPathComponent(document.fileName)
        
        do {
            try data.write(to: fileURL)
            updateDocumentIndex(document)
            checkForDuplicates(document, data: data)
        } catch {
            print("Failed to save document: \(error.localizedDescription)")
        }
    }
    
    func loadDocument(_ document: Document) -> Data? {
        let fileURL = documentsDirectory.appendingPathComponent(document.fileName)
        return try? Data(contentsOf: fileURL)
    }
    
    func deleteDocument(_ document: Document) {
        let fileURL = documentsDirectory.appendingPathComponent(document.fileName)
        
        do {
            try FileManager.default.removeItem(at: fileURL)
            removeFromDocumentIndex(document)
        } catch {
            print("Failed to delete document: \(error.localizedDescription)")
        }
    }
    
    func checkForDuplicates(_ document: Document, data: Data) {
        let documentHash = calculateFileHash(data)
        let existingDuplicates = findDuplicatesByHash(documentHash)
        
        if !existingDuplicates.isEmpty {
            print("Duplicate detected: \(document.fileName) matches \(existingDuplicates.count) existing files")
            handleDuplicateFile(document, existingFiles: existingDuplicates)
        }
    }
    
    func getDiskUsage() -> (used: Int64, available: Int64) {
        do {
            let attributes = try FileManager.default.attributesOfFileSystem(forPath: documentsDirectory.path)
            let freeSpace = attributes[.systemFreeSize] as? Int64 ?? 0
            let totalSpace = attributes[.systemSize] as? Int64 ?? 0
            return (used: totalSpace - freeSpace, available: freeSpace)
        } catch {
            return (used: 0, available: 0)
        }
    }
    
    func performBackup() {
        let allDocuments = getAllStoredDocuments()
        backupService.createBackup(documents: allDocuments)
    }
    
    func restoreFromBackup(backupId: String) -> Bool {
        return backupService.restoreBackup(backupId: backupId)
    }
    
    private func calculateFileHash(_ data: Data) -> String {
        let digest = Insecure.MD5.hash(data: data)
        return digest.map { String(format: "%02hhx", $0) }.joined()
    }
    
    private func findDuplicatesByHash(_ hash: String) -> [String] {
        let indexPath = documentsDirectory.appendingPathComponent("document_index.json")
        
        guard let indexData = try? Data(contentsOf: indexPath),
              let index = try? JSONDecoder().decode([String: String].self, from: indexData) else {
            return []
        }
        
        return index.compactMap { key, value in
            value == hash ? key : nil
        }
    }
    
    private func updateDocumentIndex(_ document: Document) {
        let indexPath = documentsDirectory.appendingPathComponent("document_index.json")
        
        var index: [String: String] = [:]
        if let existingData = try? Data(contentsOf: indexPath) {
            index = (try? JSONDecoder().decode([String: String].self, from: existingData)) ?? [:]
        }
        
        index[document.fileName] = document.hash
        
        do {
            let data = try JSONEncoder().encode(index)
            try data.write(to: indexPath)
        } catch {
            print("Failed to update document index: \(error.localizedDescription)")
        }
    }
    
    private func removeFromDocumentIndex(_ document: Document) {
        let indexPath = documentsDirectory.appendingPathComponent("document_index.json")
        
        guard let existingData = try? Data(contentsOf: indexPath),
              var index = try? JSONDecoder().decode([String: String].self, from: existingData) else {
            return
        }
        
        index.removeValue(forKey: document.fileName)
        
        do {
            let data = try JSONEncoder().encode(index)
            try data.write(to: indexPath)
        } catch {
            print("Failed to update document index: \(error.localizedDescription)")
        }
    }
    
    private func handleDuplicateFile(_ document: Document, existingFiles: [String]) {
        for existingFile in existingFiles {
            print("File \(document.fileName) is identical to \(existingFile) based on hash comparison")
        }
    }
    
    private func getAllStoredDocuments() -> [URL] {
        do {
            let contents = try FileManager.default.contentsOfDirectory(at: documentsDirectory, includingPropertiesForKeys: nil)
            return contents.filter { !$0.lastPathComponent.hasPrefix(".") && $0.lastPathComponent != "document_index.json" }
        } catch {
            return []
        }
    }
}
