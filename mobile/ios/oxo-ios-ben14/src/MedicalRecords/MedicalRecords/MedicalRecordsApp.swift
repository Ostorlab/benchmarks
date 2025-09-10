import SwiftUI

@main
struct MedicalRecordsApp: App {
    @StateObject private var sessionManager = SessionManager()
    
    var body: some Scene {
        WindowGroup {
            if sessionManager.isLoggedIn {
                ContentView()
                    .environmentObject(sessionManager)
            } else {
                LoginView()
                    .environmentObject(sessionManager)
            }
        }
    }
}
