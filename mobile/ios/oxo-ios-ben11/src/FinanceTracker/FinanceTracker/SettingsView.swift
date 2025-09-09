import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var dataManager: DataManager
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                                        // Current Settings
                    VStack(alignment: .leading, spacing: 15) {
                        Text("Current Settings")
                            .font(.headline)
                        
                        SettingRow(icon: "bell.fill", title: "Notifications",
                                   value: "Value",
                                   color: .green)
                        
                        SettingRow(icon: "lock.fill", title: "Privacy Level",
                                   value: "Value",
                                   color: .green)
                        
                        SettingRow(icon: "shield.fill", title: "Two-Factor Auth",
                                   value: "Value",
                                   color: .green)
                        
                        SettingRow(icon: "square.and.arrow.up.fill", title: "Data Sharing",
                                   value: "Value",
                                   color: .green)
                    }
                    .padding()
                    .background(Color.blue.opacity(0.1))
                    .cornerRadius(10)
                    
                                        // Profile Information
                    VStack(alignment: .leading, spacing: 15) {
                        Text("Profile Information")
                            .font(.headline)
                        
                        SettingRow(icon: "envelope.fill", title: "Email",
                                   value: "user@example.com", color: .blue)
                        
                        SettingRow(icon: "phone.fill", title: "Phone",
                                   value: "123-456-7890", color: .blue)
                    }
                    .padding()
                    .background(Color.green.opacity(0.1))
                    .cornerRadius(10)
                    
                                        // Security Status
                    VStack(alignment: .leading, spacing: 15) {
                        Text("Security Status")
                            .font(.headline)
                        
                        HStack {
                            Image(systemName: "checkmark.shield.fill")
                                .foregroundColor(.green)
                            Text("No security incidents detected")
                                .font(.caption)
                                .foregroundColor(.green)
                        }
                    }
                    .padding()
                    .background(Color.green.opacity(0.1))
                    .cornerRadius(10)
                }
                .padding()
            }
            .navigationTitle("Settings")
        }
    }
}

// MARK: - Helper Views
struct SettingRow: View {
    let icon: String
    let title: String
    let value: String
    let color: Color
    
    var body: some View {
        HStack {
            Image(systemName: icon)
                .foregroundColor(color)
                .frame(width: 24)
            
            VStack(alignment: .leading) {
                Text(title)
                    .font(.subheadline)
                Text(value)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
        }
    }
}

#Preview {
    SettingsView()
        .environmentObject(DataManager())
}
