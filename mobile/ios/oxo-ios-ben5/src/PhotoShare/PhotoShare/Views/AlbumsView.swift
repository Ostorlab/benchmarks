//
//  AlbumsView.swift
//  PhotoShare
//
//  Created by elyousfi on 10/09/2025.
//

import SwiftUI
import Combine

struct AlbumsView: View {
    @EnvironmentObject var photoManager: PhotoManager
    @EnvironmentObject var navigationManager: NavigationManager
    @State private var showingCreateAlbum = false
    
    let columns = [
        GridItem(.flexible()),
        GridItem(.flexible())
    ]
    
    var body: some View {
        NavigationView {
            ScrollView {
                LazyVGrid(columns: columns, spacing: 16) {
                    ForEach(photoManager.albums) { album in
                        AlbumThumbnailView(album: album)
                            .onTapGesture {
                                // Navigate to album details
                            }
                    }
                }
                .padding()
            }
            .navigationTitle("Albums")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        showingCreateAlbum = true
                    }) {
                        Image(systemName: "plus")
                    }
                }
            }
        }
        .sheet(isPresented: $showingCreateAlbum) {
            CreateAlbumView()
                .environmentObject(photoManager)
        }
    }
}

struct AlbumThumbnailView: View {
    let album: Album
    @EnvironmentObject var photoManager: PhotoManager
    
    var albumPhotos: [Photo] {
        photoManager.getPhotos(for: album)
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Album cover
            ZStack {
                Rectangle()
                    .fill(Color.gray.opacity(0.2))
                    .aspectRatio(4/3, contentMode: .fit)
                    .overlay {
                        if let coverImage = album.coverImageName {
                            VStack {
                                Image(systemName: "photo.stack")
                                    .font(.title)
                                    .foregroundColor(.gray)
                                Text(coverImage)
                                    .font(.caption2)
                                    .multilineTextAlignment(.center)
                            }
                        } else {
                            Image(systemName: "folder")
                                .font(.title)
                                .foregroundColor(.gray)
                        }
                    }
                
                // Privacy indicator
                if album.isPrivate {
                    VStack {
                        HStack {
                            Spacer()
                            Image(systemName: "lock.fill")
                                .foregroundColor(.white)
                                .padding(6)
                                .background(Color.black.opacity(0.7))
                                .clipShape(Circle())
                                .padding(8)
                        }
                        Spacer()
                    }
                }
                
                // Photo count
                VStack {
                    Spacer()
                    HStack {
                        Spacer()
                        Text("\(albumPhotos.count)")
                            .font(.caption)
                            .fontWeight(.semibold)
                            .foregroundColor(.white)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Color.black.opacity(0.7))
                            .cornerRadius(12)
                            .padding(8)
                    }
                }
            }
            .cornerRadius(12)
            
            // Album info
            VStack(alignment: .leading, spacing: 4) {
                Text(album.name)
                    .font(.headline)
                    .lineLimit(1)
                
                if !album.description.isEmpty {
                    Text(album.description)
                        .font(.caption)
                        .foregroundColor(.secondary)
                        .lineLimit(2)
                }
                
                HStack {
                    if album.isPrivate {
                        Image(systemName: "lock.fill")
                            .font(.caption2)
                            .foregroundColor(.red)
                    }
                    
                    if !album.sharedWith.isEmpty {
                        Image(systemName: "person.2.fill")
                            .font(.caption2)
                            .foregroundColor(.blue)
                    }
                    
                    Spacer()
                    
                    Text(formatDate(album.dateCreated))
                        .font(.caption2)
                        .foregroundColor(.secondary)
                }
            }
        }
    }
    
    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .short
        return formatter.string(from: date)
    }
}

struct CreateAlbumView: View {
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject var photoManager: PhotoManager
    @State private var albumName = ""
    @State private var albumDescription = ""
    @State private var isPrivate = false
    
    var body: some View {
        NavigationView {
            Form {
                Section("Album Details") {
                    TextField("Album Name", text: $albumName)
                    TextField("Description (Optional)", text: $albumDescription)
                    Toggle("Private Album", isOn: $isPrivate)
                }
                
                Section("Privacy") {
                    if isPrivate {
                        Text("Only you can see photos in this album.")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    } else {
                        Text("This album can be shared with others.")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
            }
            .navigationTitle("New Album")
            .navigationBarItems(
                leading: Button("Cancel") {
                    presentationMode.wrappedValue.dismiss()
                },
                trailing: Button("Create") {
                    let newAlbum = Album(
                        name: albumName,
                        description: albumDescription,
                        isPrivate: isPrivate
                    )
                    photoManager.albums.append(newAlbum)
                    presentationMode.wrappedValue.dismiss()
                }
                .disabled(albumName.isEmpty)
            )
        }
    }
}
