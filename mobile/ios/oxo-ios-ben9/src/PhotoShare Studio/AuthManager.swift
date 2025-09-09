//
//  AuthManager.swift
//  PhotoShare Studio
//
//  Simple authentication manager for login/logout functionality
//

import Foundation

class AuthManager {
    static let shared = AuthManager()
    
    private let USERNAME = "ostorlab"
    private let PASSWORD = "ostorlab"
    
    private var isLoggedIn: Bool = false
    private var currentUser: String?
    
    private init() {
        // Check if user was previously logged in
        isLoggedIn = UserDefaults.standard.bool(forKey: "isLoggedIn")
        currentUser = UserDefaults.standard.string(forKey: "currentUser")
    }
    
    func login(username: String, password: String) -> Bool {
        if username == USERNAME && password == PASSWORD {
            isLoggedIn = true
            currentUser = username
            
            // Persist login state
            UserDefaults.standard.set(true, forKey: "isLoggedIn")
            UserDefaults.standard.set(username, forKey: "currentUser")
            UserDefaults.standard.synchronize()
            
            return true
        }
        return false
    }
    
    func logout() {
        isLoggedIn = false
        currentUser = nil
        
        // Clear login state
        UserDefaults.standard.removeObject(forKey: "isLoggedIn")
        UserDefaults.standard.removeObject(forKey: "currentUser")
        UserDefaults.standard.synchronize()
    }
    
    func isUserLoggedIn() -> Bool {
        return isLoggedIn
    }
    
    func getCurrentUser() -> String? {
        return currentUser
    }
    
    func requiresLogin() -> Bool {
        return !isLoggedIn
    }
}