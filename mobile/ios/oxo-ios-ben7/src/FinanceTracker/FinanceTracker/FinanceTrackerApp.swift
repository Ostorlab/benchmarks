import SwiftUI

@main
struct FinanceTrackerApp: App {
    @StateObject private var dataManager = DataManager()
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(dataManager)
                .onOpenURL { url in
                    // VULNERABILITY: Automatic execution of sensitive actions via deeplinks
                    handleDeeplink(url: url)
                }
        }
    }
    
    // VULNERABLE: Deeplink handler that automatically performs money transfers
    private func handleDeeplink(url: URL) {
        print("🔗 Processing deeplink: \(url.absoluteString)")
        
        guard url.scheme == "financetracker" else {
            print("❌ Invalid scheme: \(url.scheme ?? "nil")")
            return
        }
        
        let host = url.host ?? ""
        let queryItems = URLComponents(url: url, resolvingAgainstBaseURL: false)?.queryItems ?? []
        
        print("📍 Host: \(host)")
        print("📍 Query: \(queryItems)")
        
        switch host {
        case "transfer":
            // CRITICAL VULNERABILITY: Automatic money transfer without user confirmation
            handleTransferDeeplink(queryItems: queryItems)
            
        default:
            print("⚠️ Unknown deeplink host: \(host)")
        }
    }
    
    // VULNERABLE: Automatic money transfer execution
    private func handleTransferDeeplink(queryItems: [URLQueryItem]) {
        print("💰 Processing transfer deeplink...")
        
        var fromAccount: String?
        var toAccount: String?
        var amount: Double = 0
        var reason: String = "Deeplink Transfer"
        
        for item in queryItems {
            switch item.name {
            case "from":
                fromAccount = item.value
            case "to":
                toAccount = item.value
            case "amount":
                amount = Double(item.value ?? "0") ?? 0
            case "reason":
                reason = item.value ?? "Deeplink Transfer"
            default:
                break
            }
        }
        
        // CRITICAL: No user confirmation or authentication required
        if let from = fromAccount, let to = toAccount, amount > 0 {
            print("🚨 EXECUTING AUTOMATIC TRANSFER:")
            print("   From: \(from)")
            print("   To: \(to)")
            print("   Amount: $\(amount)")
            print("   Reason: \(reason)")
            
            // Execute transfer automatically without user consent
            executeAutomaticTransfer(from: from, to: to, amount: amount, reason: reason)
        } else {
            print("❌ Invalid transfer parameters")
        }
    }
    
    // DANGEROUS: Execute transfer without user confirmation
    private func executeAutomaticTransfer(from: String, to: String, amount: Double, reason: String) {
        // Find accounts
        guard let fromAccount = dataManager.accounts.first(where: { $0.name.lowercased() == from.lowercased() }),
              let toAccount = dataManager.accounts.first(where: { $0.name.lowercased() == to.lowercased() }) else {
            print("❌ Account not found")
            return
        }
        
        // Execute transfer without any validation or user consent
        let transfer = Transfer(
            fromAccount: fromAccount.name,
            toAccount: toAccount.name,
            amount: amount,
            description: reason,
            date: Date()
        )
        
        dataManager.addTransfer(transfer)
        print("✅ TRANSFER EXECUTED AUTOMATICALLY!")
        print("   $\(amount) transferred from \(fromAccount.name) to \(toAccount.name)")
    }
}
