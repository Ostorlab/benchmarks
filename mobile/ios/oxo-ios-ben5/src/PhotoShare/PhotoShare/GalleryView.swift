//
//  GalleryView.swift
//  PhotoShare
//
//  Created by elyousfi on 08/09/2025.
//

import SwiftUI
import Combine

struct GalleryView: View {
    @ObservedObject var photoManager: PhotoManager
    @Environment(\.presentationMode) var presentationMode
    
    let columns = [
        GridItem(.adaptive(minimum: 100), spacing: 2)
    ]
    
    var body: some View {
        NavigationView {
            ScrollView {
                LazyVGrid(columns: columns, spacing: 2) {
                    ForEach(photoManager.photos) { photo in
                        NavigationLink(destination: PhotoDetailView(photo: photo, photoManager: photoManager)) {
                            ZStack(alignment: .bottomTrailing) {
                                Image(uiImage: photo.image)
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                                    .frame(width: 110, height: 110)
                                    .clipped()
                                
                                if photo.location != nil {
                                    Image(systemName: "location.fill")
                                        .foregroundColor(.white)
                                        .font(.caption)
                                        .padding(4)
                                        .background(Color.black.opacity(0.6))
                                        .clipShape(Circle())
                                        .padding(4)
                                }
                            }
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("Photo Gallery")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Done") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
        }
    }
}
