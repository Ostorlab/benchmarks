//
//  PhotoManager.swift
//  PhotoShare Studio
//
//  Photo management and storage functionality
//

import UIKit
import Foundation

class PhotoManager {
    static let shared = PhotoManager()
    
    private init() {
        setupDirectories()
        createSampleData()
    }
    
    private func setupDirectories() {
        let documentsPath = getDocumentsDirectory()
        let albumsPath = documentsPath.appendingPathComponent("Albums")
        let cachePath = documentsPath.appendingPathComponent("Cache")
        
        try? FileManager.default.createDirectory(at: albumsPath, withIntermediateDirectories: true)
        try? FileManager.default.createDirectory(at: cachePath, withIntermediateDirectories: true)
    }
    
    func getDocumentsDirectory() -> URL {
        return FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
    }
    
    func getAlbumsDirectory() -> URL {
        return getDocumentsDirectory().appendingPathComponent("Albums")
    }
    
    private func createSampleData() {
        let documentsPath = getDocumentsDirectory()
        
        // Create realistic iOS app files
        // 1. App settings file (commonly found in Documents)
        let settingsPath = documentsPath.appendingPathComponent("PhotoShare_Settings.plist")
        let settings: [String: Any] = [
            "UserID": "PS_2024_847291",
            "DeviceToken": "fA7kL9mP2nQ3rS4tU5vW6xY7zA8bC9dE",
            "LastSyncTime": Date().timeIntervalSince1970,
            "WiFiSharingEnabled": true,
            "AutoUploadPhotos": false,
            "StorageQuotaUsed": 2847362048,
            "PremiumExpiryDate": "2024-12-15"
        ]
        (settings as NSDictionary).write(to: settingsPath, atomically: true)
        
        // 2. Database file (realistic for photo apps)
        let dbPath = documentsPath.appendingPathComponent("PhotoMetadata.sqlite")
        let dbData = "SQLite format 3\0\0\0\0\0...".data(using: .utf8)
        try? dbData?.write(to: dbPath)
        
        // 3. User credentials cache
        let credentialsPath = documentsPath.appendingPathComponent("cached_auth.json")
        let credentials = [
            "refresh_token": "rt_1234567890abcdefghijklmnopqrstuvwxyz",
            "user_session": "sess_ps2024_847291_active",
            "api_endpoint": "https://sync.photoshare.app/v2",
            "encryption_key": "aes256_key_b847291c93d84e7f2a1b9c8d7e6f5a4b"
        ]
        if let credData = try? JSONSerialization.data(withJSONObject: credentials) {
            try? credData.write(to: credentialsPath)
        }
        
        // 4. Backup manifest (found in many iOS apps)
        let backupPath = documentsPath.appendingPathComponent("backup_manifest.plist")
        let backup: [String: Any] = [
            "LastBackupDate": "2024-09-07T18:42:33Z",
            "BackedUpPhotos": 1847,
            "BackupLocation": "iCloud/PhotoShare_Backup_847291",
            "EncryptionEnabled": true,
            "BackupSize": 4829374629
        ]
        (backup as NSDictionary).write(to: backupPath, atomically: true)
        
        // Create some sample photo metadata
        createSamplePhotoMetadata()
    }
    
    private func createSamplePhotoMetadata() {
        // Create multiple albums with sample photos
        let albums = ["Vacation", "Family", "Work", "Recent"]
        
        for albumName in albums {
            let albumPath = getAlbumsDirectory().appendingPathComponent(albumName)
            try? FileManager.default.createDirectory(at: albumPath, withIntermediateDirectories: true)
            
            // Create sample image files (placeholder data)
            createSampleImages(in: albumPath, albumName: albumName)
            
            // Create metadata
            let metadata = createMetadataForAlbum(albumName)
            let metadataPath = albumPath.appendingPathComponent("metadata.json")
            if let data = try? JSONSerialization.data(withJSONObject: metadata) {
                try? data.write(to: metadataPath)
            }
        }
    }
    
