import SwiftUI

@main
struct FinanceTrackerApp: App {
    @StateObject private var dataManager = DataManager()
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(dataManager)
                .onOpenURL { url in
                    // Handle deeplink URLs
                    handleDeeplink(url: url)
                }
        }
    }
    
    // Handle deeplink URLs for the app
    private func handleDeeplink(url: URL) {
        print("Processing URL: \(url.absoluteString)")
        
        guard url.scheme == "financetracker" else {
            print("Invalid URL scheme")
            return
        }
        
        let host = url.host ?? ""
        let queryItems = URLComponents(url: url, resolvingAgainstBaseURL: false)?.queryItems ?? []
        
        // Handle transfer requests
        if host == "transfer" {
            handleTransferRequest(queryItems: queryItems)
        }
    }
    
    // Process transfer requests from deeplinks
    private func handleTransferRequest(queryItems: [URLQueryItem]) {
        var amount: Double = 0
        var recipient: String = ""
        var description: String = ""
        
        for item in queryItems {
            switch item.name {
            case "amount":
                amount = Double(item.value ?? "0") ?? 0
            case "recipient":
                recipient = item.value ?? ""
            case "description":
                description = item.value ?? ""
            default:
                break
            }
        }

        if amount > 0 && !recipient.isEmpty {
            dataManager.executeTransfer(from: "Main Checking", to: recipient, amount: amount)
        }
    }
}
