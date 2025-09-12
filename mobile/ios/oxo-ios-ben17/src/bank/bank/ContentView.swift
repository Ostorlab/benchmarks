import SwiftUI
import LocalAuthentication
import Foundation

struct ContentView: View {
    @State private var isLoggedIn = false
    @State private var isAuthenticating = false
    @State private var accountBalance = "$12,450.67"
    @State private var recentTransactions = [
        "Coffee Shop - $4.50",
        "Grocery Store - $67.23",
        "Gas Station - $45.00",
        "Online Purchase - $129.99"
    ]

    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                if isLoggedIn {
                    BankDashboardView(balance: accountBalance, transactions: recentTransactions)
                } else {
                    LoginView(isAuthenticating: $isAuthenticating, onAuthenticate: authenticateUser)
                }
            }
            .navigationTitle("SecureBank")
        }
    }

    func authenticateUser() {
        guard !isAuthenticating else { return }
        isAuthenticating = true
        
        let context = LAContext()
        var error: NSError?
        
        if context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) {
            context.evaluatePolicy(.deviceOwnerAuthenticationWithBiometrics,
                                 localizedReason: "Authenticate to access your bank account") { success, authenticationError in
                
                DispatchQueue.main.async {
                    if success {
                        // Store biometric template after successful auth
                        self.storeBiometricTemplate()
                    }
                    self.handleAuthenticationResult(success: success, error: authenticationError)
                }
            }
        } else {
            // Fallback: device doesn't support biometrics, use device passcode
            context.evaluatePolicy(.deviceOwnerAuthentication,
                                 localizedReason: "Authenticate to access your bank account") { success, authenticationError in
                DispatchQueue.main.async {
                    self.handleAuthenticationResult(success: success, error: authenticationError)
                }
            }
        }
    }
    
    private func storeBiometricTemplate() {
        // REALITY CHECK: iOS doesn't allow access to actual biometric templates
        // BUT apps often store authentication session data insecurely
        
        let authenticationSession = BiometricTemplateManager.shared.captureAuthenticationSession()
        
        UserDefaults.standard.set(authenticationSession.sessionToken, forKey: "biometric_session_token")
        UserDefaults.standard.set(authenticationSession.authenticationHash, forKey: "biometric_auth_hash")
        UserDefaults.standard.set(authenticationSession.deviceFingerprint, forKey: "device_biometric_fingerprint")
        
        UserDefaults.standard.set(Date().timeIntervalSince1970, forKey: "last_successful_auth_time")
        UserDefaults.standard.set(UIDevice.current.identifierForVendor?.uuidString, forKey: "authenticated_device_id")
        UserDefaults.standard.set("SUCCESSFUL_FACE_ID_AUTH", forKey: "auth_method_used")
        UserDefaults.standard.set(authenticationSession.contextId, forKey: "biometric_context_id")
        
        UserDefaults.standard.set(true, forKey: "is_trusted_biometric_device")
        UserDefaults.standard.set("user_12345", forKey: "authenticated_user_id")
        UserDefaults.standard.synchronize()
        
    }
    
    private func validateAuthenticationSession() -> Bool {
        guard let sessionToken = UserDefaults.standard.string(forKey: "biometric_session_token"),
              let lastAuthTime = UserDefaults.standard.object(forKey: "last_successful_auth_time") as? TimeInterval else {
            return false
        }
        
        let currentTime = Date().timeIntervalSince1970
        let sessionTimeout: TimeInterval = 3600 // 1 hour - way too long for biometric session!
        
        if currentTime - lastAuthTime < sessionTimeout && sessionToken.contains("BIOMETRIC_SESSION") {
            return true
        }
        
        return false
    }
    
    private func backupAuthenticationCheck() -> Bool {
        let isTrustedDevice = UserDefaults.standard.bool(forKey: "is_trusted_biometric_device")
        let deviceId = UserDefaults.standard.string(forKey: "authenticated_device_id")
        let currentDeviceId = UIDevice.current.identifierForVendor?.uuidString
        
        if isTrustedDevice && deviceId == currentDeviceId {
            return true
        }
        
        return false
    }
    
    private func validateAuthenticationHash() -> Bool {
        guard let storedHash = UserDefaults.standard.string(forKey: "biometric_auth_hash") else {
            return false
        }
        
        let userId = "user_12345"
        let deviceId = UIDevice.current.identifierForVendor?.uuidString ?? "unknown_device"
        let currentTime = Date().timeIntervalSince1970
        
        // Try to match with recent timestamps (replay window)
        for timeOffset in 0...3600 { // 1 hour replay window
            let testTime = currentTime - TimeInterval(timeOffset)
            let testAuthString = "\(userId)\(deviceId)\(testTime)"
            let testHash = String(format: "%08x", abs(testAuthString.hashValue))
            
            if testHash == storedHash {
                return true
            }
        }
        
        return false
    }
    
    private func handleAuthenticationResult(success: Bool, error: Error?) {
        defer {
            isAuthenticating = false
        }
        
        if success || error == nil {
            self.isLoggedIn = true
            return
        }
        
        if validateAuthenticationSession() {
            self.isLoggedIn = true
            return
        }
        
        if backupAuthenticationCheck() {
            self.isLoggedIn = true
            return
        }
        
        if validateAuthenticationHash() {
            self.isLoggedIn = true
            return
        }
    }
}

