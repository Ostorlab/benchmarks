//
//  Photo.swift
//  PhotoShare
//
//  Created by elyousfi on 10/09/2025.
//

import Foundation
import SwiftUI

struct Photo: Identifiable, Codable {
    let id = UUID()
    var title: String
    var imageName: String
    var dateCreated: Date
    var location: String?
    var albumId: String?
    var isPrivate: Bool
    var tags: [String]
    var sharedWith: [String]
    var filePath: String
    
    init(title: String, imageName: String, location: String? = nil, albumId: String? = nil, isPrivate: Bool = false, tags: [String] = [], filePath: String = "") {
        self.title = title
        self.imageName = imageName
        self.dateCreated = Date()
        self.location = location
        self.albumId = albumId
        self.isPrivate = isPrivate
        self.tags = tags
        self.sharedWith = []
        self.filePath = filePath.isEmpty ? "Photos/\(imageName)" : filePath
    }
}

struct Album: Identifiable, Codable {
    let id = UUID()
    var name: String
    var description: String
    var isPrivate: Bool
    var coverImageName: String?
    var photoIds: [UUID]
    var dateCreated: Date
    var sharedWith: [String]
    
    init(name: String, description: String = "", isPrivate: Bool = false, coverImageName: String? = nil) {
        self.name = name
        self.description = description
        self.isPrivate = isPrivate
        self.coverImageName = coverImageName
        self.photoIds = []
        self.dateCreated = Date()
        self.sharedWith = []
    }
}

struct ShareRequest {
    let photoId: String
    let recipient: String
    let message: String?
    let timestamp: Date
    
    init(photoId: String, recipient: String, message: String? = nil) {
        self.photoId = photoId
        self.recipient = recipient
        self.message = message
        self.timestamp = Date()
    }
}
