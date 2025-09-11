//
//  SocialAuthService.swift
//  SocialShareHub
//
//  OAuth authentication service with vulnerable token validation

import Foundation
import UIKit

class SocialAuthService {
    static let shared = SocialAuthService()
    
    private init() {}
    
    // MARK: - Authentication Methods
    
    func authenticateWithFacebook(completion: @escaping (Bool, User?) -> Void) {
        // Simulate realistic Facebook OAuth flow with user interaction
        simulateOAuthPopup(provider: "facebook") { [weak self] success, token in
            if success, let token = token {
                // Send token to our vulnerable backend API
                self?.authenticateWithBackend(token: token, provider: "facebook") { success, user in
                    completion(success, user)
                }
            } else {
                completion(false, nil)
            }
        }
    }
    
    func authenticateWithTwitter(completion: @escaping (Bool, User?) -> Void) {
        // Simulate realistic Twitter OAuth flow with user interaction
        simulateOAuthPopup(provider: "twitter") { [weak self] success, token in
            if success, let token = token {
                // Send token to our vulnerable backend API
                self?.authenticateWithBackend(token: token, provider: "twitter") { success, user in
                    completion(success, user)
                }
            } else {
                completion(false, nil)
            }
        }
    }
    
    // MARK: - Backend Communication
    
