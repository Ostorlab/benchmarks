import Foundation

// NOTE: This implementation intentionally uses a publicly exposed Firebase database
// to make the messaging app more realistic. This can be detected as a security vulnerability
class FirebaseService: ObservableObject {
    private let databaseURL = "https://corporate-messenger-ios-ben15-default-rtdb.firebaseio.com"
    private var timer: Timer?
    
    func fetchMessages(completion: @escaping ([Message]) -> Void) {
        guard let url = URL(string: "\(databaseURL)/messages.json") else { return }
        
        URLSession.shared.dataTask(with: url) { data, response, error in
            guard let data = data, error == nil else {
                DispatchQueue.main.async {
                    completion([])
                }
                return
            }
            
            do {
                if let json = try JSONSerialization.jsonObject(with: data) as? [String: [String: Any]] {
                    var messages: [Message] = []
                    
                    for (_, messageData) in json {
                        if let text = messageData["text"] as? String,
                           let sender = messageData["sender"] as? String,
                           let timestampDouble = messageData["timestamp"] as? Double {
                            
                            let timestamp = Date(timeIntervalSince1970: timestampDouble / 1000)
                            let message = Message(text: text, sender: sender, timestamp: timestamp)
                            messages.append(message)
                        }
                    }
                    
                    messages.sort { $0.timestamp < $1.timestamp }
                    
                    DispatchQueue.main.async {
                        completion(messages)
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    completion([])
                }
            }
        }.resume()
        
        // Set up periodic refresh
        timer = Timer.scheduledTimer(withTimeInterval: 2.0, repeats: true) { _ in
            self.fetchMessages(completion: completion)
        }
    }
    
    func sendMessage(text: String, sender: String) {
        guard let url = URL(string: "\(databaseURL)/messages.json") else { return }
        
        let timestamp = Date().timeIntervalSince1970 * 1000
        let messageData: [String: Any] = [
            "text": text,
            "sender": sender,
            "timestamp": timestamp,
            "id": UUID().uuidString
        ]
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: messageData)
            
            URLSession.shared.dataTask(with: request) { data, response, error in
                if let error = error {
                    print("Error sending message: \(error.localizedDescription)")
                }
            }.resume()
        } catch {
            print("Error serializing message: \(error.localizedDescription)")
        }
    }
    
    func removeMessageListener() {
        timer?.invalidate()
        timer = nil
    }
}
