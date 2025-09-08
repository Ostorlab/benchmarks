import SwiftUI

struct SecretItem: Identifiable {
    let id = UUID()
    var title: String
    var username: String
    var password: String
    var notes: String
}

class SecretsStore: ObservableObject {
    @Published var secrets: [SecretItem] = [
        // Just one example item to guide users
        SecretItem(title: "Example Email", username: "user@example.com", password: "YourStrongPassword", notes: "This is an example. Add your own secrets and delete this one.")
    ]
    
    func addSecret(_ secret: SecretItem) {
        secrets.append(secret)
    }
    
    func deleteSecret(at indexSet: IndexSet) {
        secrets.remove(atOffsets: indexSet)
    }
    
    func updateSecret(_ secret: SecretItem) {
        if let index = secrets.firstIndex(where: { $0.id == secret.id }) {
            secrets[index] = secret
        }
    }
}
