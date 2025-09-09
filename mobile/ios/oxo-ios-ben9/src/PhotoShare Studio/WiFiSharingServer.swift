//
//  WiFiSharingServer.swift
//  PhotoShare Studio
//
//  WiFi sharing server with intentional path traversal vulnerability
//

import Foundation
import Network
import SystemConfiguration

class WiFiSharingServer {
    private var listener: NWListener?
    private var isRunning = false
    private let port: UInt16 = 8080
    
    static let shared = WiFiSharingServer()
    
    private init() {}
    
    private func getContentType(for fileName: String) -> String {
        let lowercased = fileName.lowercased()
        if lowercased.hasSuffix(".jpg") || lowercased.hasSuffix(".jpeg") {
            return "image/jpeg"
        } else if lowercased.hasSuffix(".png") {
            return "image/png"
        } else if lowercased.hasSuffix(".json") {
            return "application/json"
        } else if lowercased.hasSuffix(".plist") {
            return "application/x-plist"
        } else if lowercased.hasSuffix(".sqlite") {
            return "application/x-sqlite3"
        } else {
            return "application/octet-stream"
        }
    }
    
    func getDeviceIPAddress() -> String {
        var address: String = "192.168.1.100"  // fallback
        var ifaddr: UnsafeMutablePointer<ifaddrs>?
        
        if getifaddrs(&ifaddr) == 0 {
            var ptr = ifaddr
            while ptr != nil {
                defer { ptr = ptr?.pointee.ifa_next }
                
                let interface = ptr?.pointee
                let addrFamily = interface?.ifa_addr.pointee.sa_family
                
                if addrFamily == UInt8(AF_INET) {
                    let name = String(cString: (interface?.ifa_name)!)
                    if name == "en0" || name == "en1" { // WiFi interfaces
                        var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
                        getnameinfo(interface?.ifa_addr, socklen_t((interface?.ifa_addr.pointee.sa_len)!),
                                  &hostname, socklen_t(hostname.count),
                                  nil, socklen_t(0), NI_NUMERICHOST)
                        address = String(cString: hostname)
                        break
                    }
                }
            }
            freeifaddrs(ifaddr)
        }
        return address
    }
    
    func startServer() {
        guard !isRunning else { return }
        
        do {
            listener = try NWListener(using: .tcp, on: NWEndpoint.Port(rawValue: port)!)
            listener?.stateUpdateHandler = { state in
                switch state {
                case .ready:
                    print("WiFi sharing server started on port \(self.port)")
                    self.isRunning = true
                case .failed(let error):
                    print("Server failed: \(error)")
                    self.isRunning = false
                default:
                    break
                }
            }
            
            listener?.newConnectionHandler = { connection in
                self.handleConnection(connection)
            }
            
            listener?.start(queue: .global())
        } catch {
            print("Failed to start server: \(error)")
        }
    }
    
    func stopServer() {
        listener?.cancel()
        listener = nil
        isRunning = false
        print("WiFi sharing server stopped")
    }
    
    func isServerRunning() -> Bool {
        return isRunning
    }
    
    private func handleConnection(_ connection: NWConnection) {
        connection.stateUpdateHandler = { state in
            switch state {
            case .ready:
                self.receiveRequest(connection)
            case .failed, .cancelled:
                connection.cancel()
            default:
                break
            }
        }
        connection.start(queue: .global())
    }
    
    private func receiveRequest(_ connection: NWConnection) {
        connection.receive(minimumIncompleteLength: 1, maximumLength: 65536) { data, _, isComplete, error in
            
            if let data = data, let request = String(data: data, encoding: .utf8) {
                let response = self.processRequest(request)
                
                connection.send(content: response, completion: .contentProcessed { error in
                    connection.cancel()
                })
            }
        }
    }
    
