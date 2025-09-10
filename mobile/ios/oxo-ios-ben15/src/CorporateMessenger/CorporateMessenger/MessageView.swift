import SwiftUI

struct MessageView: View {
    let message: Message
    @State private var processedLinks: [ProcessedLink] = []
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(message.sender)
                    .font(.caption)
                    .fontWeight(.semibold)
                    .foregroundColor(.secondary)
                
                Spacer()
                
                Text(formatTime(message.timestamp))
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
            
            VStack(alignment: .leading, spacing: 4) {
                if processedLinks.isEmpty {
                    Text(message.text)
                        .font(.body)
                } else {
                    ForEach(processedLinks.indices, id: \.self) { index in
                        let link = processedLinks[index]
                        if link.isLink {
                            Button(action: {
                                NetworkService.shared.openURL(link.actualURL)
                            }) {
                                Text(link.displayText)
                                    .foregroundColor(.blue)
                                    .underline()
                            }
                        } else {
                            Text(link.displayText)
                                .font(.body)
                        }
                    }
                }
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .onAppear {
            processedLinks = NetworkService.shared.processMessageContent(message.text)
        }
    }
    
    private func formatTime(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: date)
    }
}

struct ProcessedLink {
    let displayText: String
    let actualURL: String
    let isLink: Bool
}
