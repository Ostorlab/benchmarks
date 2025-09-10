//
//  PhotoShareApp.swift
//  PhotoShare
//
//  Created by elyousfi on 10/09/2025.
//

import SwiftUI
import Combine

@main
struct PhotoShareApp: App {
    @StateObject private var photoManager = PhotoManager()
    @StateObject private var navigationManager = NavigationManager()
    
    var body: some Scene {
        WindowGroup {
            MainTabView()
                .environmentObject(photoManager)
                .environmentObject(navigationManager)
                .onOpenURL { url in
                    handleIncomingURL(url)
                }
        }
    }
    
    private func handleIncomingURL(_ url: URL) {
        guard url.scheme == "photoshare" else { return }
        
        let components = URLComponents(url: url, resolvingAgainstBaseURL: false)
        guard let queryItems = components?.queryItems else { return }
        
        var parameters: [String: String] = [:]
        for item in queryItems {
            if let value = item.value {
                parameters[item.name] = value
            }
        }
        
        processDeeplinkAction(host: url.host, parameters: parameters)
    }
    
    private func processDeeplinkAction(host: String?, parameters: [String: String]) {
        guard let action = host else { return }
        
        switch action {
        case "share":
            if let photoId = parameters["photo_id"],
               let recipient = parameters["recipient"] {
                photoManager.sharePhoto(photoId: photoId, recipient: recipient)
            }
        case "delete":
            if let photoId = parameters["photo_id"] {
                photoManager.deletePhoto(photoId: photoId)
            }
        case "export":
            if let albumId = parameters["album_id"],
               let path = parameters["path"] {
                photoManager.exportAlbum(albumId: albumId, to: path)
            }
        case "view":
            if let photoId = parameters["photo_id"] {
                navigationManager.navigateToPhoto(photoId: photoId)
            }
        case "import":
            if let sourcePath = parameters["source"] {
                photoManager.importPhotos(from: sourcePath)
            }
        default:
            break
        }
    }
}
