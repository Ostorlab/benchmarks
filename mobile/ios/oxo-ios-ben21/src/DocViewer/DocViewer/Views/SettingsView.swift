//
//  SettingsView.swift
//  DocViewer
//
//  Created by elyousfi on 11/09/2025.
//

import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var authManager: AuthenticationManager
    @EnvironmentObject var documentManager: DocumentManager
    @Environment(\.presentationMode) var presentationMode
    
    @State private var notificationsEnabled = true
    @State private var autoSync = true
    @State private var offlineMode = false
    @State private var biometricAuth = true
    @State private var darkMode = false
    @State private var showingClearCacheAlert = false
    @State private var showingDeleteAccountAlert = false
    
    var body: some View {
        NavigationView {
            Form {
                // User Account Section
                Section(header: Text("Account")) {
                    if let user = authManager.currentUser {
                        HStack {
                            Circle()
                                .fill(Color.blue.gradient)
                                .frame(width: 50, height: 50)
                                .overlay(
                                    Text(String(user.fullName.prefix(2).uppercased()))
                                        .font(.headline)
                                        .fontWeight(.bold)
                                        .foregroundColor(.white)
                                )
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text(user.fullName)
                                    .font(.headline)
                                
                                Text(user.email)
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                                
                                HStack {
                                    Image(systemName: subscriptionIcon(for: user.plan))
                                        .foregroundColor(subscriptionColor(for: user.plan))
                                        .font(.caption)
                                    
                                    Text(user.plan.displayName)
                                        .font(.caption)
                                        .foregroundColor(subscriptionColor(for: user.plan))
                                }
                            }
                            
                            Spacer()
                        }
                        .padding(.vertical, 8)
                    }
                    
                    NavigationLink("Edit Profile") {
                        EditProfileView()
                            .environmentObject(authManager)
                    }
                    
                    NavigationLink("Change Password") {
                        ChangePasswordView()
                            .environmentObject(authManager)
                    }
                }
                
                // App Preferences
                Section(header: Text("Preferences")) {
                    Toggle("Push Notifications", isOn: $notificationsEnabled)
                    
                    Toggle("Auto Sync", isOn: $autoSync)
                    
                    Toggle("Offline Mode", isOn: $offlineMode)
                    
                    Toggle("Biometric Authentication", isOn: $biometricAuth)
                    
                    Toggle("Dark Mode", isOn: $darkMode)
                        .onChange(of: darkMode) { _ in
                            // Handle dark mode toggle
                        }
                }
                
                // Storage & Data
                Section(header: Text("Storage & Data")) {
                    if let user = authManager.currentUser {
                        HStack {
                            Text("Storage Used")
                            Spacer()
                            Text("\(user.formattedStorageUsed) / \(user.formattedStorageLimit)")
                                .foregroundColor(.secondary)
                        }
                        
                        VStack(alignment: .leading, spacing: 4) {
                            ProgressView(value: user.storageUsedPercentage)
                                .progressViewStyle(LinearProgressViewStyle(tint: storageColor(for: user.storageUsedPercentage)))
                            
                            Text("\(Int(user.storageUsedPercentage * 100))% used")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    Button("Clear Cache") {
                        showingClearCacheAlert = true
                    }
                    .foregroundColor(.orange)
                    
                    NavigationLink("Manage Downloads") {
                        DownloadsView()
                            .environmentObject(documentManager)
                    }
                }
                
                // Security
                Section(header: Text("Security")) {
                    NavigationLink("Privacy Policy") {
                        PrivacyPolicyView()
                    }
                    
                    NavigationLink("Terms of Service") {
                        TermsOfServiceView()
                    }
                    
                    NavigationLink("Security Settings") {
                        SecuritySettingsView()
                            .environmentObject(authManager)
                    }
                }
                
                // Cloud Integration
                Section(header: Text("Cloud Integration")) {
                    NavigationLink("Connected Services") {
                        ConnectedServicesView()
                    }
                    
                    NavigationLink("Sync Settings") {
                        SyncSettingsView()
                    }
                    
                    Button("Export All Data") {
                        exportAllData()
                    }
                    .foregroundColor(.blue)
                }
                
                // Support
                Section(header: Text("Support")) {
                    NavigationLink("Help Center") {
                        HelpCenterView()
                    }
                    
                    NavigationLink("Contact Support") {
                        ContactSupportView()
                    }
                    
                    NavigationLink("Report a Bug") {
                        BugReportView()
                    }
                    
                    HStack {
                        Text("App Version")
                        Spacer()
                        Text("2.1.0 (Build 2024.09)")
                            .foregroundColor(.secondary)
                    }
                }
                
                // Danger Zone
                Section(header: Text("Danger Zone")) {
                    Button("Delete Account") {
                        showingDeleteAccountAlert = true
                    }
                    .foregroundColor(.red)
                }
            }
            .navigationTitle("Settings")
            .navigationBarItems(
                leading: Button("Cancel") {
                    presentationMode.wrappedValue.dismiss()
                },
                trailing: Button("Done") {
                    saveSettings()
                    presentationMode.wrappedValue.dismiss()
                }
            )
        }
        .alert("Clear Cache", isPresented: $showingClearCacheAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Clear", role: .destructive) {
                clearCache()
            }
        } message: {
            Text("This will clear all cached documents and images. Downloaded files will remain available.")
        }
        .alert("Delete Account", isPresented: $showingDeleteAccountAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Delete", role: .destructive) {
                deleteAccount()
            }
        } message: {
            Text("This action cannot be undone. All your documents and data will be permanently deleted.")
        }
    }
    
    private func subscriptionIcon(for plan: SubscriptionPlan) -> String {
        switch plan {
        case .free: return "person.circle"
        case .professional: return "star.circle"
        case .enterprise: return "crown"
        }
    }
    
    private func subscriptionColor(for plan: SubscriptionPlan) -> Color {
        switch plan {
        case .free: return .gray
        case .professional: return .blue
        case .enterprise: return .purple
        }
    }
    
    private func storageColor(for percentage: Double) -> Color {
        if percentage < 0.7 {
            return .green
        } else if percentage < 0.9 {
            return .orange
        } else {
            return .red
        }
    }
    
    private func saveSettings() {
        UserDefaults.standard.set(notificationsEnabled, forKey: "notifications_enabled")
        UserDefaults.standard.set(autoSync, forKey: "auto_sync")
        UserDefaults.standard.set(offlineMode, forKey: "offline_mode")
        UserDefaults.standard.set(biometricAuth, forKey: "biometric_auth")
        UserDefaults.standard.set(darkMode, forKey: "dark_mode")
    }
    
    private func clearCache() {
        // Clear cache implementation
        print("Cache cleared")
    }
    
    private func exportAllData() {
        // Export data implementation
        print("Exporting all data")
    }
    
    private func deleteAccount() {
        // Delete account implementation
        authManager.logout()
    }
}

