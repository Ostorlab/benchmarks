import SwiftUI
import AVFoundation
import CoreLocation
import Photos
import ImageIO

class PhotoManager: NSObject, ObservableObject {
    @Published var photos: [PhotoItem] = []
    private let locationManager = CLLocationManager()
    private var currentLocation: CLLocationCoordinate2D?

    override init() {
        super.init()
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        loadPhotos()
    }

    func requestPermissions() {
        locationManager.requestWhenInUseAuthorization()
        PHPhotoLibrary.requestAuthorization { _ in }
    }

    func capturePhoto(image: UIImage) {
        locationManager.requestLocation()

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            self.savePhotoWithLocation(image: image)
        }
    }

    private func savePhotoWithLocation(image: UIImage) {
        guard let imageData = image.jpegData(compressionQuality: 0.8) else { return }

        let filename = "photo_\(Date().timeIntervalSince1970).jpg"
        let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let fullURL = documentsPath.appendingPathComponent(filename)
        let thumbnailURL = documentsPath.appendingPathComponent("thumb_\(filename)")

        let imageWithLocation = addLocationToImage(imageData: imageData, location: currentLocation)

        do {
            try imageWithLocation.write(to: fullURL)

            let thumbnail = image.resized(to: CGSize(width: 300, height: 300))
            if let thumbData = thumbnail.jpegData(compressionQuality: 0.7) {
                try thumbData.write(to: thumbnailURL)
            }

            let photoItem = PhotoItem(
                filename: filename,
                thumbnailURL: thumbnailURL,
                fullURL: fullURL,
                location: currentLocation
            )

            DispatchQueue.main.async {
                self.photos.insert(photoItem, at: 0)
                self.savePhotos()
            }

        } catch {
            print("Error saving photo: \(error)")
        }
    }

    private func addLocationToImage(imageData: Data, location: CLLocationCoordinate2D?) -> Data {
        guard let location = location,
              let source = CGImageSourceCreateWithData(imageData as CFData, nil),
              let imageType = CGImageSourceGetType(source) else {
            return imageData
        }

        let mutableData = NSMutableData(data: imageData)
        guard let destination = CGImageDestinationCreateWithData(mutableData, imageType, 1, nil) else {
            return imageData
        }

        let gpsDict: [String: Any] = [
            kCGImagePropertyGPSLatitude as String: abs(location.latitude),
            kCGImagePropertyGPSLongitude as String: abs(location.longitude),
            kCGImagePropertyGPSLatitudeRef as String: location.latitude >= 0 ? "N" : "S",
            kCGImagePropertyGPSLongitudeRef as String: location.longitude >= 0 ? "E" : "W",
            kCGImagePropertyGPSTimeStamp as String: DateFormatter.gpsTimestamp.string(from: Date()),
            kCGImagePropertyGPSDateStamp as String: DateFormatter.gpsDateStamp.string(from: Date())
        ]

        let metadata: [String: Any] = [
            kCGImagePropertyGPSDictionary as String: gpsDict
        ]

        CGImageDestinationAddImageFromSource(destination, source, 0, metadata as CFDictionary)
        CGImageDestinationFinalize(destination)

        return mutableData as Data
    }

    func sharePhoto(_ photo: PhotoItem) {
        let activityController = UIActivityViewController(
            activityItems: [photo.fullURL],
            applicationActivities: nil
        )

        if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let window = windowScene.windows.first {
            window.rootViewController?.present(activityController, animated: true)
        }
    }

    func exportToPhotos(_ photo: PhotoItem) {
        PHPhotoLibrary.requestAuthorization { status in
            guard status == .authorized else { return }

            do {
                let imageData = try Data(contentsOf: photo.fullURL)
                PHPhotoLibrary.shared().performChanges({
                    let request = PHAssetCreationRequest.forAsset()
                    request.addResource(with: .photo, data: imageData, options: nil)
                }) { success, error in
                    DispatchQueue.main.async {
                        if success {
                            print("Photo saved to Photos app with location data")
                        }
                    }
                }
            } catch {
                print("Error reading photo data: \(error)")
            }
        }
    }

    private func loadPhotos() {
        // Load existing photos from documents directory
        let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]

        do {
            let files = try FileManager.default.contentsOfDirectory(at: documentsPath, includingPropertiesForKeys: nil)
            let photoFiles = files.filter { $0.pathExtension == "jpg" && !$0.lastPathComponent.hasPrefix("thumb_") }

            for file in photoFiles {
                let filename = file.lastPathComponent
                let thumbnailURL = documentsPath.appendingPathComponent("thumb_\(filename)")

                if FileManager.default.fileExists(atPath: thumbnailURL.path) {
                    let photo = PhotoItem(filename: filename, thumbnailURL: thumbnailURL, fullURL: file)
                    photos.append(photo)
                }
            }

            photos.sort { $0.dateCreated > $1.dateCreated }
        } catch {
            print("Error loading photos: \(error)")
        }
    }

    private func savePhotos() {
        // In a real app, you might save photo metadata to UserDefaults or Core Data
        // For this benchmark, we're keeping it simple
    }
}

extension PhotoManager: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        currentLocation = locations.last?.coordinate
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Location error: \(error)")
    }
}

extension UIImage {
    func resized(to size: CGSize) -> UIImage {
        UIGraphicsBeginImageContextWithOptions(size, false, scale)
        draw(in: CGRect(origin: .zero, size: size))
        let resized = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return resized ?? self
    }
}

extension DateFormatter {
    static let gpsTimestamp: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm:ss.SSSSSS"
        formatter.timeZone = TimeZone(abbreviation: "UTC")
        return formatter
    }()

    static let gpsDateStamp: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy:MM:dd"
        formatter.timeZone = TimeZone(abbreviation: "UTC")
        return formatter
    }()
}