    private func processRequest(_ request: String) -> Data {
        print("Received request: \(request)")
        
        let lines = request.components(separatedBy: "\r\n")
        guard let requestLine = lines.first else {
            return createErrorResponse("Bad Request")
        }
        
        let components = requestLine.components(separatedBy: " ")
        guard components.count >= 2 else {
            return createErrorResponse("Bad Request")
        }
        
        let path = components[1]
        
        // Handle different endpoints
        if path == "/" {
            return createWebInterface()
        } else if path.hasPrefix("/album/") {
            let albumName = String(path.dropFirst(7)) // Remove "/album/"
            return createAlbumView(albumName: albumName)
        } else if path.hasPrefix("/view?photo=") {
            let photoPath = String(path.dropFirst(12)) // Remove "/view?photo="
            return createPhotoView(photoPath: photoPath)
        } else if path.hasPrefix("/download?file=") {
            let fileName = String(path.dropFirst(14)) // Remove "/download?file="
            return handleFileDownload(fileName)
        } else if path.hasPrefix("/photo?file=") {
            let fileName = String(path.dropFirst(12)) // Remove "/photo?file="
            return handlePhotoDownload(fileName)
        } else if path.hasPrefix("/download-album/") {
            let albumName = String(path.dropFirst(16)) // Remove "/download-album/"
            return handleAlbumDownload(albumName)
        } else {
            return createErrorResponse("Not Found")
        }
    }
    
    private func createWebInterface() -> Data {
        // Get all albums and photos dynamically
        let albums = PhotoManager.shared.getAllAlbums()
        var albumsHTML = ""
        
        for album in albums {
            let photos = PhotoManager.shared.getPhotosInAlbum(album)
            let photoCount = photos.count
            albumsHTML += """
            <div class="album">
                <div class="album-header">
                    <h3>\(album) Album</h3>
                    <span class="photo-count">\(photoCount) photos</span>
                    <a href="/album/\(album)" class="album-view-link">View Album</a>
                </div>
                <div class="album-preview">
            """
            
            // Show first 3 photos as preview
            let previewPhotos = Array(photos.prefix(3))
            for photo in previewPhotos {
                albumsHTML += """
                    <a href="/view?photo=\(album)/\(photo)" class="photo-preview">
                        <span class="photo-name">\(photo)</span>
                    </a>
                """
            }
            
            if photos.count > 3 {
                albumsHTML += """
                    <div class="more-photos">... and \(photos.count - 3) more photos</div>
                """
            }
            
            albumsHTML += """
                </div>
                <div class="album-actions">
                    <a href="/download?file=\(album)/metadata.json" class="action-link">📄 Download Metadata</a>
                </div>
            </div>
            """
        }
        
        let html = """
<!DOCTYPE html>
<html>
<head>
    <title>PhotoShare Studio - WiFi Transfer</title>
    <meta charset="UTF-8">
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, sans-serif; margin: 40px; background: #f8f9fa; }
        h1 { color: #007AFF; text-align: center; }
        h2 { color: #333; }
        .album { margin: 20px 0; padding: 20px; background: white; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .album-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
        .album h3 { color: #007AFF; margin: 0; }
        .photo-count { color: #666; font-size: 0.9em; }
        .album-view-link { color: #007AFF; text-decoration: none; font-weight: 500; }
        .album-view-link:hover { text-decoration: underline; }
        .album-preview { margin: 15px 0; }
        .photo-preview { 
            display: inline-block; 
            margin: 5px 10px 5px 0; 
            padding: 6px 12px;
            color: #007AFF; 
            text-decoration: none; 
            background: #f0f8ff;
            border-radius: 20px;
            font-size: 0.9em;
            transition: background 0.2s;
        }
        .photo-preview:hover { 
            background: #e0f0ff; 
            text-decoration: none;
        }
        .more-photos { color: #666; font-style: italic; margin-top: 10px; }
        .album-actions { margin-top: 15px; padding-top: 15px; border-top: 1px solid #eee; }
        .action-link {
            color: #007AFF; 
            text-decoration: none; 
            font-size: 0.9em;
        }
        .action-link:hover { text-decoration: underline; }
        .info { 
            margin-top: 30px; 
            padding: 20px; 
            background: #e3f2fd; 
            border-radius: 12px; 
            border: 1px solid #bbdefb;
        }
        .footer {
            margin-top: 30px;
            text-align: center;
            font-size: 0.9em;
            color: #666;
            border-top: 1px solid #ddd;
            padding-top: 20px;
        }
    </style>
</head>
<body>
    <h1>PhotoShare Studio</h1>
    <h2>Available Photos</h2>
    
    \(albumsHTML)
    
    <div class="info">
        <h3>Transfer Instructions</h3>
        <p>Click on any photo link to download it to your computer.</p>
        <p>All photos are organized by album and stored securely.</p>
    </div>
    
    <div class="footer">
        <p>PhotoShare Studio v1.0 - WiFi Transfer Service</p>
        <p>Server running on port 8080</p>
    </div>
</body>
</html>
"""
        
        let response = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: \(html.utf8.count)\r\nConnection: close\r\n\r\n\(html)"
        return response.data(using: .utf8) ?? Data()
    }
    
