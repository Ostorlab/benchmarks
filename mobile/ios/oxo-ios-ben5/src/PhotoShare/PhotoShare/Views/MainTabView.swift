//
//  MainTabView.swift
//  PhotoShare
//
//  Created by elyousfi on 10/09/2025.
//

import SwiftUI
import Combine

struct MainTabView: View {
    @EnvironmentObject var navigationManager: NavigationManager
    @EnvironmentObject var photoManager: PhotoManager
    
    var body: some View {
        TabView(selection: $navigationManager.selectedTab) {
            PhotosView()
                .tabItem {
                    Image(systemName: NavigationManager.Tab.photos.iconName)
                    Text(NavigationManager.Tab.photos.rawValue)
                }
                .tag(NavigationManager.Tab.photos)
            
            AlbumsView()
                .tabItem {
                    Image(systemName: NavigationManager.Tab.albums.iconName)
                    Text(NavigationManager.Tab.albums.rawValue)
                }
                .tag(NavigationManager.Tab.albums)
            
            SharedView()
                .tabItem {
                    Image(systemName: NavigationManager.Tab.shared.iconName)
                    Text(NavigationManager.Tab.shared.rawValue)
                }
                .tag(NavigationManager.Tab.shared)
            
            ProfileView()
                .tabItem {
                    Image(systemName: NavigationManager.Tab.profile.iconName)
                    Text(NavigationManager.Tab.profile.rawValue)
                }
                .tag(NavigationManager.Tab.profile)
        }
        .sheet(isPresented: $navigationManager.showingPhotoDetail) {
            if let photoId = navigationManager.selectedPhotoId,
               let photo = photoManager.getPhoto(byId: photoId) {
                PhotoDetailView(photo: photo)
                    .environmentObject(photoManager)
                    .environmentObject(navigationManager)
            }
        }
        .alert("Error", isPresented: .constant(photoManager.errorMessage != nil)) {
            Button("OK") {
                photoManager.clearError()
            }
        } message: {
            if let error = photoManager.errorMessage {
                Text(error)
            }
        }
    }
}
