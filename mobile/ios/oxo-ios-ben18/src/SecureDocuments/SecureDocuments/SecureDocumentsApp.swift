import SwiftUI

@main
struct SecureDocumentsApp: App {
    @StateObject private var authService = AuthenticationService()
    @StateObject private var documentManager = DocumentManager()
    
    var body: some Scene {
        WindowGroup {
            if authService.isAuthenticated {
                MainTabView()
                    .environmentObject(authService)
                    .environmentObject(documentManager)
            } else {
                LoginView()
                    .environmentObject(authService)
            }
        }
    }
}
