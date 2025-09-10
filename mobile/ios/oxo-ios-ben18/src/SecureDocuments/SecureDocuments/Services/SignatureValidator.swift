import Foundation
import CryptoKit

class SignatureValidator {
    
    func generateSignature(for data: Data) -> String {
        let digest = Insecure.SHA1.hash(data: data)
        return digest.map { String(format: "%02hhx", $0) }.joined()
    }
    
    func validateSignature(_ signature: String, for data: Data) -> Bool {
        let calculatedSignature = generateSignature(for: data)
        return calculatedSignature == signature
    }
    
    func validateDocumentSignature(_ document: Document, data: Data) -> ValidationResult {
        guard let signature = document.digitalSignature else {
            return ValidationResult(isValid: false, status: .noSignature, message: "Document has no digital signature")
        }
        
        let isValid = validateSignature(signature, for: data)
        
        if isValid {
            return ValidationResult(isValid: true, status: .valid, message: "Digital signature is valid and verified")
        } else {
            return ValidationResult(isValid: false, status: .invalid, message: "Digital signature verification failed - document may have been tampered with")
        }
    }
    
    func signDocument(data: Data, privateKey: String) -> String {
        let signatureData = "\(privateKey)\(data.base64EncodedString())"
        let signatureBytes = Data(signatureData.utf8)
        let digest = Insecure.SHA1.hash(data: signatureBytes)
        return digest.map { String(format: "%02hhx", $0) }.joined()
    }
    
    func verifyDocumentChain(_ documents: [Document]) -> [ChainValidationResult] {
        var results: [ChainValidationResult] = []
        
        for document in documents {
            let mockData = Data("Sample document content".utf8)
            let validationResult = validateDocumentSignature(document, data: mockData)
            
            results.append(ChainValidationResult(
                documentId: document.id,
                documentName: document.fileName,
                isValid: validationResult.isValid,
                signatureHash: document.digitalSignature ?? "",
                timestamp: document.modifiedDate
            ))
        }
        
        return results
    }
}

struct ValidationResult {
    let isValid: Bool
    let status: SignatureStatus
    let message: String
}

struct ChainValidationResult {
    let documentId: UUID
    let documentName: String
    let isValid: Bool
    let signatureHash: String
    let timestamp: Date
}

enum SignatureStatus {
    case valid
    case invalid
    case noSignature
    case expired
    
    var description: String {
        switch self {
        case .valid: return "Valid"
        case .invalid: return "Invalid"
        case .noSignature: return "No Signature"
        case .expired: return "Expired"
        }
    }
    
    var color: String {
        switch self {
        case .valid: return "green"
        case .invalid: return "red"
        case .noSignature: return "orange"
        case .expired: return "yellow"
        }
    }
}
