//
//  SocialAPIService.swift
//  SocialShareHub
//
//  Social media API authentication service

import Foundation
import Network

class SocialAPIService {
    static let shared = SocialAPIService()
    
    private var listener: NWListener?
    private let port: UInt16 = 8080
    private var isRunning = false
    
    // Mock user database - simulates real users in the system
    private let mockUsers: [String: [String: Any]] = [
        "facebook_user_1": [
            "id": "fb_123456789",
            "name": "Sarah Johnson",
            "email": "sarah.johnson@email.com",
            "provider": "facebook",
            "connected_accounts": ["Facebook", "Instagram"]
        ],
        "facebook_user_2": [
            "id": "fb_987654321", 
            "name": "Mike Chen",
            "email": "mike.chen@email.com",
            "provider": "facebook",
            "connected_accounts": ["Facebook", "Twitter"]
        ],
        "facebook_user_3": [
            "id": "fb_456789123",
            "name": "Emma Williams",
            "email": "emma.williams@email.com", 
            "provider": "facebook",
            "connected_accounts": ["Facebook", "LinkedIn", "Instagram"]
        ],
        "twitter_user_1": [
            "id": "tw_111222333",
            "name": "Alex Rodriguez",
            "email": "alex.rodriguez@email.com",
            "provider": "twitter", 
            "connected_accounts": ["Twitter", "LinkedIn"]
        ]
    ]
    
    private init() {}
    
    // MARK: - Server Control
    
    func startServer() {
        if isRunning {
            print("✅ Server already running on port \(port)")
            return 
        }
        
        print("🔄 Attempting to start server on port \(port)")
        
        do {
            listener = try NWListener(using: .tcp, on: NWEndpoint.Port(rawValue: port)!)
            
            listener?.stateUpdateHandler = { [weak self] state in
                DispatchQueue.main.async {
                    switch state {
                    case .ready:
                        self?.isRunning = true
                        print("🔴 OAuth server started on port \(self?.port ?? 0)")
                        print("🔴 API endpoint: http://127.0.0.1:\(self?.port ?? 0)/api/auth/social")
                        print("🔴 Status endpoint: http://127.0.0.1:\(self?.port ?? 0)/status")
                    case .failed(let error):
                        print("❌ Server failed: \(error)")
                        self?.isRunning = false
                    case .waiting(let error):
                        print("⚠️ Server waiting: \(error)")
                    case .cancelled:
                        print("⚪ Server cancelled")
                        self?.isRunning = false
                    case .setup:
                        print("🔄 Server setting up...")
                    @unknown default:
                        print("❓ Unknown server state: \(state)")
                    }
                }
            }
            
            listener?.newConnectionHandler = { [weak self] connection in
                print("🔗 New connection received")
                self?.handleNewConnection(connection)
            }
            
            listener?.start(queue: .main)
            
        } catch {
            print("❌ Failed to start server: \(error)")
        }
    }
    
    func stopServer() {
        listener?.cancel()
        listener = nil
        isRunning = false
        print("⚪ OAuth server stopped")
    }
    
    var serverStatus: String {
        return isRunning ? "🔴 Running on :8080" : "⚪ Stopped"
    }
    
    // MARK: - Connection Handling
    
    private func handleNewConnection(_ connection: NWConnection) {
        connection.stateUpdateHandler = { state in
            switch state {
            case .ready:
                self.receiveRequest(on: connection)
            case .failed(let error):
                print("❌ Connection failed: \(error)")
            default:
                break
            }
        }
        connection.start(queue: .main)
    }
    
