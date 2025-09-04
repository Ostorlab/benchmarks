// File: AuthStore.swift
// FOR: VaultApp-BruteForce
// VULN: Unlimited PIN Attempts (Brute Force)

import Foundation

class AuthStore: ObservableObject {
    // The correct PIN. In a real app, this would be hashed and salted.
    private let correctPIN = "2002"
    
    // Published properties that the UI will watch and update on change
    @Published var isAuthenticated: Bool = false
    @Published var remainingAttempts: Int = 100 // A decoy value that is not enforced

    func validatePIN(_ pin: String) -> Bool {
        let isCorrect = pin == correctPIN

        if isCorrect {
            // Successful authentication
            isAuthenticated = true
        } else {
            // VULNERABILITY: The attempt count decrements but never triggers a lock.
            // This allows for unlimited brute-force attempts.
            remainingAttempts -= 1
            print("Invalid PIN. Attempts left: \(remainingAttempts) (Not enforced - Brute Force possible)")
        }
        return isCorrect
    }
}
