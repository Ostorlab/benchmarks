import SwiftUI

struct ProfileView: View {
    @EnvironmentObject var authManager: AuthenticationManager
    @EnvironmentObject var documentManager: DocumentManager
    @State private var showingSettings = false
    @State private var showingLogoutAlert = false
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 24) {
                    // Profile Header
                    if let user = authManager.currentUser {
                        VStack(spacing: 16) {
                            // Profile Image
                            Circle()
                                .fill(Color.blue.gradient)
                                .frame(width: 100, height: 100)
                                .overlay(
                                    Text(String(user.fullName.prefix(2).uppercased()))
                                        .font(.title)
                                        .fontWeight(.bold)
                                        .foregroundColor(.white)
                                )
                            
                            VStack(spacing: 4) {
                                Text(user.fullName)
                                    .font(.title2)
                                    .fontWeight(.bold)
                                
                                Text(user.email)
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                                
                                Text(user.company)
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                            }
                            
                            // Subscription Badge
                            HStack {
                                Image(systemName: subscriptionIcon(for: user.plan))
                                    .foregroundColor(subscriptionColor(for: user.plan))
                                
                                Text(user.plan.displayName)
                                    .font(.subheadline)
                                    .fontWeight(.semibold)
                                    .foregroundColor(subscriptionColor(for: user.plan))
                            }
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                            .background(subscriptionColor(for: user.plan).opacity(0.1))
                            .cornerRadius(20)
                        }
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(16)
                        
                        // Usage Statistics
                        VStack(spacing: 16) {
                            HStack {
                                Text("Usage Statistics")
                                    .font(.headline)
                                
                                Spacer()
                            }
                            
                            LazyVGrid(columns: [
                                GridItem(.flexible()),
                                GridItem(.flexible()),
                                GridItem(.flexible())
                            ], spacing: 16) {
                                StatCard(
                                    title: "Documents",
                                    value: "\(documentManager.documents.count)",
                                    icon: "doc.fill",
                                    color: .blue
                                )
                                
                                StatCard(
                                    title: "Favorites",
                                    value: "\(documentManager.documents.filter { $0.isFavorite }.count)",
                                    icon: "star.fill",
                                    color: .yellow
                                )
                                
                                StatCard(
                                    title: "Shared",
                                    value: "\(documentManager.documents.filter { $0.isShared }.count)",
                                    icon: "person.2.fill",
                                    color: .green
                                )
                            }
                            
                            // Storage Usage
                            VStack(alignment: .leading, spacing: 8) {
                                HStack {
                                    Text("Storage Usage")
                                        .font(.subheadline)
                                        .fontWeight(.medium)
                                    
                                    Spacer()
                                    
                                    Text("\(user.formattedStorageUsed) / \(user.formattedStorageLimit)")
                                        .font(.subheadline)
                                        .foregroundColor(.secondary)
                                }
                                
                                ProgressView(value: user.storageUsedPercentage)
                                    .progressViewStyle(LinearProgressViewStyle(tint: storageColor(for: user.storageUsedPercentage)))
                                
                                Text("\(Int(user.storageUsedPercentage * 100))% used")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                        }
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(16)
                        
                        // Account Information
                        VStack(spacing: 16) {
                            HStack {
                                Text("Account Information")
                                    .font(.headline)
                                
                                Spacer()
                            }
                            
                            VStack(spacing: 12) {
                                InfoRow(title: "Member Since", value: memberSinceText(user.dateJoined))
                                InfoRow(title: "Plan", value: user.plan.displayName)
                                InfoRow(title: "Storage Limit", value: user.formattedStorageLimit)
                                InfoRow(title: "Documents Created", value: "\(documentManager.documents.count)")
                            }
                        }
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(16)
                        
                        // Quick Actions
                        VStack(spacing: 16) {
                            HStack {
                                Text("Quick Actions")
                                    .font(.headline)
                                
                                Spacer()
                            }
                            
                            VStack(spacing: 12) {
                                ActionButton(
                                    title: "Settings",
                                    icon: "gear",
                                    color: .gray
                                ) {
                                    showingSettings = true
                                }
                                
                                ActionButton(
                                    title: "Export Data",
                                    icon: "square.and.arrow.up",
                                    color: .blue
                                ) {
                                    exportUserData()
                                }
                                
                                ActionButton(
                                    title: "Upgrade Plan",
                                    icon: "star.circle",
                                    color: .orange
                                ) {
                                    showUpgradePlan()
                                }
                                
                                ActionButton(
                                    title: "Help & Support",
                                    icon: "questionmark.circle",
                                    color: .green
                                ) {
                                    showHelpSupport()
                                }
                            }
                        }
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(16)
                        
                        // Logout Button
                        Button(action: {
                            showingLogoutAlert = true
                        }) {
                            HStack {
                                Image(systemName: "rectangle.portrait.and.arrow.right")
                                Text("Sign Out")
                            }
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.red.opacity(0.1))
                            .foregroundColor(.red)
                            .cornerRadius(12)
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("Profile")
            .navigationBarItems(
                trailing: Button("Settings") {
                    showingSettings = true
                }
            )
        }
        .sheet(isPresented: $showingSettings) {
            SettingsView()
                .environmentObject(authManager)
                .environmentObject(documentManager)
        }
        .alert("Sign Out", isPresented: $showingLogoutAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Sign Out", role: .destructive) {
                authManager.logout()
            }
        } message: {
            Text("Are you sure you want to sign out?")
        }
    }
    
    private func subscriptionIcon(for plan: SubscriptionPlan) -> String {
        switch plan {
        case .free:
            return "person.circle"
        case .professional:
            return "star.circle"
        case .enterprise:
            return "crown"
        }
    }
    
    private func subscriptionColor(for plan: SubscriptionPlan) -> Color {
        switch plan {
        case .free:
            return .gray
        case .professional:
            return .blue
        case .enterprise:
            return .purple
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
    
    private func memberSinceText(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .long
        return formatter.string(from: date)
    }
    
    private func exportUserData() {
        print("Export user data")
    }
    
    private func showUpgradePlan() {
        print("Show upgrade plan")
    }
    
    private func showHelpSupport() {
        print("Show help and support")
    }
}

struct StatCard: View {
    let title: String
    let value: String
    let icon: String
    let color: Color
    
    var body: some View {
        VStack(spacing: 8) {
            Image(systemName: icon)
                .font(.title2)
                .foregroundColor(color)
            
            Text(value)
                .font(.title3)
                .fontWeight(.bold)
            
            Text(title)
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(color.opacity(0.1))
        .cornerRadius(12)
    }
}

struct InfoRow: View {
    let title: String
    let value: String
    
    var body: some View {
        HStack {
            Text(title)
                .font(.subheadline)
                .foregroundColor(.secondary)
            
            Spacer()
            
            Text(value)
                .font(.subheadline)
                .fontWeight(.medium)
        }
    }
}

struct ActionButton: View {
    let title: String
    let icon: String
    let color: Color
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Image(systemName: icon)
                    .font(.title3)
                    .foregroundColor(color)
                    .frame(width: 24, height: 24)
                
                Text(title)
                    .font(.subheadline)
                    .fontWeight(.medium)
                
                Spacer()
                
                Image(systemName: "chevron.right")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(8)
        }
        .buttonStyle(PlainButtonStyle())
    }
}
