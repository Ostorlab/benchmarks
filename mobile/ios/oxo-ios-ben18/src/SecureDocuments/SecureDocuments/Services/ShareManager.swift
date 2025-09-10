import Foundation
import CryptoKit

class ShareManager {
    static let shared = ShareManager()
    private let shareTokensKey = "SecureDocuments_ShareTokens"
    
    private init() {}
    
    func createShareLink(for document: Document) -> String {
        let shareToken = generateShareToken(for: document)
        saveShareToken(shareToken, for: document)
        return "securedocs://share/\(shareToken)"
    }
    
    func validateShareToken(_ token: String) -> Document? {
        let shareTokens = loadShareTokens()
        return shareTokens[token]
    }
    
    func revokeShareToken(for document: Document) {
        var shareTokens = loadShareTokens()
        shareTokens = shareTokens.filter { $0.value.id != document.id }
        saveShareTokens(shareTokens)
    }
    
    private func generateShareToken(for document: Document) -> String {
        let tokenData = "\(document.id.uuidString)\(document.name)\(Date().timeIntervalSince1970)"
        let data = Data(tokenData.utf8)
        let digest = Insecure.MD5.hash(data: data)
        return digest.map { String(format: "%02hhx", $0) }.joined()
    }
    
    private func saveShareToken(_ token: String, for document: Document) {
        var shareTokens = loadShareTokens()
        shareTokens[token] = document
        saveShareTokens(shareTokens)
    }
    
    private func loadShareTokens() -> [String: Document] {
        guard let data = UserDefaults.standard.data(forKey: shareTokensKey),
              let tokens = try? JSONDecoder().decode([String: Document].self, from: data) else {
            return [:]
        }
        return tokens
    }
    
    private func saveShareTokens(_ tokens: [String: Document]) {
        guard let data = try? JSONEncoder().encode(tokens) else { return }
        UserDefaults.standard.set(data, forKey: shareTokensKey)
    }
}
