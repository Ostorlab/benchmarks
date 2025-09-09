//
//  PhotoGalleryView.swift
//  PhotoShare
//
//  Created by elyousfi on 08/09/2025.
//

import SwiftUI
import Combine

struct PhotoGalleryView: View {
    let photos: [PhotoItem]
    @ObservedObject var photoManager: PhotoManager
    
    let columns = [
        GridItem(.adaptive(minimum: 100, maximum: 150), spacing: 4)
    ]
    
    var body: some View {
        ScrollView {
            LazyVGrid(columns: columns, spacing: 4) {
                ForEach(photos.prefix(6)) { photo in
                    NavigationLink(destination: PhotoDetailView(photo: photo, photoManager: photoManager)) {
                        Image(uiImage: photo.image)
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: 100, height: 100)
                            .clipped()
                            .cornerRadius(8)
                    }
                }
            }
            .padding()
        }
    }
}

struct PhotoDetailView: View {
    let photo: PhotoItem
    @ObservedObject var photoManager: PhotoManager
    @State private var showingShareSheet = false
    
    var body: some View {
        VStack {
            Image(uiImage: photo.image)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .cornerRadius(12)
                .padding()
            
            VStack(alignment: .leading, spacing: 8) {
                HStack {
                    Image(systemName: "calendar")
                        .foregroundColor(.secondary)
                    Text(photo.timestamp.formatted(date: .abbreviated, time: .shortened))
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    
                    Spacer()
                    
                    if photo.location != nil {
                        HStack {
                            Image(systemName: "location")
                                .foregroundColor(.green)
                            Text("Location")
                                .font(.subheadline)
                                .foregroundColor(.green)
                        }
                    }
                }
                .padding(.horizontal)
                
                Button(action: {
                    showingShareSheet = true
                }) {
                    HStack {
                        Image(systemName: "square.and.arrow.up")
                        Text("Share Photo")
                    }
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .cornerRadius(12)
                }
                .padding()
            }
            
            Spacer()
        }
        .navigationTitle("Photo")
        .navigationBarTitleDisplayMode(.inline)
        .sheet(isPresented: $showingShareSheet) {
            ShareSheet(photo: photo, photoManager: photoManager)
        }
    }
}

struct ShareSheet: UIViewControllerRepresentable {
    let photo: PhotoItem
    @ObservedObject var photoManager: PhotoManager
    
    func makeUIViewController(context: Context) -> UIActivityViewController {
        // The vulnerability: Export photo with location metadata intact
        let imageToShare = photoManager.exportPhoto(photo)
        let activityVC = UIActivityViewController(activityItems: [imageToShare], applicationActivities: nil)
        return activityVC
    }
    
    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}
