//
//  NavigationManager.swift
//  PhotoShare
//
//  Created by elyousfi on 10/09/2025.
//

import Foundation
import SwiftUI
import Combine

class NavigationManager: ObservableObject {
    @Published var selectedTab: Tab = .photos
    @Published var navigationPath = NavigationPath()
    @Published var selectedPhotoId: String?
    @Published var showingPhotoDetail = false
    @Published var showingShareSheet = false
    
    enum Tab: String, CaseIterable {
        case photos = "Photos"
        case albums = "Albums"
        case shared = "Shared"
        case profile = "Profile"
        
        var iconName: String {
            switch self {
            case .photos: return "photo.on.rectangle"
            case .albums: return "folder"
            case .shared: return "person.2"
            case .profile: return "person.circle"
            }
        }
    }
    
    func navigateToPhoto(photoId: String) {
        DispatchQueue.main.async {
            self.selectedPhotoId = photoId
            self.showingPhotoDetail = true
            self.selectedTab = .photos
        }
    }
    
    func resetNavigation() {
        DispatchQueue.main.async {
            self.navigationPath = NavigationPath()
            self.selectedPhotoId = nil
            self.showingPhotoDetail = false
            self.showingShareSheet = false
        }
    }
    
    func presentShareSheet() {
        DispatchQueue.main.async {
            self.showingShareSheet = true
        }
    }
}
