import Foundation
import CoreLocation

struct PhotoItem: Identifiable, Codable {
    let id = UUID()
    let filename: String
    let thumbnailURL: URL
    let fullURL: URL
    let dateCreated: Date
    let location: CLLocationCoordinate2D?

    init(filename: String, thumbnailURL: URL, fullURL: URL, dateCreated: Date = Date(), location: CLLocationCoordinate2D? = nil) {
        self.filename = filename
        self.thumbnailURL = thumbnailURL
        self.fullURL = fullURL
        self.dateCreated = dateCreated
        self.location = location
    }
}

extension CLLocationCoordinate2D: Codable {
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(latitude, forKey: .latitude)
        try container.encode(longitude, forKey: .longitude)
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let latitude = try container.decode(Double.self, forKey: .latitude)
        let longitude = try container.decode(Double.self, forKey: .longitude)
        self.init(latitude: latitude, longitude: longitude)
    }

    private enum CodingKeys: String, CodingKey {
        case latitude, longitude
    }
}