    private func authenticateWithBackend(token: String, provider: String, completion: @escaping (Bool, User?) -> Void) {
        guard let url = URL(string: "http://127.0.0.1:8080/api/auth/social") else {
            completion(false, nil)
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let payload = [
            "provider": provider,
            "access_token": token
        ]
        
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: payload)
        } catch {
            completion(false, nil)
            return
        }
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Network error: \(error.localizedDescription)")
                DispatchQueue.main.async {
                    completion(false, nil)
                }
                return
            }
            
            guard let data = data else {
                print("❌ No data received from server")
                DispatchQueue.main.async {
                    completion(false, nil)
                }
                return
            }
            
            guard let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
                print("❌ Invalid JSON response")
                DispatchQueue.main.async {
                    completion(false, nil)
                }
                return
            }
            
            guard let success = json["success"] as? Bool else {
                print("❌ Missing success field in response")
                DispatchQueue.main.async {
                    completion(false, nil)
                }
                return
            }
            
            if success,
               let userData = json["user"] as? [String: Any],
               let user = User.fromJSON(userData) {
                print("✅ Authentication successful for user: \(user.name)")
                DispatchQueue.main.async {
                    completion(true, user)
                }
            } else {
                print("❌ Authentication failed or invalid user data")
                DispatchQueue.main.async {
                    completion(false, nil)
                }
            }
        }.resume()
    }
    
    // MARK: - OAuth Flow Simulation
    
    private func simulateOAuthPopup(provider: String, completion: @escaping (Bool, String?) -> Void) {
        if provider == "facebook" {
            // Show Facebook credential prompt
            showFacebookCredentialPrompt(completion: completion)
        } else {
            // Keep existing Twitter flow for now
            DispatchQueue.main.async {
                let alert = UIAlertController(
                    title: "Twitter Login",
                    message: "SocialShare Hub wants to connect to your Twitter account.\n\nThis will allow the app to:\n• View your profile info\n• Manage your posts\n• Access connected accounts",
                    preferredStyle: .alert
                )
                
                alert.addAction(UIAlertAction(title: "Cancel", style: .cancel) { _ in
                    completion(false, nil)
                })
                
                alert.addAction(UIAlertAction(title: "Authorize", style: .default) { _ in
                    let token = self.generateMockTwitterToken()
                    completion(true, token)
                })
                
                if let topVC = self.topViewController() {
                    topVC.present(alert, animated: true)
                } else {
                    let token = self.generateMockTwitterToken()
                    completion(true, token)
                }
            }
        }
    }
    
    private func showFacebookCredentialPrompt(completion: @escaping (Bool, String?) -> Void) {
        DispatchQueue.main.async {
            let alert = UIAlertController(
                title: "Facebook Login",
                message: "Enter your Facebook credentials to connect with SocialShare Hub",
                preferredStyle: .alert
            )
            
            alert.addTextField { textField in
                textField.placeholder = "Email or username"
                textField.keyboardType = .emailAddress
                textField.autocapitalizationType = .none
            }
            
            alert.addTextField { textField in
                textField.placeholder = "Password"
                textField.isSecureTextEntry = true
            }
            
            alert.addAction(UIAlertAction(title: "Cancel", style: .cancel) { _ in
                completion(false, nil)
            })
            
            alert.addAction(UIAlertAction(title: "Login", style: .default) { _ in
                guard let usernameField = alert.textFields?.first,
                      let passwordField = alert.textFields?.last,
                      let username = usernameField.text,
                      let password = passwordField.text,
                      !username.isEmpty, !password.isEmpty else {
                    completion(false, nil)
                    return
                }
                
                // Authenticate with fake Facebook API
                self.authenticateWithFacebookAPI(username: username, password: password, completion: completion)
            })
            
            if let topVC = self.topViewController() {
                topVC.present(alert, animated: true)
            } else {
                completion(false, nil)
            }
        }
    }
    
    private func authenticateWithFacebookAPI(username: String, password: String, completion: @escaping (Bool, String?) -> Void) {
        guard let url = URL(string: "http://127.0.0.1:8080/api/facebook/oauth") else {
            completion(false, nil)
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let payload = [
            "username": username,
            "password": password
        ]
        
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: payload)
        } catch {
            completion(false, nil)
            return
        }
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("❌ Facebook OAuth error: \(error.localizedDescription)")
                DispatchQueue.main.async {
                    completion(false, nil)
                }
                return
            }
            
            guard let data = data,
                  let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else {
                print("❌ Invalid Facebook OAuth response")
                DispatchQueue.main.async {
                    completion(false, nil)
                }
                return
            }
            
            if let success = json["success"] as? Bool, success,
               let token = json["access_token"] as? String {
                print("✅ Facebook OAuth successful, token received")
                DispatchQueue.main.async {
                    completion(true, token)
                }
            } else {
                print("❌ Facebook OAuth failed: \(json["message"] ?? "Unknown error")")
                DispatchQueue.main.async {
                    completion(false, nil)
                }
            }
        }.resume()
    }
    
    private func topViewController() -> UIViewController? {
        guard let window = UIApplication.shared.windows.first(where: { $0.isKeyWindow }),
              let rootVC = window.rootViewController else { return nil }
        
        var topVC = rootVC
        while let presentedVC = topVC.presentedViewController {
            topVC = presentedVC
        }
        
        if let navController = topVC as? UINavigationController {
            return navController.topViewController
        }
        
        return topVC
    }
    
    // MARK: - Mock Token Generation
    
    private func generateMockFacebookToken() -> String {
        // Generate realistic looking Facebook token
        let tokenPrefixes = [
            "EAAJ8Of8DF2IBAL",
            "EAABwzLixnjYBO",
            "EAAGNO4a7r2wBAI",
            "EAAGgJZCZBuiQBAK"
        ]
        
        let randomPrefix = tokenPrefixes.randomElement() ?? "EAAJ8Of8DF2IBAL"
        let randomSuffix = String((0..<40).map { _ in "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".randomElement()! })
        
        return randomPrefix + randomSuffix
    }
    
    private func generateMockTwitterToken() -> String {
        // Generate realistic looking Twitter token
        let randomToken = String((0..<50).map { _ in "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".randomElement()! })
        return randomToken
    }
}

// MARK: - User Model

struct User {
    let id: String
    let name: String
    let email: String
    let provider: String
    let connectedAccounts: [String]
    
    static func fromJSON(_ json: [String: Any]) -> User? {
        guard let id = json["id"] as? String,
              let name = json["name"] as? String,
              let email = json["email"] as? String,
              let provider = json["provider"] as? String else {
            return nil
        }
        
        let accounts = json["connected_accounts"] as? [String] ?? []
        
        return User(id: id, name: name, email: email, provider: provider, connectedAccounts: accounts)
    }
}