import SwiftUI

struct MainTabView: View {
    @EnvironmentObject var authService: AuthenticationService
    @EnvironmentObject var documentManager: DocumentManager
    
    var body: some View {
        TabView {
            DocumentListView()
                .tabItem {
                    Image(systemName: "doc.text")
                    Text("Documents")
                }
            
            SignatureVerificationView()
                .tabItem {
                    Image(systemName: "checkmark.seal")
                    Text("Verify")
                }
            
            BackupView()
                .tabItem {
                    Image(systemName: "externaldrive")
                    Text("Backup")
                }
            
            SettingsView()
                .tabItem {
                    Image(systemName: "gear")
                    Text("Settings")
                }
        }
        .accentColor(.blue)
    }
}