    private func receiveRequest(on connection: NWConnection) {
        var receivedData = Data()
        var expectedContentLength: Int?
        
        func receiveMore() {
            connection.receive(minimumIncompleteLength: 1, maximumLength: 4096) { [weak self] data, _, isComplete, error in
                
                if let error = error {
                    print("❌ Receive error: \(error)")
                    connection.cancel()
                    return
                }
                
                if let data = data, !data.isEmpty {
                    receivedData.append(data)
                    print("🔍 Received \(data.count) bytes, total: \(receivedData.count) bytes")
                }
                
                // Convert to string and check if we have headers
                let currentRequest = String(data: receivedData, encoding: .utf8) ?? ""
                
                // Extract Content-Length from headers if not already done
                if expectedContentLength == nil, let contentLengthRange = currentRequest.range(of: "Content-Length: (\\d+)", options: .regularExpression) {
                    let contentLengthString = String(currentRequest[contentLengthRange])
                    if let length = Int(contentLengthString.replacingOccurrences(of: "Content-Length: ", with: "")) {
                        expectedContentLength = length
                        print("🔍 Expected content length: \(length)")
                    }
                }
                
                // Check if we have complete HTTP request (headers + body)
                let hasCompleteHeaders = currentRequest.contains("\r\n\r\n")
                let headerEndIndex = currentRequest.range(of: "\r\n\r\n")?.upperBound
                
                if hasCompleteHeaders, let headerEnd = headerEndIndex {
                    let bodyStart = currentRequest.distance(from: currentRequest.startIndex, to: headerEnd)
                    let bodyLength = receivedData.count - bodyStart
                    
                    print("🔍 Headers complete, body length: \(bodyLength), expected: \(expectedContentLength ?? 0)")
                    
                    // Check if we have complete body
                    if let expectedLength = expectedContentLength {
                        if bodyLength >= expectedLength {
                            print("🔍 Complete request received")
                            self?.processCompleteRequest(currentRequest, receivedData: receivedData, connection: connection)
                            return
                        } else {
                            print("🔍 Need more body data, continuing...")
                        }
                    } else {
                        // No content-length header, process immediately
                        self?.processCompleteRequest(currentRequest, receivedData: receivedData, connection: connection)
                        return
                    }
                }
                
                if isComplete {
                    print("🔍 Connection complete, processing request")
                    self?.processCompleteRequest(currentRequest, receivedData: receivedData, connection: connection)
                } else {
                    receiveMore()
                }
            }
        }
        
        receiveMore()
    }
    
    private func processCompleteRequest(_ request: String, receivedData: Data, connection: NWConnection) {
        print("🔍 Processing complete HTTP request (\(receivedData.count) bytes)")
        
        let response = self.processRequest(request) ?? "HTTP/1.1 500 Internal Server Error\r\n\r\nError"
        
        guard let responseData = response.data(using: .utf8) else {
            connection.cancel()
            return
        }
        
        print("🔍 Sending response (\(responseData.count) bytes)")
        
        connection.send(content: responseData, completion: .contentProcessed { sendError in
            if let sendError = sendError {
                print("❌ Send error: \(sendError)")
            } else {
                print("✅ Response sent successfully")
            }
            connection.cancel()
        })
    }
    
    // MARK: - Request Processing
    
    private func processRequest(_ request: String) -> String? {
        print("📥 Received request:")
        print(request)
        
        // Parse HTTP request
        let lines = request.components(separatedBy: .newlines)
        guard let requestLine = lines.first else { return createErrorResponse() }
        
        let components = requestLine.components(separatedBy: " ")
        guard components.count >= 2 else { return createErrorResponse() }
        
        let method = components[0]
        let path = components[1]
        
        print("🔍 HTTP Method: \(method), Path: \(path)")
        
        // Handle fake Facebook OAuth endpoint
        if method == "POST" && path == "/api/facebook/oauth" {
            return handleFacebookOAuth(request)
        }
        
        // Handle OAuth authentication endpoint
        if method == "POST" && path == "/api/auth/social" {
            return handleOAuthAuthentication(request)
        }
        
        // Handle status endpoint for debugging
        if method == "GET" && (path == "/status" || path == "/") {
            return createStatusResponse()
        }
        
        print("❌ Unhandled request: \(method) \(path)")
        return createNotFoundResponse()
    }
    
    // MARK: - OAuth Authentication Handler (VULNERABLE)
    
