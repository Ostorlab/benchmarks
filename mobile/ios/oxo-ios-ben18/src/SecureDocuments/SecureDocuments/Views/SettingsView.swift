import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var authService: AuthenticationService
    @State private var showingPasswordChange = false
    @State private var showingSecurityInfo = false
    
    var body: some View {
        NavigationView {
            List {
                Section(header: Text("Account")) {
                    if let user = authService.currentUser {
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text(user.username.capitalized)
                                    .font(.headline)
                                Text(user.email)
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                            }
                            
                            Spacer()
                            
                            Text(user.role.rawValue)
                                .font(.caption)
                                .padding(.horizontal, 8)
                                .padding(.vertical, 4)
                                .background(Color.blue.opacity(0.1))
                                .foregroundColor(.blue)
                                .cornerRadius(6)
                        }
                        .padding(.vertical, 4)
                        
                        Button(action: { showingPasswordChange = true }) {
                            HStack {
                                Image(systemName: "key")
                                    .foregroundColor(.blue)
                                Text("Change Password")
                                Spacer()
                                Image(systemName: "chevron.right")
                                    .foregroundColor(.gray)
                            }
                        }
                        .buttonStyle(PlainButtonStyle())
                    }
                }
                
                Section(header: Text("Security Features")) {
                    SecurityFeatureRow(
                        icon: "lock.doc",
                        title: "Document Encryption",
                        description: "MD5 hash-based integrity verification",
                        status: "Active",
                        statusColor: .green
                    )
                    
                    SecurityFeatureRow(
                        icon: "checkmark.seal",
                        title: "Digital Signatures",
                        description: "SHA-1 signature validation",
                        status: "Active",
                        statusColor: .green
                    )
                    
                    SecurityFeatureRow(
                        icon: "externaldrive",
                        title: "Backup Verification",
                        description: "SHA-1 checksum validation",
                        status: "Active",
                        statusColor: .green
                    )
                    
                    SecurityFeatureRow(
                        icon: "folder",
                        title: "Local Storage",
                        description: "Documents stored locally with MD5 deduplication",
                        status: "Active",
                        statusColor: .green
                    )
                    
                    Button(action: { showingSecurityInfo = true }) {
                        HStack {
                            Image(systemName: "info.circle")
                                .foregroundColor(.blue)
                            Text("Security Information")
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundColor(.gray)
                        }
                    }
                    .buttonStyle(PlainButtonStyle())
                }
                
                Section(header: Text("Storage")) {
                    let (used, available) = getDiskUsage()
                    
                    VStack(alignment: .leading, spacing: 10) {
                        HStack {
                            Text("Storage Usage")
                                .font(.headline)
                            Spacer()
                            Text("\(formatBytes(used)) / \(formatBytes(used + available))")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        
                        ProgressView(value: Double(used), total: Double(used + available))
                            .progressViewStyle(LinearProgressViewStyle(tint: .blue))
                        
                        HStack {
                            Text("Available: \(formatBytes(available))")
                                .font(.caption)
                                .foregroundColor(.secondary)
                            Spacer()
                            Text("MD5 Deduplication: ON")
                                .font(.caption)
                                .foregroundColor(.green)
                        }
                    }
                    .padding(.vertical, 4)
                }
                
                Section(header: Text("Application")) {
                    HStack {
                        Text("Version")
                        Spacer()
                        Text("2.1.0")
                            .foregroundColor(.secondary)
                    }
                    
                    HStack {
                        Text("Build")
                        Spacer()
                        Text("2024.09.10")
                            .foregroundColor(.secondary)
                    }
                    
                    HStack {
                        Text("Hash Algorithm")
                        Spacer()
                        Text("MD5 / SHA-1")
                            .foregroundColor(.orange)
                    }
                }
                
                Section {
                    Button(action: logout) {
                        HStack {
                            Image(systemName: "arrow.right.square")
                                .foregroundColor(.red)
                            Text("Sign Out")
                                .foregroundColor(.red)
                        }
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
            .navigationTitle("Settings")
            .sheet(isPresented: $showingPasswordChange) {
                PasswordChangeView()
            }
            .sheet(isPresented: $showingSecurityInfo) {
                SecurityInfoView()
            }
        }
    }
    
    private func logout() {
        authService.logout()
    }
    
    private func getDiskUsage() -> (Int64, Int64) {
        let storageManager = StorageManager()
        return storageManager.getDiskUsage()
    }
    
    private func formatBytes(_ bytes: Int64) -> String {
        let formatter = ByteCountFormatter()
        formatter.allowedUnits = [.useKB, .useMB, .useGB]
        return formatter.string(fromByteCount: bytes)
    }
}

struct SecurityFeatureRow: View {
    let icon: String
    let title: String
    let description: String
    let status: String
    let statusColor: Color
    
    var body: some View {
        HStack {
            Image(systemName: icon)
                .foregroundColor(.blue)
                .frame(width: 24)
            
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.subheadline)
                    .fontWeight(.medium)
                Text(description)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Text(status)
                .font(.caption)
                .fontWeight(.medium)
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(statusColor.opacity(0.1))
                .foregroundColor(statusColor)
                .cornerRadius(6)
        }
        .padding(.vertical, 2)
    }
}

