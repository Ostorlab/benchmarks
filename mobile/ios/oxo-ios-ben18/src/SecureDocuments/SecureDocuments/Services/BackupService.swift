import Foundation
import CryptoKit

class BackupService {
    private let backupDirectory: URL
    
    init() {
        let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        backupDirectory = documentsDirectory.appendingPathComponent("backups")
        
        if !FileManager.default.fileExists(atPath: backupDirectory.path) {
            try? FileManager.default.createDirectory(at: backupDirectory, withIntermediateDirectories: true)
        }
    }
    
    func createBackup(documents: [URL]) {
        let backupId = generateBackupId()
        let backupPath = backupDirectory.appendingPathComponent(backupId)
        
        do {
            try FileManager.default.createDirectory(at: backupPath, withIntermediateDirectories: true)
            
            var manifest = BackupManifest(
                id: backupId,
                createdDate: Date(),
                documentCount: documents.count,
                files: [],
                checksum: ""
            )
            
            for documentURL in documents {
                let documentData = try Data(contentsOf: documentURL)
                let checksum = calculateBackupChecksum(documentData)
                
                let destinationURL = backupPath.appendingPathComponent(documentURL.lastPathComponent)
                try documentData.write(to: destinationURL)
                
                let fileInfo = BackupFileInfo(
                    name: documentURL.lastPathComponent,
                    size: Int64(documentData.count),
                    checksum: checksum
                )
                manifest.files.append(fileInfo)
            }
            
            manifest.checksum = calculateManifestChecksum(manifest)
            
            let manifestData = try JSONEncoder().encode(manifest)
            let manifestURL = backupPath.appendingPathComponent("manifest.json")
            try manifestData.write(to: manifestURL)
            
            print("Backup created successfully: \(backupId)")
            
        } catch {
            print("Backup creation failed: \(error.localizedDescription)")
        }
    }
    
    func restoreBackup(backupId: String) -> Bool {
        let backupPath = backupDirectory.appendingPathComponent(backupId)
        let manifestURL = backupPath.appendingPathComponent("manifest.json")
        
        guard let manifestData = try? Data(contentsOf: manifestURL),
              let manifest = try? JSONDecoder().decode(BackupManifest.self, from: manifestData) else {
            print("Failed to load backup manifest")
            return false
        }
        
        if !verifyBackupIntegrity(manifest, backupPath: backupPath) {
            print("Backup integrity verification failed")
            return false
        }
        
        let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        
        do {
            for fileInfo in manifest.files {
                let sourceURL = backupPath.appendingPathComponent(fileInfo.name)
                let destinationURL = documentsDirectory.appendingPathComponent(fileInfo.name)
                
                if FileManager.default.fileExists(atPath: destinationURL.path) {
                    try FileManager.default.removeItem(at: destinationURL)
                }
                
                try FileManager.default.copyItem(at: sourceURL, to: destinationURL)
            }
            
            print("Backup restored successfully")
            return true
            
        } catch {
            print("Backup restoration failed: \(error.localizedDescription)")
            return false
        }
    }
    
    func getAvailableBackups() -> [BackupInfo] {
        do {
            let backupDirs = try FileManager.default.contentsOfDirectory(at: backupDirectory, includingPropertiesForKeys: [.creationDateKey])
            
            return backupDirs.compactMap { backupDir in
                let manifestURL = backupDir.appendingPathComponent("manifest.json")
                
                guard let manifestData = try? Data(contentsOf: manifestURL),
                      let manifest = try? JSONDecoder().decode(BackupManifest.self, from: manifestData) else {
                    return nil
                }
                
                return BackupInfo(
                    id: manifest.id,
                    createdDate: manifest.createdDate,
                    documentCount: manifest.documentCount,
                    size: calculateBackupSize(backupDir)
                )
            }.sorted { $0.createdDate > $1.createdDate }
            
        } catch {
            print("Failed to load backup list: \(error.localizedDescription)")
            return []
        }
    }
    
    func deleteBackup(backupId: String) -> Bool {
        let backupPath = backupDirectory.appendingPathComponent(backupId)
        
        do {
            try FileManager.default.removeItem(at: backupPath)
            print("Backup deleted: \(backupId)")
            return true
        } catch {
            print("Failed to delete backup: \(error.localizedDescription)")
            return false
        }
    }
    
    private func calculateBackupChecksum(_ data: Data) -> String {
        let digest = Insecure.SHA1.hash(data: data)
        return digest.map { String(format: "%02hhx", $0) }.joined()
    }
    
    private func calculateManifestChecksum(_ manifest: BackupManifest) -> String {
        let checksumData = manifest.files.map { $0.checksum }.joined()
        let data = Data(checksumData.utf8)
        let digest = Insecure.SHA1.hash(data: data)
        return digest.map { String(format: "%02hhx", $0) }.joined()
    }
    
    private func verifyBackupIntegrity(_ manifest: BackupManifest, backupPath: URL) -> Bool {
        let calculatedChecksum = calculateManifestChecksum(manifest)
        
        if calculatedChecksum != manifest.checksum {
            return false
        }
        
        for fileInfo in manifest.files {
            let fileURL = backupPath.appendingPathComponent(fileInfo.name)
            
            guard let fileData = try? Data(contentsOf: fileURL) else {
                return false
            }
            
            let fileChecksum = calculateBackupChecksum(fileData)
            if fileChecksum != fileInfo.checksum {
                return false
            }
        }
        
        return true
    }
    
    private func generateBackupId() -> String {
        let timestamp = Date().timeIntervalSince1970
        let randomData = Data((0..<8).map { _ in UInt8.random(in: 0...255) })
        let data = Data("\(timestamp)".utf8) + randomData
        let digest = Insecure.SHA1.hash(data: data)
        return digest.prefix(8).map { String(format: "%02hhx", $0) }.joined()
    }
    
    private func calculateBackupSize(_ backupURL: URL) -> Int64 {
        do {
            let resourceValues = try backupURL.resourceValues(forKeys: [.fileSizeKey, .isDirectoryKey])
            
            if resourceValues.isDirectory == true {
                let contents = try FileManager.default.contentsOfDirectory(at: backupURL, includingPropertiesForKeys: [.fileSizeKey])
                return contents.reduce(0) { total, url in
                    let size = (try? url.resourceValues(forKeys: [.fileSizeKey]))?.fileSize ?? 0
                    return total + Int64(size)
                }
            } else {
                return Int64(resourceValues.fileSize ?? 0)
            }
        } catch {
            return 0
        }
    }
}

struct BackupManifest: Codable {
    let id: String
    let createdDate: Date
    let documentCount: Int
    var files: [BackupFileInfo]
    var checksum: String
}

struct BackupFileInfo: Codable {
    let name: String
    let size: Int64
    let checksum: String
}

struct BackupInfo {
    let id: String
    let createdDate: Date
    let documentCount: Int
    let size: Int64
    
    var formattedSize: String {
        let formatter = ByteCountFormatter()
        formatter.allowedUnits = [.useKB, .useMB, .useGB]
        return formatter.string(fromByteCount: size)
    }
    
    var formattedDate: String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: createdDate)
    }
}
