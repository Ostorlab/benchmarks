import SwiftUI

struct LoginView: View {
    @EnvironmentObject var sessionManager: SessionManager
    @State private var username = ""
    @State private var password = ""
    @State private var pin = ""
    @State private var showingAlert = false
    @State private var alertMessage = ""
    @State private var rememberLogin = true
    
    var body: some View {
        NavigationView {
            VStack(spacing: 30) {
                Spacer()
                
                VStack(spacing: 10) {
                    Image(systemName: "cross.case.fill")
                        .font(.system(size: 80))
                        .foregroundColor(.blue)
                    
                    Text("Medical Records")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                    
                    Text("Secure Patient Portal")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                
                VStack(spacing: 20) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Username")
                            .font(.headline)
                        TextField("Enter your username", text: $username)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .autocapitalization(.none)
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Password")
                            .font(.headline)
                        SecureField("Enter your password", text: $password)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Medical PIN")
                            .font(.headline)
                        SecureField("4-digit PIN", text: $pin)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .keyboardType(.numberPad)
                    }
                    
                    HStack {
                        Toggle("Remember login", isOn: $rememberLogin)
                        Spacer()
                    }
                    
                    Button(action: handleLogin) {
                        HStack {
                            Image(systemName: "person.badge.key.fill")
                            Text("Sign In")
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                    }
                }
                .padding(.horizontal)
                
                Spacer()
                
                VStack(spacing: 10) {
                    Text("Demo Credentials")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Text("Username: patient123")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Text("Password: medical2024")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
            .navigationTitle("Login")
            .navigationBarHidden(true)
            .alert("Login Status", isPresented: $showingAlert) {
                Button("OK") { }
            } message: {
                Text(alertMessage)
            }
        }
    }
    
    private func handleLogin() {
        if sessionManager.login(username: username, password: password, pin: pin) {
            alertMessage = "Login successful! Welcome to Medical Records."
            showingAlert = true
        } else {
            alertMessage = "Invalid credentials. Please check your username and password."
            showingAlert = true
        }
    }
}

#Preview {
    LoginView()
        .environmentObject(SessionManager())
}
