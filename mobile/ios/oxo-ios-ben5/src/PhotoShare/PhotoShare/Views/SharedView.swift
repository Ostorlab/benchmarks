//
//  SharedView.swift
//  PhotoShare
//
//  Created by elyousfi on 10/09/2025.
//

import SwiftUI
import Combine

struct SharedView: View {
    @EnvironmentObject var photoManager: PhotoManager
    @EnvironmentObject var navigationManager: NavigationManager
    
    var sharedPhotos: [Photo] {
        photoManager.photos.filter { !$0.sharedWith.isEmpty }
    }
    
    var body: some View {
        NavigationView {
            List {
                Section("Recent Shares") {
                    ForEach(photoManager.recentShares.reversed(), id: \.photoId) { share in
                        ShareItemView(shareRequest: share)
                    }
                    
                    if photoManager.recentShares.isEmpty {
                        Text("No recent shares")
                            .foregroundColor(.secondary)
                            .italic()
                    }
                }
                
                Section("Photos Shared with Others") {
                    ForEach(sharedPhotos) { photo in
                        PhotoShareItemView(photo: photo)
                            .onTapGesture {
                                navigationManager.navigateToPhoto(photoId: photo.id.uuidString)
                            }
                    }
                    
                    if sharedPhotos.isEmpty {
                        Text("No shared photos")
                            .foregroundColor(.secondary)
                            .italic()
                    }
                }
            }
            .navigationTitle("Shared")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Menu {
                        Button("View All Shares") {
                            // Show all sharing history
                        }
                        
                        Button("Export Share Report") {
                            // Export sharing activity
                        }
                        
                        Button("Manage Permissions") {
                            // Manage sharing permissions
                        }
                    } label: {
                        Image(systemName: "ellipsis.circle")
                    }
                }
            }
        }
    }
}

struct ShareItemView: View {
    let shareRequest: ShareRequest
    @EnvironmentObject var photoManager: PhotoManager
    
    var body: some View {
        HStack {
            // Photo thumbnail placeholder
            Rectangle()
                .fill(Color.gray.opacity(0.3))
                .frame(width: 50, height: 50)
                .cornerRadius(8)
                .overlay {
                    Image(systemName: "photo")
                        .foregroundColor(.gray)
                }
            
            VStack(alignment: .leading, spacing: 4) {
                if let photo = photoManager.getPhoto(byId: shareRequest.photoId) {
                    Text(photo.title)
                        .font(.headline)
                        .lineLimit(1)
                } else {
                    Text("Photo ID: \(shareRequest.photoId)")
                        .font(.headline)
                        .lineLimit(1)
                }
                
                Text("Shared with: \(shareRequest.recipient)")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                
                if let message = shareRequest.message {
                    Text(message)
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .italic()
                }
                
                Text(formatDate(shareRequest.timestamp))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Image(systemName: "checkmark.circle.fill")
                .foregroundColor(.green)
        }
        .padding(.vertical, 4)
    }
    
    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .none
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

struct PhotoShareItemView: View {
    let photo: Photo
    
    var body: some View {
        HStack {
            // Photo thumbnail placeholder
            Rectangle()
                .fill(Color.gray.opacity(0.3))
                .frame(width: 50, height: 50)
                .cornerRadius(8)
                .overlay {
                    Image(systemName: "photo")
                        .foregroundColor(.gray)
                }
            
            VStack(alignment: .leading, spacing: 4) {
                Text(photo.title)
                    .font(.headline)
                    .lineLimit(1)
                
                Text("Shared with \(photo.sharedWith.count) people")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 4) {
                        ForEach(photo.sharedWith.prefix(3), id: \.self) { recipient in
                            Text(recipient)
                                .font(.caption)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color.blue.opacity(0.1))
                                .foregroundColor(.blue)
                                .cornerRadius(8)
                        }
                        
                        if photo.sharedWith.count > 3 {
                            Text("+\(photo.sharedWith.count - 3)")
                                .font(.caption)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color.gray.opacity(0.2))
                                .foregroundColor(.secondary)
                                .cornerRadius(8)
                        }
                    }
                }
            }
            
            Spacer()
            
            VStack {
                if photo.isPrivate {
                    Image(systemName: "lock.fill")
                        .foregroundColor(.red)
                        .font(.caption)
                }
                
                Image(systemName: "person.2.fill")
                    .foregroundColor(.blue)
            }
        }
        .padding(.vertical, 4)
    }
}