    private func createSampleImages(in albumPath: URL, albumName: String) {
        let photoNames = getPhotoNamesForAlbum(albumName)
        
        for photoName in photoNames {
            let photoPath = albumPath.appendingPathComponent(photoName)
            
            // Create a simple colored image as placeholder
            if let imageData = createPlaceholderImageData(for: photoName) {
                try? imageData.write(to: photoPath)
            }
        }
    }
    
    private func getPhotoNamesForAlbum(_ albumName: String) -> [String] {
        switch albumName {
        case "Vacation":
            return ["paris_tower.jpg", "rome_colosseum.jpg", "beach_sunset.jpg", "mountain_view.jpg"]
        case "Family":
            return ["birthday_party.jpg", "family_dinner.jpg", "kids_playground.jpg"]
        case "Work":
            return ["conference_2024.jpg", "team_meeting.jpg", "office_view.jpg"]
        case "Recent":
            return ["morning_coffee.jpg", "workout_session.jpg", "new_restaurant.jpg", "weekend_hike.jpg"]
        default:
            return ["sample_photo.jpg"]
        }
    }
    
    private func createMetadataForAlbum(_ albumName: String) -> [String: [String: String]] {
        switch albumName {
        case "Vacation":
            return [
                "paris_tower.jpg": [
                    "location": "Paris, France",
                    "date": "2024-07-15",
                    "camera": "iPhone 15 Pro",
                    "gps_coords": "48.8566, 2.3522"
                ],
                "rome_colosseum.jpg": [
                    "location": "Rome, Italy",
                    "date": "2024-07-20",
                    "camera": "iPhone 15 Pro",
                    "gps_coords": "41.9028, 12.4964"
                ]
            ]
        case "Family":
            return [
                "birthday_party.jpg": [
                    "location": "Home",
                    "date": "2024-08-12",
                    "camera": "iPhone 15",
                    "people": "Family gathering"
                ]
            ]
        default:
            return [:]
        }
    }
    
    private func createPlaceholderImageData(for fileName: String) -> Data? {
        // Create a simple colored square as placeholder image
        let size = CGSize(width: 200, height: 200)
        let color = getColorForPhoto(fileName)
        
        UIGraphicsBeginImageContext(size)
        color.setFill()
        UIRectFill(CGRect(origin: .zero, size: size))
        
        // Add text label
        let text = fileName.replacingOccurrences(of: ".jpg", with: "")
        let attributes: [NSAttributedString.Key: Any] = [
            .foregroundColor: UIColor.white,
            .font: UIFont.boldSystemFont(ofSize: 16)
        ]
        
        let textSize = text.size(withAttributes: attributes)
        let textRect = CGRect(
            x: (size.width - textSize.width) / 2,
            y: (size.height - textSize.height) / 2,
            width: textSize.width,
            height: textSize.height
        )
        
        text.draw(in: textRect, withAttributes: attributes)
        
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return image?.jpegData(compressionQuality: 0.8)
    }
    
    private func getColorForPhoto(_ fileName: String) -> UIColor {
        let hash = fileName.hashValue
        let colors: [UIColor] = [.systemBlue, .systemGreen, .systemOrange, .systemPurple, .systemRed, .systemTeal]
        return colors[abs(hash) % colors.count]
    }
    
    func getAllAlbums() -> [String] {
        let albumsURL = getAlbumsDirectory()
        do {
            let contents = try FileManager.default.contentsOfDirectory(atPath: albumsURL.path)
            return contents.filter { item in
                var isDirectory: ObjCBool = false
                FileManager.default.fileExists(atPath: albumsURL.appendingPathComponent(item).path, isDirectory: &isDirectory)
                return isDirectory.boolValue
            }
        } catch {
            return []
        }
    }
    
    func getPhotosInAlbum(_ albumName: String) -> [String] {
        let albumURL = getAlbumsDirectory().appendingPathComponent(albumName)
        do {
            let contents = try FileManager.default.contentsOfDirectory(atPath: albumURL.path)
            return contents.filter { $0.lowercased().hasSuffix(".jpg") || $0.lowercased().hasSuffix(".png") }
        } catch {
            return []
        }
    }
}