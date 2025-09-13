import SwiftUI

struct ContentView: View {
    @State private var selectedCategory = 0
    @State private var showingWebView = false
    @State private var selectedArticleURL = ""
    
    let categories = ["Breaking", "Tech", "Sports", "Politics"]
    let articles = [
        Article(title: "Breaking: Major Technology Breakthrough Announced", 
                summary: "Scientists reveal revolutionary advancement in quantum computing that could transform the industry...",
                url: "http://localhost:8080/test_vulnerability.html"),
        Article(title: "Sports: Championship Finals This Weekend", 
                summary: "Two powerhouse teams prepare for the ultimate showdown in what promises to be an exciting match...",
                url: "http://localhost:8080/test_vulnerability.html"),
        Article(title: "Politics: New Policy Changes Take Effect", 
                summary: "Government announces comprehensive initiatives affecting healthcare and education sectors...",
                url: "http://localhost:8080/test_vulnerability.html"),
        Article(title: "Business: Market Analysis Shows Strong Growth", 
                summary: "Financial experts analyze current market conditions and predict continued economic expansion...",
                url: "http://localhost:8080/test_vulnerability.html"),
        Article(title: "Science: Climate Research Reveals New Findings", 
                summary: "International study provides fresh insights into environmental patterns and future projections...",
                url: "http://localhost:8080/test_vulnerability.html"),
        Article(title: "Entertainment: Award Season Highlights", 
                summary: "Critics and audiences celebrate outstanding performances in film and television this year...",
                url: "http://localhost:8080/test_vulnerability.html")
    ]
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                Picker("Category", selection: $selectedCategory) {
                    ForEach(0..<categories.count, id: \.self) { index in
                        Text(categories[index]).tag(index)
                    }
                }
                .pickerStyle(SegmentedPickerStyle())
                .padding()
                
                List(articles) { article in
                    VStack(alignment: .leading, spacing: 8) {
                        Text(article.title)
                            .font(.headline)
                            .foregroundColor(.primary)
                            .lineLimit(2)
                        
                        Text(article.summary)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .lineLimit(3)
                        
                        HStack {
                            Spacer()
                            Button("Read Full Article") {
                                selectedArticleURL = article.url
                                showingWebView = true
                            }
                            .font(.caption)
                            .foregroundColor(.blue)
                            .padding(.top, 4)
                        }
                    }
                    .padding(.vertical, 8)
                }
            }
            .navigationTitle("News Reader")
            .navigationBarTitleDisplayMode(.large)
            .sheet(isPresented: $showingWebView) {
                NewsWebViewContainer(url: selectedArticleURL)
            }
        }
    }
}

struct Article: Identifiable {
    let id = UUID()
    let title: String
    let summary: String
    let url: String
}

#Preview {
    ContentView()
}
