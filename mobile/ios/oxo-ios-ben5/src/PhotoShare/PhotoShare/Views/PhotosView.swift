//
//  PhotosView.swift
//  PhotoShare
//
//  Created by elyousfi on 10/09/2025.
//

import SwiftUI
import Combine

struct PhotosView: View {
    @EnvironmentObject var photoManager: PhotoManager
    @EnvironmentObject var navigationManager: NavigationManager
    @State private var showingAllPhotos = true
    @State private var searchText = ""
    
    var filteredPhotos: [Photo] {
        let photos = showingAllPhotos ? photoManager.photos : photoManager.recentPhotos
        
        if searchText.isEmpty {
            return photos
        } else {
            return photos.filter { photo in
                photo.title.lowercased().contains(searchText.lowercased()) ||
                photo.tags.joined().lowercased().contains(searchText.lowercased()) ||
                (photo.location?.lowercased().contains(searchText.lowercased()) ?? false)
            }
        }
    }
    
    let columns = [
        GridItem(.flexible()),
        GridItem(.flexible()),
        GridItem(.flexible())
    ]
    
    var body: some View {
        NavigationView {
            VStack {
                if photoManager.isLoading {
                    ProgressView("Processing...")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    ScrollView {
                        LazyVGrid(columns: columns, spacing: 2) {
                            ForEach(filteredPhotos) { photo in
                                PhotoThumbnailView(photo: photo)
                                    .aspectRatio(1, contentMode: .fill)
                                    .onTapGesture {
                                        navigationManager.navigateToPhoto(photoId: photo.id.uuidString)
                                    }
                            }
                        }
                        .padding(.horizontal, 2)
                    }
                }
            }
            .navigationTitle("Photos")
            .searchable(text: $searchText, prompt: "Search photos...")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Menu {
                        Button(action: { showingAllPhotos = true }) {
                            Label("All Photos", systemImage: showingAllPhotos ? "checkmark" : "")
                        }
                        
                        Button(action: { showingAllPhotos = false }) {
                            Label("Recent", systemImage: !showingAllPhotos ? "checkmark" : "")
                        }
                        
                        Divider()
                        
                        Button("Import Photos") {
                            // Simulate import action
                            photoManager.importPhotos(from: "Documents/ImportedPhotos/")
                        }
                    } label: {
                        Image(systemName: "ellipsis.circle")
                    }
                }
                
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Stats") {
                        // Show photo statistics
                    }
                }
            }
        }
    }
}

struct PhotoThumbnailView: View {
    let photo: Photo
    
    var body: some View {
        ZStack {
            // Placeholder for actual image
            Rectangle()
                .fill(Color.gray.opacity(0.3))
                .overlay {
                    VStack {
                        Image(systemName: "photo")
                            .font(.title)
                            .foregroundColor(.gray)
                        Text(photo.title)
                            .font(.caption2)
                            .multilineTextAlignment(.center)
                            .lineLimit(2)
                    }
                    .padding(4)
                }
            
            // Privacy indicator
            if photo.isPrivate {
                VStack {
                    HStack {
                        Spacer()
                        Image(systemName: "lock.fill")
                            .foregroundColor(.white)
                            .background(Color.black.opacity(0.6))
                            .clipShape(Circle())
                            .padding(4)
                    }
                    Spacer()
                }
            }
            
            // Shared indicator
            if !photo.sharedWith.isEmpty {
                VStack {
                    Spacer()
                    HStack {
                        Image(systemName: "person.2.fill")
                            .foregroundColor(.white)
                            .background(Color.blue.opacity(0.8))
                            .clipShape(Circle())
                            .padding(4)
                        Spacer()
                    }
                }
            }
        }
        .clipped()
        .cornerRadius(8)
    }
}