    private func createAlbumView(albumName: String) -> Data {
        let photos = PhotoManager.shared.getPhotosInAlbum(albumName)
        let photoCount = photos.count
        
        var photosHTML = ""
        for photo in photos {
            photosHTML += """
            <div class="photo-item">
                <h4>\(photo)</h4>
                <div class="photo-actions">
                    <a href="/view?photo=\(albumName)/\(photo)" class="btn btn-view">👁️ View</a>
                    <a href="/photo?file=\(albumName)/\(photo)" class="btn btn-download">⬇️ Download</a>
                </div>
            </div>
            """
        }
        
        let html = """
<!DOCTYPE html>
<html>
<head>
    <title>\(albumName) Album - PhotoShare Studio</title>
    <meta charset="UTF-8">
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, sans-serif; margin: 40px; background: #f8f9fa; }
        h1 { color: #007AFF; text-align: center; }
        .breadcrumb { margin-bottom: 20px; }
        .breadcrumb a { color: #007AFF; text-decoration: none; }
        .breadcrumb a:hover { text-decoration: underline; }
        .album-info { background: white; padding: 20px; border-radius: 12px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .photo-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; }
        .photo-item { background: white; padding: 20px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .photo-item h4 { color: #333; margin-top: 0; }
        .photo-actions { display: flex; gap: 10px; }
        .btn { padding: 8px 16px; border-radius: 6px; text-decoration: none; font-weight: 500; }
        .btn-view { background: #007AFF; color: white; }
        .btn-download { background: #34c759; color: white; }
        .btn-download-all { background: #ff9500; color: white; padding: 12px 24px; font-size: 16px; font-weight: 600; }
        .btn:hover { opacity: 0.8; }
        .download-all-section { margin: 20px 0; text-align: center; }
        .share-section { background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 8px; margin-top: 20px; }
    </style>
</head>
<body>
    <div class="breadcrumb">
        <a href="/">← Back to All Albums</a>
    </div>
    
    <h1>\(albumName) Album</h1>
    
    <div class="album-info">
        <p><strong>\(photoCount) photos</strong> in this album</p>
        <div class="download-all-section">
            <a href="/download-album/\(albumName)" class="btn btn-download-all">📦 Download Entire Album</a>
        </div>
        <div class="share-section">
            <strong>📤 Share this album:</strong> 
            <code>http://\(getDeviceIPAddress()):8080/album/\(albumName)</code>
        </div>
    </div>
    
    <div class="photo-grid">
        \(photosHTML)
    </div>
</body>
</html>
"""
        
        let response = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: \(html.utf8.count)\r\nConnection: close\r\n\r\n\(html)"
        return response.data(using: .utf8) ?? Data()
    }
    