    private func handleOAuthAuthentication(_ request: String) -> String {
        // Extract JSON payload from POST body
        guard let jsonString = extractJSONFromRequest(request) else {
            print("❌ Could not extract JSON from request")
            return createErrorResponse(message: "Could not extract JSON payload")
        }
        
        print("🔍 Extracted JSON: \(jsonString)")
        
        guard let jsonData = jsonString.data(using: .utf8) else {
            print("❌ Could not convert JSON string to data")
            return createErrorResponse(message: "Invalid JSON string")
        }
        
        guard let payload = try? JSONSerialization.jsonObject(with: jsonData) as? [String: Any] else {
            print("❌ Could not parse JSON data")
            return createErrorResponse(message: "Invalid JSON format")
        }
        
        print("🔍 Parsed payload: \(payload)")
        
        guard let provider = payload["provider"] as? String,
              let accessToken = payload["access_token"] as? String else {
            print("❌ Missing provider or access_token in payload")
            return createErrorResponse(message: "Missing provider or access_token")
        }
        
        print("🔍 Processing OAuth request:")
        print("  Provider: \(provider)")
        print("  Token: \(accessToken.prefix(20))...")
        
        // VULNERABLE: Token validation bypass
        let isValidToken = validateTokenFormat(accessToken, provider: provider)
        print("🔍 Token validation result: \(isValidToken)")
        
        if isValidToken {
            let userData = selectUserForToken(provider: provider, token: accessToken)
            print("🔍 Selected user data: \(userData)")
            
            print("✅ Authentication successful")
            print("  Authenticated as: \(userData["name"] ?? "Unknown")")
            
            let response: [String: Any] = [
                "success": true,
                "user": userData,
                "message": "Authentication successful",
                "debug_info": "Token validated successfully"
            ]
            
            print("🔍 Creating JSON response...")
            let jsonResponse = createJSONResponse(response)
            print("🔍 JSON response created, length: \(jsonResponse.count)")
            return jsonResponse
        } else {
            print("❌ Token format invalid")
            return createErrorResponse(message: "Invalid token format")
        }
    }
    
    // MARK: - Fake Facebook OAuth Handler
    
    private func handleFacebookOAuth(_ request: String) -> String {
        // Extract credentials from POST body
        guard let jsonString = extractJSONFromRequest(request) else {
            print("❌ Could not extract JSON from Facebook OAuth request")
            return createErrorResponse(message: "Could not extract JSON payload")
        }
        
        print("🔍 Facebook OAuth JSON: \(jsonString)")
        
        guard let jsonData = jsonString.data(using: .utf8),
              let payload = try? JSONSerialization.jsonObject(with: jsonData) as? [String: Any] else {
            print("❌ Could not parse Facebook OAuth JSON")
            return createErrorResponse(message: "Invalid JSON format")
        }
        
        guard let username = payload["username"] as? String,
              let password = payload["password"] as? String else {
            print("❌ Missing username or password in Facebook OAuth request")
            return createErrorResponse(message: "Missing username or password")
        }
        
        print("🔍 Facebook OAuth attempt: \(username)")
        
        // Valid SocialShare Hub test accounts
        let validAccounts: [String: String] = [
            "sarah.johnson@email.com": "password123",
            "mike.chen@email.com": "password456", 
            "emma.williams@email.com": "password789"
        ]
        
        if let validPassword = validAccounts[username.lowercased()], 
           validPassword == password {
            // Generate legitimate SocialShare Hub token
            let token = generateLegitimateToken(for: username)
            
            let response: [String: Any] = [
                "success": true,
                "access_token": token,
                "user_id": extractUserIdFromEmail(username),
                "app_id": "socialsharehub_app",
                "message": "Facebook OAuth successful"
            ]
            
            print("✅ Facebook OAuth successful for: \(username)")
            return createJSONResponse(response)
        } else {
            print("❌ Invalid Facebook credentials: \(username)")
            return createErrorResponse(message: "Invalid username or password")
        }
    }
    
    private func generateLegitimateToken(for username: String) -> String {
        // Generate tokens that are specifically for SocialShare Hub
        let userPrefix = username.prefix(5).lowercased()
        let appIdentifier = "socialsharehub"
        let randomSuffix = String((0..<20).map { _ in "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".randomElement()! })
        
        return "EAABwzLixnjYBO\(userPrefix)\(appIdentifier)\(randomSuffix)"
    }
    
    private func extractUserIdFromEmail(_ email: String) -> String {
        let emailParts = email.components(separatedBy: "@")
        return "fb_\(emailParts[0].replacingOccurrences(of: ".", with: "_"))"
    }
    
    // MARK: - Token Validation (FLAWED)
    
    private func validateTokenFormat(_ token: String, provider: String) -> Bool {
        switch provider.lowercased() {
        case "facebook":
            // Only check format, NOT app origin (vulnerability!)
            return token.hasPrefix("EAA") && token.count > 20
        case "twitter":
            return token.count > 20 && token.allSatisfy { $0.isLetter || $0.isNumber || $0 == "-" || $0 == "_" }
        default:
            return false
        }
    }
    
