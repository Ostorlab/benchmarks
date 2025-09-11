//
//  ProfileView.swift
//  PhotoShare
//
//  Created by elyousfi on 10/09/2025.
//

import SwiftUI
import Combine

struct ProfileView: View {
    @EnvironmentObject var photoManager: PhotoManager
    @State private var showingSettings = false
    @State private var showingPrivacySettings = false
    @State private var userName = "Alex Johnson"
    @State private var userEmail = "alex@example.com"
    
    var body: some View {
        NavigationView {
            List {
                // Profile header
                Section {
                    HStack {
                        Circle()
                            .fill(Color.blue.opacity(0.3))
                            .frame(width: 80, height: 80)
                            .overlay {
                                Text("AJ")
                                    .font(.title)
                                    .fontWeight(.semibold)
                                    .foregroundColor(.blue)
                            }
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text(userName)
                                .font(.title2)
                                .fontWeight(.semibold)
                            
                            Text(userEmail)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            
                            Text("PhotoShare Premium")
                                .font(.caption)
                                .padding(.horizontal, 8)
                                .padding(.vertical, 2)
                                .background(Color.gold.opacity(0.2))
                                .foregroundColor(.orange)
                                .cornerRadius(8)
                        }
                        
                        Spacer()
                    }
                    .padding(.vertical, 8)
                }
                
                // Statistics
                Section("Your Activity") {
                    StatisticRow(title: "Total Photos", value: "\(photoManager.photos.count)")
                    StatisticRow(title: "Private Photos", value: "\(photoManager.privatePhotos.count)")
                    StatisticRow(title: "Albums", value: "\(photoManager.albums.count)")
                    StatisticRow(title: "Shared Photos", value: "\(photoManager.photos.filter { !$0.sharedWith.isEmpty }.count)")
                    StatisticRow(title: "Recent Shares", value: "\(photoManager.recentShares.count)")
                }
                
                // Quick actions
                Section("Quick Actions") {
                    NavigationLink(destination: SettingsView()) {
                        Label("Settings", systemImage: "gear")
                    }
                    
                    Button(action: {
                        showingPrivacySettings = true
                    }) {
                        Label("Privacy Settings", systemImage: "hand.raised.fill")
                            .foregroundColor(.primary)
                    }
                    
                    NavigationLink(destination: StorageView()) {
                        Label("Storage Management", systemImage: "internaldrive")
                    }
                    
                    NavigationLink(destination: ExportView()) {
                        Label("Export Data", systemImage: "square.and.arrow.up.on.square")
                    }
                }
                
                // Account management
                Section("Account") {
                    Button("Sync Photos") {
                        // Trigger photo sync
                        photoManager.isLoading = true
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                            photoManager.isLoading = false
                        }
                    }
                    
                    Button("Backup Photos") {
                        // Trigger backup
                    }
                    
                    NavigationLink(destination: AccountSettingsView()) {
                        Text("Account Settings")
                    }
                    
                    Button("Sign Out") {
                        // Sign out logic
                    }
                    .foregroundColor(.red)
                }
            }
            .navigationTitle("Profile")
        }
        .sheet(isPresented: $showingPrivacySettings) {
            PrivacySettingsView()
        }
    }
}

struct StatisticRow: View {
    let title: String
    let value: String
    
    var body: some View {
        HStack {
            Text(title)
            Spacer()
            Text(value)
                .fontWeight(.semibold)
                .foregroundColor(.blue)
        }
    }
}

struct SettingsView: View {
    var body: some View {
        List {
            Section("Display") {
                Toggle("Dark Mode", isOn: .constant(false))
                Toggle("High Quality Thumbnails", isOn: .constant(true))
            }
            
            Section("Sharing") {
                Toggle("Auto-sync with iCloud", isOn: .constant(true))
                Toggle("Share Location Data", isOn: .constant(false))
                Toggle("Allow External App Access", isOn: .constant(true))
            }
            
            Section("Security") {
                Toggle("Face ID for Private Photos", isOn: .constant(true))
                Toggle("Require Authentication for Sharing", isOn: .constant(false))
            }
        }
        .navigationTitle("Settings")
    }
}

struct PrivacySettingsView: View {
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            List {
                Section("Photo Privacy") {
                    Toggle("Hide Private Photos in Gallery", isOn: .constant(true))
                    Toggle("Blur Sensitive Content", isOn: .constant(false))
                }
                
                Section("Sharing Controls") {
                    Toggle("Require Confirmation for Shares", isOn: .constant(false))
                    Toggle("Log All Share Activities", isOn: .constant(true))
                    Toggle("Allow Deeplink Actions", isOn: .constant(true))
                }
                
                Section("Data Protection") {
                    Toggle("Encrypt Local Storage", isOn: .constant(true))
                    Toggle("Secure Photo Deletion", isOn: .constant(true))
                }
            }
            .navigationTitle("Privacy Settings")
            .navigationBarItems(
                trailing: Button("Done") {
                    presentationMode.wrappedValue.dismiss()
                }
            )
        }
    }
}

struct StorageView: View {
    var body: some View {
        List {
            Section("Storage Usage") {
                HStack {
                    Text("Photos")
                    Spacer()
                    Text("2.4 GB")
                        .foregroundColor(.secondary)
                }
                
                HStack {
                    Text("Thumbnails")
                    Spacer()
                    Text("156 MB")
                        .foregroundColor(.secondary)
                }
                
                HStack {
                    Text("Cache")
                    Spacer()
                    Text("89 MB")
                        .foregroundColor(.secondary)
                }
            }
            
            Section("Cleanup") {
                Button("Clear Cache") {
                    // Clear cache
                }
                
                Button("Optimize Storage") {
                    // Optimize storage
                }
                
                Button("Remove Duplicates") {
                    // Remove duplicate photos
                }
            }
        }
        .navigationTitle("Storage")
    }
}

struct ExportView: View {
    var body: some View {
        List {
            Section("Export Options") {
                Button("Export All Photos") {
                    // Export all photos
                }
                
                Button("Export Private Photos Only") {
                    // Export private photos
                }
                
                Button("Export Sharing History") {
                    // Export sharing data
                }
            }
            
            Section("Format") {
                HStack {
                    Text("Image Quality")
                    Spacer()
                    Text("Original")
                        .foregroundColor(.secondary)
                }
                
                HStack {
                    Text("Include Metadata")
                    Spacer()
                    Toggle("", isOn: .constant(true))
                }
            }
        }
        .navigationTitle("Export Data")
    }
}

struct AccountSettingsView: View {
    var body: some View {
        List {
            Section("Profile") {
                HStack {
                    Text("Name")
                    Spacer()
                    Text("Alex Johnson")
                        .foregroundColor(.secondary)
                }
                
                HStack {
                    Text("Email")
                    Spacer()
                    Text("alex@example.com")
                        .foregroundColor(.secondary)
                }
            }
            
            Section("Subscription") {
                HStack {
                    Text("Plan")
                    Spacer()
                    Text("Premium")
                        .foregroundColor(.gold)
                }
                
                Button("Manage Subscription") {
                    // Manage subscription
                }
            }
        }
        .navigationTitle("Account")
    }
}

extension Color {
    static let gold = Color(red: 1.0, green: 0.8, blue: 0.0)
}
