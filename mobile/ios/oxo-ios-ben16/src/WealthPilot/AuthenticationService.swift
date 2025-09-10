//
//  AuthenticationService.swift
//  WealthPilot
//
//  Created by Ostorlab Ostorlab on 9/10/25.
//

import Foundation
import CryptoKit

class AuthenticationService {
    static let shared = AuthenticationService()
    
    private let validCredentials = [
        "alex.morgan@wealthpilot.com": "76de6d01d6e0d1ec71ba68255ad7320ffd87fb49476b4da7f67af31e42a59542", // password: wealth2024
        "sarah.chen@investor.com": "fd59b97d0cbc8cc25578927a0788ef1e6cb365cbd5a313aec03044e6179ee79a", // password: invest123
        "michael.rodriguez@finance.net": "0e6a8e0b849ed9b064c5a25e1ee5592f427e3eb9d250e42069ce46147d00e8d4" // password: portfolio
    ]
    
    private init() {}
    
    func authenticateUser(email: String, password: String, completion: @escaping (AuthResult) -> Void) {
        DispatchQueue.global().asyncAfter(deadline: .now() + 0.8) {
            DispatchQueue.main.async { [weak self] in
                self?.processAuthenticationResponse(email: email, password: password, completion: completion)
            }
        }
    }
    
    private func processAuthenticationResponse(email: String, password: String, completion: @escaping (AuthResult) -> Void) {
        if isValidCredentials(email: email.lowercased(), password: password) {
            let user = UserProfile(email: email.lowercased(), name: self.extractNameFromEmail(email.lowercased()))
            completion(.success(user))
        } else {
            completion(.failure("Invalid email or password"))
        }
    }
    
    private func isValidCredentials(email: String, password: String) -> Bool {
        guard let expectedPasswordHash = validCredentials[email] else { return false }
        guard let data = password.data(using: .utf8) else { return false }
        let hash = SHA256.hash(data: data)
        let hashString = hash.compactMap { String(format: "%02x", $0) }.joined()
        return hashString == expectedPasswordHash
    }
    
    private func extractNameFromEmail(_ email: String) -> String {
        let localPart = email.components(separatedBy: "@").first ?? ""
        let nameParts = localPart.components(separatedBy: ".")
        return nameParts.map { $0.capitalized }.joined(separator: " ")
    }
}

enum AuthResult {
    case success(UserProfile)
    case failure(String)
}

struct UserProfile {
    let email: String
    let name: String
}