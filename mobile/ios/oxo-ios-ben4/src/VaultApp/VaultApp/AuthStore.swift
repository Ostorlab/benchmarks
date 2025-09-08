import Foundation
import SwiftUI

class AuthStore: ObservableObject {
    @Published var isAuthenticated = false
    @Published var isPINSet = false
    @Published var userPIN = ""
    
    init() {
        // Check if PIN is already set
        if let storedPIN = UserDefaults.standard.string(forKey: "userPIN") {
            userPIN = storedPIN
            isPINSet = true
        }
    }
    
    func setupPIN(_ pin: String) -> Bool {
        // Validate PIN format (4 digits)
        if pin.count == 4 && pin.allSatisfy({ $0.isNumber }) {
            userPIN = pin
            isPINSet = true
            
            // Store PIN in UserDefaults (insecure by design)
            UserDefaults.standard.set(pin, forKey: "userPIN")
            return true
        }
        return false
    }
    
    func validatePIN(_ pin: String) -> Bool {
        if pin == userPIN {
            isAuthenticated = true
            return true
        } else {
            // Vulnerability: No lockout mechanism for failed attempts
            return false
        }
    }
    
    func resetPIN() {
        userPIN = ""
        isPINSet = false
        isAuthenticated = false
        UserDefaults.standard.removeObject(forKey: "userPIN")
    }
}