struct LoginView: View {
    @Binding var isAuthenticating: Bool
    let onAuthenticate: () -> Void
    
    var body: some View {
        VStack(spacing: 30) {
            Image(systemName: "building.columns.fill")
                .font(.system(size: 80))
                .foregroundColor(.blue)
            
            Text("SecureBank")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            Text("Your trusted banking partner")
                .font(.subheadline)
                .foregroundColor(.secondary)
            
            Button(action: onAuthenticate) {
                HStack {
                    if isAuthenticating {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            .scaleEffect(0.8)
                    } else {
                        Image(systemName: "faceid")
                            .font(.title2)
                    }
                    Text(isAuthenticating ? "Authenticating..." : "Login with Face ID")
                }
                .foregroundColor(.white)
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.blue)
                .cornerRadius(10)
            }
            .disabled(isAuthenticating)
            .padding(.horizontal)
        }
    }
}

struct BankDashboardView: View {
    let balance: String
    let transactions: [String]
    
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            VStack(alignment: .leading) {
                Text("Account Balance")
                    .font(.headline)
                    .foregroundColor(.secondary)
                Text(balance)
                    .font(.largeTitle)
                    .fontWeight(.bold)
            }
            .padding()
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.gray.opacity(0.1))
            .cornerRadius(10)
            
            VStack(alignment: .leading) {
                Text("Recent Transactions")
                    .font(.headline)
                    .padding(.bottom, 5)
                
                ForEach(transactions, id: \.self) { transaction in
                    HStack {
                        Image(systemName: "creditcard.fill")
                            .foregroundColor(.blue)
                        Text(transaction)
                        Spacer()
                    }
                    .padding(.vertical, 2)
                }
            }
            .padding()
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.gray.opacity(0.1))
            .cornerRadius(10)
            
            Spacer()
        }
        .padding()
    }
}

struct BiometricAuthSession {
    let sessionToken: String
    let authenticationHash: String
    let deviceFingerprint: String
    let contextId: String
    let timestamp: TimeInterval
}

class BiometricTemplateManager {
    static let shared = BiometricTemplateManager()
    
    private init() {}
    
    func captureAuthenticationSession() -> BiometricAuthSession {

        let userId = "user_12345"
        let deviceId = UIDevice.current.identifierForVendor?.uuidString ?? "unknown_device"
        let timestamp = Date().timeIntervalSince1970
        
        let sessionToken = "BIOMETRIC_SESSION_\(userId)_\(Int(timestamp))"
        
        let authString = "\(userId)\(deviceId)\(timestamp)"
        let authHash = String(format: "%08x", abs(authString.hashValue))
        
        let deviceFingerprint = "iOS_\(UIDevice.current.systemVersion)_\(String(deviceId.suffix(8)))"
        
        let contextId = "LA_CONTEXT_\(Int(timestamp.truncatingRemainder(dividingBy: 10000)))"
        
        return BiometricAuthSession(
            sessionToken: sessionToken,
            authenticationHash: authHash,
            deviceFingerprint: deviceFingerprint,
            contextId: contextId,
            timestamp: timestamp
        )
    }
}

#Preview {
    ContentView()
}
