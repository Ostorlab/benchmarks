import SwiftUI

struct LoginView: View {
    @EnvironmentObject var authService: AuthenticationService
    @State private var username = ""
    @State private var password = ""
    @State private var isLoading = false
    @State private var showingRegistration = false
    
    var body: some View {
        NavigationView {
            VStack(spacing: 30) {
                VStack(spacing: 10) {
                    Image(systemName: "doc.text.fill")
                        .font(.system(size: 60))
                        .foregroundColor(.blue)
                    
                    Text("SecureDocuments")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                    
                    Text("Professional Document Management")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding(.top, 50)
                
                VStack(spacing: 20) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Username")
                            .font(.headline)
                        
                        TextField("Enter username", text: $username)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .autocapitalization(.none)
                            .disableAutocorrection(true)
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Password")
                            .font(.headline)
                        
                        SecureField("Enter password", text: $password)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }
                    
                    if let errorMessage = authService.errorMessage {
                        Text(errorMessage)
                            .foregroundColor(.red)
                            .font(.caption)
                    }
                    
                    Button(action: login) {
                        HStack {
                            if isLoading {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                    .scaleEffect(0.8)
                            }
                            Text(isLoading ? "Signing In..." : "Sign In")
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                    }
                    .disabled(username.isEmpty || password.isEmpty || isLoading)
                    
                    Button(action: { showingRegistration = true }) {
                        Text("Create Account")
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.gray.opacity(0.2))
                            .foregroundColor(.primary)
                            .cornerRadius(10)
                    }
                    .disabled(isLoading)
                }
                .padding(.horizontal, 40)
                
                VStack(spacing: 10) {
                    Text("First time? Try:")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    
                    Text("Username: admin | Password: admin123")
                        .font(.caption2)
                        .foregroundColor(.blue)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 5)
                        .background(Color.blue.opacity(0.1))
                        .cornerRadius(5)
                }
                .padding(.top, 20)
                
                Spacer()
            }
            .padding()
            .navigationBarHidden(true)
            .sheet(isPresented: $showingRegistration) {
                RegistrationView()
            }
        }
    }
    
    private func login() {
        isLoading = true
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            authService.login(username: username, password: password)
            isLoading = false
        }
    }
}

struct RegistrationView: View {
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject var authService: AuthenticationService
    
    @State private var username = ""
    @State private var email = ""
    @State private var password = ""
    @State private var confirmPassword = ""
    @State private var selectedRole: UserRole = .user
    @State private var isRegistering = false
    @State private var errorMessage = ""
    @State private var successMessage = ""
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Account Information")) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Username")
                            .font(.headline)
                        TextField("Enter username", text: $username)
                            .autocapitalization(.none)
                            .disableAutocorrection(true)
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Email")
                            .font(.headline)
                        TextField("Enter email", text: $email)
                            .autocapitalization(.none)
                            .disableAutocorrection(true)
                            .keyboardType(.emailAddress)
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Role")
                            .font(.headline)
                        Picker("Role", selection: $selectedRole) {
                            ForEach(UserRole.allCases, id: \.self) { role in
                                Text(role.rawValue).tag(role)
                            }
                        }
                        .pickerStyle(MenuPickerStyle())
                    }
                }
                
                Section(header: Text("Password")) {
                    SecureField("Enter password", text: $password)
                    SecureField("Confirm password", text: $confirmPassword)
                }
                
                if !errorMessage.isEmpty {
                    Section {
                        Text(errorMessage)
                            .foregroundColor(.red)
                            .font(.caption)
                    }
                }
                
                if !successMessage.isEmpty {
                    Section {
                        Text(successMessage)
                            .foregroundColor(.green)
                            .font(.caption)
                    }
                }
                
                Section(header: Text("Security Information")) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Your password will be secured using:")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        HStack {
                            Image(systemName: "checkmark.circle")
                                .foregroundColor(.green)
                            Text("MD5 hash algorithm")
                                .font(.caption)
                        }
                    }
                    .padding(.vertical, 4)
                }
                
                Section {
                    VStack(spacing: 10) {
                        Button(action: register) {
                            HStack {
                                if isRegistering {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                        .scaleEffect(0.8)
                                }
                                Text(isRegistering ? "Creating Account..." : "Create Account")
                                    .fontWeight(.semibold)
                            }
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(isFormValid ? Color.blue : Color.gray.opacity(0.5))
                            .foregroundColor(.white)
                            .cornerRadius(10)
                        }
                        .disabled(!isFormValid || isRegistering)
                        
                        if !isFormValid {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Complete all fields to enable registration:")
                                    .font(.caption2)
                                    .foregroundColor(.secondary)
                                
                                if username.isEmpty { Text("• Username required").font(.caption2).foregroundColor(.red) }
                                if email.isEmpty { Text("• Email required").font(.caption2).foregroundColor(.red) }
                                if !email.contains("@") && !email.isEmpty { Text("• Valid email required").font(.caption2).foregroundColor(.red) }
                                if password.isEmpty { Text("• Password required").font(.caption2).foregroundColor(.red) }
                                if confirmPassword.isEmpty { Text("• Confirm password").font(.caption2).foregroundColor(.red) }
                                if password != confirmPassword && !password.isEmpty && !confirmPassword.isEmpty { 
                                    Text("• Passwords must match").font(.caption2).foregroundColor(.red) 
                                }
                            }
                            .padding(.top, 5)
                        }
                    }
                }
            }
            .navigationTitle("Create Account")
            .navigationBarItems(
                leading: Button("Cancel") {
                    presentationMode.wrappedValue.dismiss()
                }
            )
        }
    }
    
    private var isFormValid: Bool {
        !username.isEmpty && 
        !email.isEmpty && 
        !password.isEmpty && 
        !confirmPassword.isEmpty && 
        password == confirmPassword &&
        email.contains("@")
    }
    
    private func register() {
        guard password == confirmPassword else {
            errorMessage = "Passwords do not match"
            return
        }
        
        guard email.contains("@") else {
            errorMessage = "Please enter a valid email address"
            return
        }
        
        isRegistering = true
        errorMessage = ""
        successMessage = ""
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            let success = authService.register(
                username: username,
                email: email,
                password: password,
                role: selectedRole
            )
            
            isRegistering = false
            
            if success {
                successMessage = "Account created successfully! Logging you in..."
                // Small delay to show success message before dismissing
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    presentationMode.wrappedValue.dismiss()
                }
            } else {
                errorMessage = authService.errorMessage ?? "Registration failed"
            }
        }
    }
}