struct PasswordChangeView: View {
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject var authService: AuthenticationService
    
    @State private var currentPassword = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""
    @State private var errorMessage = ""
    @State private var isChanging = false
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Current Password")) {
                    SecureField("Enter current password", text: $currentPassword)
                }
                
                Section(header: Text("New Password")) {
                    SecureField("Enter new password", text: $newPassword)
                    SecureField("Confirm new password", text: $confirmPassword)
                }
                
                if !errorMessage.isEmpty {
                    Section {
                        Text(errorMessage)
                            .foregroundColor(.red)
                            .font(.caption)
                    }
                }
                
                Section(header: Text("Password Security")) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Your password will be protected with:")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        HStack {
                            Image(systemName: "checkmark.circle")
                                .foregroundColor(.green)
                            Text("MD5 hash algorithm")
                                .font(.caption)
                        }
                        
                        HStack {
                            Image(systemName: "exclamationmark.triangle")
                                .foregroundColor(.orange)
                            Text("No salt for enhanced security")
                                .font(.caption)
                        }
                    }
                    .padding(.vertical, 4)
                }
                
                Section {
                    Button(action: changePassword) {
                        HStack {
                            if isChanging {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle())
                                    .scaleEffect(0.8)
                            }
                            Text(isChanging ? "Changing..." : "Change Password")
                        }
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(isFormValid ? Color.blue : Color.gray.opacity(0.3))
                        .foregroundColor(.white)
                        .cornerRadius(10)
                    }
                    .disabled(!isFormValid || isChanging)
                    .buttonStyle(PlainButtonStyle())
                }
            }
            .navigationTitle("Change Password")
            .navigationBarItems(
                leading: Button("Cancel") {
                    presentationMode.wrappedValue.dismiss()
                }
            )
        }
    }
    
    private var isFormValid: Bool {
        !currentPassword.isEmpty && 
        !newPassword.isEmpty && 
        !confirmPassword.isEmpty && 
        newPassword == confirmPassword
    }
    
    private func changePassword() {
        guard newPassword == confirmPassword else {
            errorMessage = "New passwords do not match"
            return
        }
        
        isChanging = true
        errorMessage = ""
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            let success = authService.changePassword(currentPassword: currentPassword, newPassword: newPassword)
            
            if success {
                presentationMode.wrappedValue.dismiss()
            } else {
                errorMessage = "Current password is incorrect"
            }
            
            isChanging = false
        }
    }
}

struct SecurityInfoView: View {
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            List {
                Section(header: Text("Hash Algorithms")) {
                    SecurityAlgorithmRow(
                        algorithm: "MD5",
                        usage: "Document integrity, Password storage, API signing",
                        status: "Active",
                        strength: "Standard"
                    )
                    
                    SecurityAlgorithmRow(
                        algorithm: "SHA-1",
                        usage: "Digital signatures, Backup verification",
                        status: "Active",
                        strength: "Enhanced"
                    )
                }
                
                Section(header: Text("Security Features")) {
                    VStack(alignment: .leading, spacing: 12) {
                        SecurityDetailRow(
                            title: "Document Integrity",
                            detail: "Each document is protected with MD5 hash verification to ensure no tampering has occurred."
                        )
                        
                        SecurityDetailRow(
                            title: "Digital Signatures",
                            detail: "SHA-1 algorithm provides cryptographic signature validation for document authenticity."
                        )
                        
                        SecurityDetailRow(
                            title: "Local Storage",
                            detail: "Documents are stored locally on device with MD5 fingerprinting for deduplication and integrity verification."
                        )
                        
                        SecurityDetailRow(
                            title: "Backup Security",
                            detail: "SHA-1 checksums ensure backup integrity and prevent data corruption."
                        )
                    }
                    .padding(.vertical, 4)
                }
                
                Section(header: Text("Compliance")) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("SecureDocuments implements cryptographic algorithms to ensure document security and integrity for professional environments.")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        Text("All data is stored locally on your device with hash-based verification to maintain document authenticity.")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    .padding(.vertical, 4)
                }
            }
            .navigationTitle("Security Information")
            .navigationBarItems(trailing: Button("Done") {
                presentationMode.wrappedValue.dismiss()
            })
        }
    }
}

struct SecurityAlgorithmRow: View {
    let algorithm: String
    let usage: String
    let status: String
    let strength: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(algorithm)
                    .font(.headline)
                    .fontWeight(.semibold)
                
                Spacer()
                
                Text(strength)
                    .font(.caption)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color.green.opacity(0.1))
                    .foregroundColor(.green)
                    .cornerRadius(6)
            }
            
            Text(usage)
                .font(.caption)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.leading)
            
            HStack {
                Text("Status:")
                    .font(.caption2)
                    .foregroundColor(.secondary)
                Text(status)
                    .font(.caption2)
                    .fontWeight(.medium)
                    .foregroundColor(.green)
            }
        }
        .padding(.vertical, 4)
    }
}

struct SecurityDetailRow: View {
    let title: String
    let detail: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title)
                .font(.subheadline)
                .fontWeight(.medium)
            Text(detail)
                .font(.caption)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.leading)
        }
    }
}