    private func selectUserForToken(provider: String, token: String) -> [String: Any] {
        // REALISTIC: Token determines which user data is returned
        // In real attack: each stolen token corresponds to a specific victim
        
        // Hash the token to consistently map to the same user
        let tokenHash = abs(token.hashValue) % mockUsers.count
        let providerUsers = mockUsers.filter { $0.value["provider"] as? String == provider }
        
        if !providerUsers.isEmpty {
            let userArray = Array(providerUsers.values)
            return userArray[tokenHash % userArray.count]
        }
        
        // Fallback to any user if no provider match
        let allUsers = Array(mockUsers.values)
        return allUsers[tokenHash % allUsers.count]
    }
    
    // MARK: - Response Generation
    
    private func createJSONResponse(_ data: [String: Any]) -> String {
        print("🔍 Serializing JSON data: \(data)")
        
        guard let jsonData = try? JSONSerialization.data(withJSONObject: data) else {
            print("❌ Failed to serialize JSON data")
            return createErrorResponse()
        }
        
        guard let jsonString = String(data: jsonData, encoding: .utf8) else {
            print("❌ Failed to convert JSON data to string")
            return createErrorResponse()
        }
        
        print("🔍 JSON string: \(jsonString)")
        
        let httpResponse = """
        HTTP/1.1 200 OK\r
        Content-Type: application/json\r
        Access-Control-Allow-Origin: *\r
        Access-Control-Allow-Methods: POST, GET, OPTIONS\r
        Access-Control-Allow-Headers: Content-Type\r
        Content-Length: \(jsonString.utf8.count)\r
        \r
        \(jsonString)
        """
        
        print("🔍 HTTP response ready, length: \(httpResponse.count)")
        return httpResponse
    }
    
    private func createErrorResponse(message: String = "Internal Server Error") -> String {
        let error: [String: Any] = ["success": false, "error": message]
        return createJSONResponse(error)
    }
    
    private func createStatusResponse() -> String {
        let status: [String: Any] = [
            "server": "SocialShareHub API",
            "status": "running",
            "version": "1.0.0",
            "endpoints": ["/api/auth/social", "/status"]
        ]
        return createJSONResponse(status)
    }
    
    private func createNotFoundResponse() -> String {
        return "HTTP/1.1 404 Not Found\r\nContent-Length: 9\r\n\r\nNot Found"
    }
    
    // MARK: - Utility Methods
    
    private func extractJSONFromRequest(_ request: String) -> String? {
        print("🔍 Full request to parse:")
        print("--- REQUEST START ---")
        print(request)
        print("--- REQUEST END ---")
        
        // Method 1: Look for JSON pattern anywhere in the request
        let jsonPattern = "\\{.*\"(access_token|provider)\".*\\}"
        if let range = request.range(of: jsonPattern, options: .regularExpression) {
            let jsonString = String(request[range])
            print("🔍 RegEx extracted JSON: \(jsonString)")
            return jsonString
        }
        
        // Method 2: Split by double newline (HTTP standard)
        let components = request.components(separatedBy: "\r\n\r\n")
        if components.count > 1 {
            let body = components[1].trimmingCharacters(in: .whitespacesAndNewlines)
            print("🔍 CRLF body: '\(body)'")
            if !body.isEmpty { return body }
        }
        
        // Method 3: Split by single newline + find empty line
        let lines = request.components(separatedBy: "\n")
        var foundEmptyLine = false
        var bodyLines: [String] = []
        
        for line in lines {
            if foundEmptyLine {
                bodyLines.append(line)
            } else if line.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                foundEmptyLine = true
            }
        }
        
        if !bodyLines.isEmpty {
            let body = bodyLines.joined(separator: "\n").trimmingCharacters(in: .whitespacesAndNewlines)
            print("🔍 Line-by-line body: '\(body)'")
            if !body.isEmpty { return body }
        }
        
        // Method 4: Look for JSON pattern with more flexible regex
        let flexiblePattern = "\\{[^}]*\\}"
        if let range = request.range(of: flexiblePattern, options: .regularExpression) {
            let jsonString = String(request[range])
            if jsonString.contains("provider") || jsonString.contains("access_token") {
                print("🔍 Flexible regex JSON: \(jsonString)")
                return jsonString
            }
        }
        
        print("🔍 No JSON found with any method")
        return nil
    }
}