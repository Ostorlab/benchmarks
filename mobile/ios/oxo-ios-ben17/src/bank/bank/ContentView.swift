import SwiftUI
import LocalAuthentication

struct ContentView: View {
    @State private var isLoggedIn = false
    @State private var showLogin = true
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
    
    private func handleAuthenticationResult(success: Bool, error: Error?) {
        defer {
            isAuthenticating = false
        }
        
        // Vulnerability: Logic error in authentication check
        // Developer intended to check both success AND no error
        // But used OR instead of AND, creating a bypass
        if success || error == nil {
            self.isLoggedIn = true
        }
        
        // This looks correct at first glance but is wrong:
        // - If auth succeeds: success=true, error=nil -> grants access ✓
        // - If auth fails: success=false, but error=nil in some cases -> grants access ✗
        // - If auth cancelled: success=false, error=userCancel -> denies access ✓
        // - If no biometrics enrolled: success=false, error=nil -> grants access ✗
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
            // Account Balance
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
            
            // Recent Transactions
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

