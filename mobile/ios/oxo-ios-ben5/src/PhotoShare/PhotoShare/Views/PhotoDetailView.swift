//
//  PhotoDetailView.swift
//  PhotoShare
//
//  Created by elyousfi on 10/09/2025.
//

import SwiftUI
import Combine

struct PhotoDetailView: View {
    let photo: Photo
    @EnvironmentObject var photoManager: PhotoManager
    @EnvironmentObject var navigationManager: NavigationManager
    @State private var showingShareOptions = false
    @State private var showingDeleteAlert = false
    @State private var shareRecipient = ""
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    // Main photo display
                    ZStack {
                        Rectangle()
                            .fill(Color.gray.opacity(0.2))
                            .aspectRatio(4/3, contentMode: .fit)
                            .overlay {
                                VStack {
                                    Image(systemName: "photo")
                                        .font(.system(size: 60))
                                        .foregroundColor(.gray)
                                    Text(photo.title)
                                        .font(.headline)
                                        .multilineTextAlignment(.center)
                                }
                            }
                        
                        if photo.isPrivate {
                            VStack {
                                HStack {
                                    Spacer()
                                    Image(systemName: "lock.fill")
                                        .foregroundColor(.white)
                                        .padding(8)
                                        .background(Color.black.opacity(0.7))
                                        .clipShape(Circle())
                                        .padding()
                                }
                                Spacer()
                            }
                        }
                    }
                    .cornerRadius(12)
                    .padding(.horizontal)
                    
                    // Photo information
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Text(photo.title)
                                .font(.title2)
                                .fontWeight(.semibold)
                            Spacer()
                            if photo.isPrivate {
                                Label("Private", systemImage: "lock.fill")
                                    .font(.caption)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 4)
                                    .background(Color.red.opacity(0.1))
                                    .foregroundColor(.red)
                                    .cornerRadius(8)
                            }
                        }
                        
                        if let location = photo.location {
                            Label(location, systemImage: "location")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                        
                        Label(formatDate(photo.dateCreated), systemImage: "calendar")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                        
                        // File path (for demonstration)
                        Label(photo.filePath, systemImage: "folder")
                            .font(.caption)
                            .foregroundColor(.secondary)
                            .padding(.top, 4)
                        
                        // Tags
                        if !photo.tags.isEmpty {
                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack {
                                    ForEach(photo.tags, id: \.self) { tag in
                                        Text("#\(tag)")
                                            .font(.caption)
                                            .padding(.horizontal, 8)
                                            .padding(.vertical, 4)
                                            .background(Color.blue.opacity(0.1))
                                            .foregroundColor(.blue)
                                            .cornerRadius(12)
                                    }
                                }
                                .padding(.horizontal)
                            }
                        }
                        
                        // Shared with information
                        if !photo.sharedWith.isEmpty {
                            VStack(alignment: .leading, spacing: 8) {
                                Text("Shared with:")
                                    .font(.headline)
                                
                                ForEach(photo.sharedWith, id: \.self) { recipient in
                                    HStack {
                                        Image(systemName: "person.circle")
                                        Text(recipient)
                                        Spacer()
                                        Text("Active")
                                            .font(.caption)
                                            .foregroundColor(.green)
                                    }
                                    .padding(.vertical, 4)
                                }
                            }
                            .padding()
                            .background(Color.gray.opacity(0.05))
                            .cornerRadius(12)
                        }
                        
                        // Action buttons
                        VStack(spacing: 12) {
                            Button(action: {
                                showingShareOptions = true
                            }) {
                                HStack {
                                    Image(systemName: "square.and.arrow.up")
                                    Text("Share Photo")
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(12)
                            }
                            
                            Button(action: {
                                showingDeleteAlert = true
                            }) {
                                HStack {
                                    Image(systemName: "trash")
                                    Text("Delete Photo")
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.red.opacity(0.1))
                                .foregroundColor(.red)
                                .cornerRadius(12)
                            }
                        }
                        .padding(.top)
                    }
                    .padding(.horizontal)
                    
                    Spacer()
                }
            }
            .navigationTitle("Photo Details")
            .navigationBarItems(
                leading: Button("Close") {
                    navigationManager.showingPhotoDetail = false
                }
            )
        }
        .sheet(isPresented: $showingShareOptions) {
            ShareOptionsView(photo: photo, recipient: $shareRecipient) {
                photoManager.sharePhoto(photoId: photo.id.uuidString, recipient: shareRecipient)
                showingShareOptions = false
            }
        }
        .alert("Delete Photo", isPresented: $showingDeleteAlert) {
            Button("Cancel", role: .cancel) { }
            Button("Delete", role: .destructive) {
                photoManager.deletePhoto(photoId: photo.id.uuidString)
                navigationManager.showingPhotoDetail = false
            }
        } message: {
            Text("Are you sure you want to delete '\(photo.title)'? This action cannot be undone.")
        }
    }
    
    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

struct ShareOptionsView: View {
    let photo: Photo
    @Binding var recipient: String
    let onShare: () -> Void
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            Form {
                Section("Share Photo") {
                    HStack {
                        Text(photo.title)
                            .fontWeight(.semibold)
                        Spacer()
                        if photo.isPrivate {
                            Image(systemName: "lock.fill")
                                .foregroundColor(.red)
                        }
                    }
                }
                
                Section("Recipient") {
                    TextField("Enter email or username", text: $recipient)
                        .keyboardType(.emailAddress)
                        .autocapitalization(.none)
                }
                
                Section("Quick Contacts") {
                    Button("family@example.com") {
                        recipient = "family@example.com"
                    }
                    Button("friends@example.com") {
                        recipient = "friends@example.com"
                    }
                    Button("work@example.com") {
                        recipient = "work@example.com"
                    }
                }
                
                Section {
                    Button("Share Photo") {
                        onShare()
                    }
                    .disabled(recipient.isEmpty)
                }
            }
            .navigationTitle("Share Photo")
            .navigationBarItems(
                leading: Button("Cancel") {
                    presentationMode.wrappedValue.dismiss()
                }
            )
        }
    }
}
