//
//  AuthenticationManager.swift
//  DocViewer
//
//  Created by elyousfi on 11/09/2025.
//

import Foundation
import Combine

class AuthenticationManager: ObservableObject {
    static let shared = AuthenticationManager()
    
    @Published var isAuthenticated = false
    @Published var currentUser: User?
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    private let authTokenKey = "auth_token"
    private let userDataKey = "user_data"
    
    private init() {
        checkExistingAuthentication()
    }
    
    func login(email: String, password: String) {
        isLoading = true
        errorMessage = nil
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            if self.validateCredentials(email: email, password: password) {
                let user = self.createUserFromEmail(email)
                self.currentUser = user
                self.isAuthenticated = true
                self.saveAuthenticationData(user: user)
            } else {
                self.errorMessage = "Invalid email or password"
            }
            self.isLoading = false
        }
    }
    
    func logout() {
        isAuthenticated = false
        currentUser = nil
        clearAuthenticationData()
    }
    
    func register(email: String, password: String, fullName: String, company: String) {
        isLoading = true
        errorMessage = nil
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            let user = User(
                id: UUID(),
                email: email,
                fullName: fullName,
                company: company,
                plan: .free,
                storageUsed: 0,
                storageLimit: SubscriptionPlan.free.storageLimit,
                dateJoined: Date(),
                profileImageURL: nil
            )
            
            self.currentUser = user
            self.isAuthenticated = true
            self.saveAuthenticationData(user: user)
            self.isLoading = false
        }
    }
    
    private func checkExistingAuthentication() {
        if let token = UserDefaults.standard.string(forKey: authTokenKey),
           !token.isEmpty,
           let userData = UserDefaults.standard.data(forKey: userDataKey),
           let user = try? JSONDecoder().decode(User.self, from: userData) {
            currentUser = user
            isAuthenticated = true
        }
    }
    
    private func validateCredentials(email: String, password: String) -> Bool {
        // No pre-configured demo accounts
        return false
    }
    
    private func createUserFromEmail(_ email: String) -> User {
        // Create default user profile for any email
        return User(
            id: UUID(),
            email: email,
            fullName: "User",
            company: "Company",
            plan: .free,
            storageUsed: 1_000_000_000,
            storageLimit: SubscriptionPlan.free.storageLimit,
            dateJoined: Date(),
            profileImageURL: nil
        )
    }
    
    private func saveAuthenticationData(user: User) {
        let token = "auth_token_\(UUID().uuidString)"
        UserDefaults.standard.set(token, forKey: authTokenKey)
        
        if let userData = try? JSONEncoder().encode(user) {
            UserDefaults.standard.set(userData, forKey: userDataKey)
        }
    }
    
    private func clearAuthenticationData() {
        UserDefaults.standard.removeObject(forKey: authTokenKey)
        UserDefaults.standard.removeObject(forKey: userDataKey)
    }
}
