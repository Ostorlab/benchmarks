//
//  PhotoManager.swift
//  PhotoShare
//
//  Created by elyousfi on 08/09/2025.
//

import SwiftUI
import Photos
import CoreLocation
import UIKit
import UniformTypeIdentifiers
import Combine

final class PhotoManager: NSObject, ObservableObject {
    @Published var photos: [PhotoItem] = []
    private let locationManager = CLLocationManager()
    private var currentLocation: CLLocation?
    
    override init() {
        super.init()
        setupLocationManager()
        loadPhotos()
    }
    
    private func setupLocationManager() {
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        
        // Request permission based on current authorization status
        let authorizationStatus = locationManager.authorizationStatus
        if authorizationStatus == .notDetermined {
            locationManager.requestWhenInUseAuthorization()
        } else if authorizationStatus == .authorizedWhenInUse || authorizationStatus == .authorizedAlways {
            locationManager.startUpdatingLocation()
        }
    }
    
    func addPhoto(_ image: UIImage) {
        let photoItem = PhotoItem(
            id: UUID(),
            image: image,
            timestamp: Date(),
            location: currentLocation
        )
        
        photos.insert(photoItem, at: 0)
        savePhoto(photoItem)
    }
    
    private func savePhoto(_ photoItem: PhotoItem) {
        // Save to user's photo library with location data preserved
        PHPhotoLibrary.requestAuthorization { status in
            if status == .authorized {
                PHPhotoLibrary.shared().performChanges({
                    let request = PHAssetChangeRequest.creationRequestForAsset(from: photoItem.image)
                    
                    // Preserve location data when saving to Photos
                    if let location = photoItem.location {
                        request.location = location
                    }
                    request.creationDate = photoItem.timestamp
                }) { success, error in
                    if let error = error {
                        print("Error saving photo: \(error)")
                    }
                }
            }
        }
    }
    
    func exportPhoto(_ photoItem: PhotoItem) -> UIImage {
        // This method preserves the original image with all metadata intact
        // The vulnerability: Location data is not stripped during export
        return addLocationMetadataToImage(photoItem.image, location: photoItem.location, timestamp: photoItem.timestamp)
    }
    
    private func addLocationMetadataToImage(_ image: UIImage, location: CLLocation?, timestamp: Date) -> UIImage {
        guard let imageData = image.jpegData(compressionQuality: 0.9) else {
            return image
        }
        
        guard let source = CGImageSourceCreateWithData(imageData as CFData, nil),
              let imageProperties = CGImageSourceCopyPropertiesAtIndex(source, 0, nil) as? [String: Any] else {
            return image
        }
        
        var mutableMetadata = imageProperties
        
        // Add timestamp
        var exifDict = mutableMetadata[kCGImagePropertyExifDictionary as String] as? [String: Any] ?? [:]
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy:MM:dd HH:mm:ss"
        exifDict[kCGImagePropertyExifDateTimeOriginal as String] = dateFormatter.string(from: timestamp)
        mutableMetadata[kCGImagePropertyExifDictionary as String] = exifDict
        
        // The vulnerability: Add GPS location data without user awareness
        if let location = location {
            var gpsDict: [String: Any] = [:]
            
            let latitude = location.coordinate.latitude
            let longitude = location.coordinate.longitude
            
            gpsDict[kCGImagePropertyGPSLatitude as String] = abs(latitude)
            gpsDict[kCGImagePropertyGPSLatitudeRef as String] = latitude >= 0 ? "N" : "S"
            gpsDict[kCGImagePropertyGPSLongitude as String] = abs(longitude)
            gpsDict[kCGImagePropertyGPSLongitudeRef as String] = longitude >= 0 ? "E" : "W"
            gpsDict[kCGImagePropertyGPSAltitude as String] = location.altitude
            gpsDict[kCGImagePropertyGPSTimeStamp as String] = dateFormatter.string(from: location.timestamp)
            
            mutableMetadata[kCGImagePropertyGPSDictionary as String] = gpsDict
        }
        
        // Create new image with metadata
        guard let mutableData = CFDataCreateMutable(nil, 0),
              let destination = CGImageDestinationCreateWithData(mutableData, UTType.jpeg.identifier as CFString, 1, nil),
              let cgImage = image.cgImage else {
            return image
        }
        
        CGImageDestinationAddImage(destination, cgImage, mutableMetadata as CFDictionary)
        CGImageDestinationFinalize(destination)
        
        let finalImageData = mutableData as Data
        return UIImage(data: finalImageData) ?? image
    }
    
    private func loadPhotos() {
        // Load previously saved photos from UserDefaults for demo purposes
        // In a real app, this might load from Core Data or other persistence
    }
}

extension PhotoManager: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        currentLocation = locations.last
        // Stop updating location after getting a fix to save battery
        locationManager.stopUpdatingLocation()
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Location error: \(error.localizedDescription)")
        // Don't crash the app if location fails - just continue without location data
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        switch status {
        case .authorizedWhenInUse, .authorizedAlways:
            locationManager.startUpdatingLocation()
        case .denied, .restricted:
            print("Location access denied - photos will be saved without location data")
        case .notDetermined:
            locationManager.requestWhenInUseAuthorization()
        @unknown default:
            break
        }
    }
}

struct PhotoItem: Identifiable {
    let id: UUID
    let image: UIImage
    let timestamp: Date
    let location: CLLocation?
}