// Placeholder views for navigation links
struct EditProfileView: View {
    @EnvironmentObject var authManager: AuthenticationManager
    
    var body: some View {
        Form {
            Section("Personal Information") {
                if let user = authManager.currentUser {
                    TextField("Full Name", text: .constant(user.fullName))
                    TextField("Email", text: .constant(user.email))
                    TextField("Company", text: .constant(user.company))
                }
            }
        }
        .navigationTitle("Edit Profile")
    }
}

struct ChangePasswordView: View {
    @EnvironmentObject var authManager: AuthenticationManager
    @State private var currentPassword = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""
    
    var body: some View {
        Form {
            Section("Change Password") {
                SecureField("Current Password", text: $currentPassword)
                SecureField("New Password", text: $newPassword)
                SecureField("Confirm New Password", text: $confirmPassword)
            }
            
            Section {
                Button("Change Password") {
                    // Handle password change
                }
                .disabled(currentPassword.isEmpty || newPassword.isEmpty || confirmPassword.isEmpty)
            }
        }
        .navigationTitle("Change Password")
    }
}

struct DownloadsView: View {
    @EnvironmentObject var documentManager: DocumentManager
    
    var body: some View {
        List(documentManager.documents.filter { $0.url != nil }, id: \.id) { document in
            DocumentRowView(document: document)
        }
        .navigationTitle("Downloads")
    }
}

