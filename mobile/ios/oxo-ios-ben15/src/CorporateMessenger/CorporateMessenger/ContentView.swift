import SwiftUI

struct ContentView: View {
    @State private var messages: [Message] = []
    @State private var newMessage = ""
    @StateObject private var firebaseService = FirebaseService()
    
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
                            firebaseService.sendMessage(text: newMessage, sender: "You")
                            newMessage = ""
                        }
                    }
                    .buttonStyle(.borderedProminent)
                }
                .padding()
            }
            .navigationTitle("Corporate Chat")
            .onAppear {
                firebaseService.fetchMessages { fetchedMessages in
                    self.messages = fetchedMessages
                }
            }
            .onDisappear {
                firebaseService.removeMessageListener()
            }
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