    private func createPhotoView(photoPath: String) -> Data {
        let components = photoPath.split(separator: "/")
        guard components.count >= 2 else {
            return createErrorResponse("Invalid photo path")
        }
        
        let albumName = String(components[0])
        let photoName = String(components[1])
        
        let html = """
<!DOCTYPE html>
<html>
<head>
    <title>\(photoName) - PhotoShare Studio</title>
    <meta charset="UTF-8">
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, sans-serif; margin: 40px; background: #f8f9fa; }
        h1 { color: #007AFF; text-align: center; }
        .breadcrumb { margin-bottom: 20px; }
        .breadcrumb a { color: #007AFF; text-decoration: none; margin-right: 10px; }
        .breadcrumb a:hover { text-decoration: underline; }
        .photo-container { background: white; padding: 30px; border-radius: 12px; text-align: center; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        .photo-info { margin: 20px 0; }
        .download-section { margin-top: 30px; }
        .btn { padding: 12px 24px; background: #007AFF; color: white; text-decoration: none; border-radius: 8px; font-weight: 500; display: inline-block; }
        .btn:hover { background: #0056d6; }
        .share-section { background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 8px; margin-top: 20px; }
        .photo-placeholder { 
            width: 300px; 
            height: 300px; 
            background: linear-gradient(45deg, #007AFF, #5ac8fa); 
            margin: 20px auto; 
            border-radius: 12px; 
            display: flex; 
            align-items: center; 
            justify-content: center; 
            color: white; 
            font-size: 18px; 
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="breadcrumb">
        <a href="/">← All Albums</a>
        <a href="/album/\(albumName)">← \(albumName) Album</a>
    </div>
    
    <h1>\(photoName)</h1>
    
    <div class="photo-container">
        <div class="photo-placeholder">
            📷 \(photoName)
        </div>
        
        <div class="photo-info">
            <p><strong>Album:</strong> \(albumName)</p>
            <p><strong>File:</strong> \(photoName)</p>
        </div>
        
        <div class="download-section">
            <a href="/photo?file=\(albumName)/\(photoName)" class="btn">⬇️ Download Photo</a>
        </div>
        
        <div class="share-section">
            <strong>📤 Share this photo:</strong><br>
            <code>http://\(getDeviceIPAddress()):8080/view?photo=\(albumName)/\(photoName)</code>
        </div>
    </div>
</body>
</html>
"""
        
        let response = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: \(html.utf8.count)\r\nConnection: close\r\n\r\n\(html)"
        return response.data(using: .utf8) ?? Data()
    }
    
    private func handleAlbumDownload(_ albumName: String) -> Data {
        print("Album download request: \(albumName)")
        
        // Create a simple text file with all photo filenames (simulating zip)
        let albumPath = PhotoManager.shared.getAlbumsDirectory().appendingPathComponent(albumName)
        
        guard FileManager.default.fileExists(atPath: albumPath.path) else {
            return createErrorResponse("Album Not Found")
        }
        
        do {
            let photos = try FileManager.default.contentsOfDirectory(atPath: albumPath.path)
            let photoFiles = photos.filter { $0.lowercased().hasSuffix(".jpg") || $0.lowercased().hasSuffix(".png") }
            
            // Create a simple manifest file (in real app this would be a ZIP)
            let manifest = """
            PhotoShare Studio - Album Download
            ==================================
            Album: \(albumName)
            Downloaded: \(Date())
            Photos: \(photoFiles.count)
            
            Photo Files:
            \(photoFiles.map { "• \($0)" }.joined(separator: "\n"))
            
            Note: This is a demo manifest file. 
            In a real app, this would be a ZIP file containing all photos.
            
            Individual photos can be downloaded at:
            \(photoFiles.map { "http://\(getDeviceIPAddress()):8080/photo?file=\(albumName)/\($0)" }.joined(separator: "\n"))
            """
            
            let manifestData = manifest.data(using: .utf8) ?? Data()
            
            let headers = [
                "HTTP/1.1 200 OK",
                "Content-Type: text/plain; charset=utf-8",
                "Content-Disposition: attachment; filename=\"\(albumName)_Album_Manifest.txt\"",
                "Content-Length: \(manifestData.count)",
                "Cache-Control: no-cache",
                "Connection: close",
                "",
                ""
            ].joined(separator: "\r\n")
            
            var responseData = headers.data(using: .utf8) ?? Data()
            responseData.append(manifestData)
            return responseData
            
        } catch {
            return createErrorResponse("Error Reading Album")
        }
    }
    