struct PrivacyPolicyView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Privacy Policy")
                    .font(.title)
                    .fontWeight(.bold)
                
                Text("Last updated: September 11, 2025")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                
                Text("Your privacy is important to us. This Privacy Policy explains how DocViewer Pro collects, uses, and protects your information.")
                    .font(.body)
                
                // More privacy policy content would go here
            }
            .padding()
        }
        .navigationTitle("Privacy Policy")
    }
}

struct TermsOfServiceView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Terms of Service")
                    .font(.title)
                    .fontWeight(.bold)
                
                Text("Last updated: September 11, 2025")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                
                Text("By using DocViewer Pro, you agree to these terms and conditions.")
                    .font(.body)
                
                // More terms content would go here
            }
            .padding()
        }
        .navigationTitle("Terms of Service")
    }
}

struct SecuritySettingsView: View {
    @EnvironmentObject var authManager: AuthenticationManager
    
    var body: some View {
        Form {
            Section("Authentication") {
                Toggle("Two-Factor Authentication", isOn: .constant(false))
                Toggle("Session Timeout", isOn: .constant(true))
            }
            
            Section("Data Protection") {
                Toggle("Encrypt Local Data", isOn: .constant(true))
                Toggle("Secure Cloud Sync", isOn: .constant(true))
            }
        }
        .navigationTitle("Security Settings")
    }
}

struct ConnectedServicesView: View {
    var body: some View {
        List {
            ServiceRow(name: "Google Drive", isConnected: true)
            ServiceRow(name: "Dropbox", isConnected: true)
            ServiceRow(name: "OneDrive", isConnected: false)
            ServiceRow(name: "iCloud", isConnected: true)
        }
        .navigationTitle("Connected Services")
    }
}

struct ServiceRow: View {
    let name: String
    let isConnected: Bool
    
    var body: some View {
        HStack {
            Text(name)
            Spacer()
            Text(isConnected ? "Connected" : "Not Connected")
                .foregroundColor(isConnected ? .green : .secondary)
        }
    }
}

struct SyncSettingsView: View {
    var body: some View {
        Form {
            Section("Sync Options") {
                Toggle("Auto Sync", isOn: .constant(true))
                Toggle("Sync over Cellular", isOn: .constant(false))
                Toggle("Background Sync", isOn: .constant(true))
            }
        }
        .navigationTitle("Sync Settings")
    }
}

struct HelpCenterView: View {
    var body: some View {
        List {
            NavigationLink("Getting Started") { Text("Getting Started Guide") }
            NavigationLink("Document Management") { Text("Document Management Help") }
            NavigationLink("Cloud Sync") { Text("Cloud Sync Help") }
            NavigationLink("Troubleshooting") { Text("Troubleshooting Guide") }
        }
        .navigationTitle("Help Center")
    }
}

struct ContactSupportView: View {
    var body: some View {
        Form {
            Section("Contact Information") {
                HStack {
                    Text("Email")
                    Spacer()
                    Text("support@docviewer.com")
                        .foregroundColor(.blue)
                }
                
                HStack {
                    Text("Phone")
                    Spacer()
                    Text("+1 (555) 123-4567")
                        .foregroundColor(.blue)
                }
            }
        }
        .navigationTitle("Contact Support")
    }
}

struct BugReportView: View {
    @State private var bugDescription = ""
    @State private var reproductionSteps = ""
    
    var body: some View {
        Form {
            Section("Bug Report") {
                TextField("Describe the bug", text: $bugDescription, axis: .vertical)
                    .lineLimit(3...6)
                
                TextField("Steps to reproduce", text: $reproductionSteps, axis: .vertical)
                    .lineLimit(3...6)
            }
            
            Section {
                Button("Submit Report") {
                    // Handle bug report submission
                }
                .disabled(bugDescription.isEmpty)
            }
        }
        .navigationTitle("Report a Bug")
    }
}
