import SwiftUI

struct ContentView: View {
    @State private var messages: [Message] = [
        Message(text: "Welcome to Corporate Messenger!", sender: "System", timestamp: Date()),
        Message(text: "Check out our company portal: https://company-portal.com", sender: "HR Team", timestamp: Date()),
        Message(text: "New security guidelines: <https://malicious-site.com|https://security.company.com>", sender: "IT Security", timestamp: Date())
    ]
    @State private var newMessage = ""
    
    var body: some View {
        NavigationView {
            VStack {
                ScrollView {
                    LazyVStack(alignment: .leading, spacing: 12) {
                        ForEach(messages) { message in
                            MessageView(message: message)
                        }
                    }
                    .padding()
                }
                
                HStack {
                    TextField("Type a message...", text: $newMessage)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    
                    Button("Send") {
                        if !newMessage.isEmpty {
                            messages.append(Message(text: newMessage, sender: "You", timestamp: Date()))
                            newMessage = ""
                        }
                    }
                    .buttonStyle(.borderedProminent)
                }
                .padding()
            }
            .navigationTitle("Corporate Chat")
        }
    }
}

struct Message: Identifiable {
    let id = UUID()
    let text: String
    let sender: String
    let timestamp: Date
}

#Preview {
    ContentView()
}