    // VULNERABLE FUNCTION - Path Traversal Vulnerability
    private func handleFileDownload(_ fileName: String) -> Data {
        print("File download request: \(fileName)")
        
        // VULNERABILITY: Direct path concatenation without validation
        // This allows path traversal attacks using "../" sequences
        let documentsPath = PhotoManager.shared.getDocumentsDirectory()
        let filePath = documentsPath.appendingPathComponent("Albums/\(fileName)")
        
        print("Attempting to access file: \(filePath.path)")
        
        // Check if file exists and read it
        if FileManager.default.fileExists(atPath: filePath.path) {
            do {
                let fileData = try Data(contentsOf: filePath)
                let cleanFileName = URL(fileURLWithPath: fileName).lastPathComponent
                let contentType = getContentType(for: cleanFileName)
                
                let headers = [
                    "HTTP/1.1 200 OK",
                    "Content-Type: \(contentType)",
                    "Content-Disposition: attachment; filename=\"\(cleanFileName)\"",
                    "Content-Length: \(fileData.count)",
                    "Cache-Control: no-cache",
                    "Connection: close",
                    "",
                    ""
                ].joined(separator: "\r\n")
                
                var responseData = headers.data(using: .utf8) ?? Data()
                responseData.append(fileData)
                return responseData
            } catch {
                print("Error reading file: \(error)")
                return createErrorResponse("Internal Server Error")
            }
        } else {
            print("File not found: \(filePath.path)")
            return createErrorResponse("File Not Found")
        }
    }
    
    // VULNERABLE FUNCTION - Alternative path traversal endpoint
    private func handlePhotoDownload(_ fileName: String) -> Data {
        print("Photo download request: \(fileName)")
        
        // VULNERABILITY: Even more direct path traversal
        // First try Albums subdirectory, then direct path
        let documentsPath = PhotoManager.shared.getDocumentsDirectory()
        var photoPath = documentsPath.appendingPathComponent("Albums/\(fileName)")
        
        print("Attempting to access photo: \(photoPath.path)")
        
        // If file doesn't exist in Albums, try direct path (for path traversal)
        if !FileManager.default.fileExists(atPath: photoPath.path) {
            photoPath = documentsPath.appendingPathComponent(fileName)
            print("Trying direct path: \(photoPath.path)")
        }
        
        if FileManager.default.fileExists(atPath: photoPath.path) {
            do {
                let photoData = try Data(contentsOf: photoPath)
                let cleanFileName = URL(fileURLWithPath: fileName).lastPathComponent
                let contentType = getContentType(for: cleanFileName)
                
                let headers = [
                    "HTTP/1.1 200 OK",
                    "Content-Type: \(contentType)",
                    "Content-Disposition: attachment; filename=\"\(cleanFileName)\"",
                    "Content-Length: \(photoData.count)",
                    "Cache-Control: no-cache",
                    "Connection: close",
                    "",
                    ""
                ].joined(separator: "\r\n")
                
                var responseData = headers.data(using: .utf8) ?? Data()
                responseData.append(photoData)
                return responseData
            } catch {
                return createErrorResponse("Internal Server Error")
            }
        } else {
            return createErrorResponse("Photo Not Found")
        }
    }
    
    private func createErrorResponse(_ message: String) -> Data {
        let html = """
        <!DOCTYPE html>
        <html>
        <head><title>Error - PhotoShare Studio</title></head>
        <body>
            <h1>Error</h1>
            <p>\(message)</p>
            <a href="/">Back to Main</a>
        </body>
        </html>
        """
        
        let response = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\nContent-Length: \(html.count)\r\n\r\n\(html)"
        return response.data(using: .utf8) ?? Data()
    }
}