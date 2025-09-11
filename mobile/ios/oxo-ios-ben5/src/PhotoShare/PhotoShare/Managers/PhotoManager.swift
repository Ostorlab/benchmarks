//
//  PhotoManager.swift
//  PhotoShare
//
//  Created by elyousfi on 10/09/2025.
//

import Foundation
import SwiftUI
import Combine

class PhotoManager: ObservableObject {
    @Published var photos: [Photo] = []
    @Published var albums: [Album] = []
    @Published var recentShares: [ShareRequest] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    init() {
        loadSampleData()
    }
    
    private func loadSampleData() {
        // Sample photos with realistic data
        photos = [
            Photo(title: "Beach Sunset", imageName: "beach_sunset", location: "Malibu, CA", isPrivate: false, tags: ["sunset", "beach", "vacation"], filePath: "Photos/Public/beach_sunset.jpg"),
            Photo(title: "Family Gathering", imageName: "family_dinner", location: "Home", isPrivate: true, tags: ["family", "dinner", "memories"], filePath: "Photos/Private/family_dinner.jpg"),
            Photo(title: "Mountain Hike", imageName: "mountain_view", location: "Rocky Mountains", isPrivate: false, tags: ["nature", "hiking", "adventure"], filePath: "Photos/Public/mountain_view.jpg"),
            Photo(title: "Birthday Party", imageName: "birthday_cake", location: "Home", isPrivate: true, tags: ["birthday", "celebration", "family"], filePath: "Photos/Private/birthday_cake.jpg"),
            Photo(title: "City Skyline", imageName: "city_night", location: "New York, NY", isPrivate: false, tags: ["city", "skyline", "night"], filePath: "Photos/Public/city_night.jpg"),
            Photo(title: "Wedding Photo", imageName: "wedding_photo", location: "Garden Venue", isPrivate: true, tags: ["wedding", "love", "celebration"], filePath: "Photos/Private/wedding_photo.jpg"),
            Photo(title: "Pet Portrait", imageName: "dog_portrait", location: "Home", isPrivate: false, tags: ["pets", "dog", "portrait"], filePath: "Photos/Public/dog_portrait.jpg"),
            Photo(title: "Vacation Memories", imageName: "vacation_beach", location: "Hawaii", isPrivate: true, tags: ["vacation", "beach", "tropical"], filePath: "Photos/Private/vacation_beach.jpg")
        ]
        
        // Sample albums
        albums = [
            Album(name: "Family Moments", description: "Precious family memories", isPrivate: true, coverImageName: "family_dinner"),
            Album(name: "Travel Adventures", description: "Amazing places I've visited", isPrivate: false, coverImageName: "beach_sunset"),
            Album(name: "Nature Photography", description: "Beautiful landscapes and wildlife", isPrivate: false, coverImageName: "mountain_view"),
            Album(name: "Personal Collection", description: "Private moments and memories", isPrivate: true, coverImageName: "wedding_photo")
        ]
    }
    
    // Core photo management functions
    func addPhoto(_ photo: Photo) {
        DispatchQueue.main.async {
            self.photos.append(photo)
        }
    }
    
    func removePhoto(withId photoId: UUID) {
        DispatchQueue.main.async {
            self.photos.removeAll { $0.id == photoId }
        }
    }
    
    func getPhoto(byId photoId: String) -> Photo? {
        // Try to find by UUID string first
        if let uuid = UUID(uuidString: photoId) {
            return photos.first { $0.id == uuid }
        }
        
        // Fallback to finding by title or filename
        return photos.first { photo in
            photo.title.lowercased().contains(photoId.lowercased()) ||
            photo.imageName.lowercased().contains(photoId.lowercased()) ||
            photo.filePath.lowercased().contains(photoId.lowercased())
        }
    }
    
    func getAlbum(byId albumId: String) -> Album? {
        if let uuid = UUID(uuidString: albumId) {
            return albums.first { $0.id == uuid }
        }
        
        return albums.first { album in
            album.name.lowercased().contains(albumId.lowercased())
        }
    }
    
    // Deeplink action handlers
    func sharePhoto(photoId: String, recipient: String) {
        guard let photo = getPhoto(byId: photoId) else {
            setError("Photo not found: \(photoId)")
            return
        }
        
        DispatchQueue.main.async {
            // Simulate sharing process
            self.isLoading = true
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                let shareRequest = ShareRequest(photoId: photoId, recipient: recipient, message: "Shared via deeplink")
                self.recentShares.append(shareRequest)
                
                // Update photo's shared list
                if let index = self.photos.firstIndex(where: { self.getPhoto(byId: photoId)?.id == $0.id }) {
                    self.photos[index].sharedWith.append(recipient)
                }
                
                self.isLoading = false
                print("Photo '\(photo.title)' shared with \(recipient)")
            }
        }
    }
    
    func deletePhoto(photoId: String) {
        guard let photo = getPhoto(byId: photoId) else {
            setError("Photo not found for deletion: \(photoId)")
            return
        }
        
        DispatchQueue.main.async {
            self.isLoading = true
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                // Remove from photos array
                self.photos.removeAll { existingPhoto in
                    existingPhoto.id == photo.id
                }
                
                self.isLoading = false
                print("Photo '\(photo.title)' has been deleted")
            }
        }
    }
    
    func exportAlbum(albumId: String, to path: String) {
        guard let album = getAlbum(byId: albumId) else {
            setError("Album not found: \(albumId)")
            return
        }
        
        DispatchQueue.main.async {
            self.isLoading = true
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                // Simulate export process
                let albumPhotos = self.photos.filter { photo in
                    album.photoIds.contains(photo.id)
                }
                
                // Log the export operation (this would normally save files)
                print("Exporting album '\(album.name)' with \(albumPhotos.count) photos to: \(path)")
                for photo in albumPhotos {
                    print("  - Exporting: \(photo.filePath) -> \(path)/\(photo.imageName)")
                }
                
                self.isLoading = false
            }
        }
    }
    
    func importPhotos(from sourcePath: String) {
        DispatchQueue.main.async {
            self.isLoading = true
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                // Simulate import process
                let newPhoto = Photo(
                    title: "Imported Photo",
                    imageName: "imported_\(Date().timeIntervalSince1970)",
                    location: "Imported",
                    isPrivate: false,
                    tags: ["imported"],
                    filePath: sourcePath
                )
                
                self.photos.append(newPhoto)
                self.isLoading = false
                print("Imported photo from: \(sourcePath)")
            }
        }
    }
    
    // Utility functions
    var privatePhotos: [Photo] {
        photos.filter { $0.isPrivate }
    }
    
    var publicPhotos: [Photo] {
        photos.filter { !$0.isPrivate }
    }
    
    var recentPhotos: [Photo] {
        photos.sorted { $0.dateCreated > $1.dateCreated }.prefix(10).map { $0 }
    }
    
    func getPhotos(for album: Album) -> [Photo] {
        photos.filter { album.photoIds.contains($0.id) }
    }
    
    private func setError(_ message: String) {
        DispatchQueue.main.async {
            self.errorMessage = message
        }
    }
    
    func clearError() {
        DispatchQueue.main.async {
            self.errorMessage = nil
        }
    }
}
