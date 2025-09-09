//
//  ContentView.swift
//  PhotoShare
//
//  Created by elyousfi on 08/09/2025.
//

import SwiftUI
import Combine

struct ContentView: View {
    @StateObject private var photoManager = PhotoManager()
    @State private var showingCamera = false
    @State private var showingGallery = false
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                if photoManager.photos.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "camera.fill")
                            .font(.system(size: 60))
                            .foregroundColor(.blue)
                        
                        Text("Welcome to PhotoShare")
                            .font(.title2)
                            .fontWeight(.bold)
                        
                        Text("Capture and share your favorite moments with enhanced quality and easy sharing features.")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                    }
                    .padding(.top, 50)
                    
                    Spacer()
                } else {
                    PhotoGalleryView(photos: photoManager.photos, photoManager: photoManager)
                }
                
                VStack(spacing: 12) {
                    Button(action: {
                        showingCamera = true
                    }) {
                        HStack {
                            Image(systemName: UIImagePickerController.isSourceTypeAvailable(.camera) ? "camera.fill" : "photo")
                            Text(UIImagePickerController.isSourceTypeAvailable(.camera) ? "Take Photo" : "Choose Photo")
                        }
                        .font(.headline)
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .cornerRadius(12)
                    }
                    
                    if !photoManager.photos.isEmpty {
                        Button(action: {
                            showingGallery = true
                        }) {
                            HStack {
                                Image(systemName: "photo.stack")
                                Text("View Gallery")
                            }
                            .font(.headline)
                            .foregroundColor(.blue)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue.opacity(0.1))
                            .cornerRadius(12)
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("PhotoShare")
            .sheet(isPresented: $showingCamera) {
                CameraView(photoManager: photoManager)
            }
            .sheet(isPresented: $showingGallery) {
                GalleryView(photoManager: photoManager)
            }
        }
    }
}

#Preview {
    ContentView()
}
