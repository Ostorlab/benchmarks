import SwiftUI

struct ContentView: View {
    @StateObject private var authService = AuthenticationService()
    @StateObject private var documentManager = DocumentManager()
    
    var body: some View {
        Group {
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

#Preview {
    ContentView()
}
