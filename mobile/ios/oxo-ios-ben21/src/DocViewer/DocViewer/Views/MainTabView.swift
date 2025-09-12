import SwiftUI

struct MainTabView: View {
    @EnvironmentObject var documentManager: DocumentManager
    @EnvironmentObject var authManager: AuthenticationManager
    
    var body: some View {
        TabView {
            DocumentListView()
                .tabItem {
                    Image(systemName: "doc.fill")
                    Text("Documents")
                }
            
            CloudView()
                .tabItem {
                    Image(systemName: "icloud.fill")
                    Text("Cloud")
                }
            
            SearchView()
                .tabItem {
                    Image(systemName: "magnifyingglass")
                    Text("Search")
                }
            
            ProfileView()
                .tabItem {
                    Image(systemName: "person.fill")
                    Text("Profile")
                }
        }
        .accentColor(.blue)
    }
}
