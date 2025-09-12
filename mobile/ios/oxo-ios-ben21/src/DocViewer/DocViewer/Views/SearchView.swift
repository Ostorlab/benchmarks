import SwiftUI

struct SearchView: View {
    @EnvironmentObject var documentManager: DocumentManager
    @State private var searchQuery = ""
    @State private var selectedSortOption = DocumentManager.SortOption.dateModified
    @State private var showingFilters = false
    
    var body: some View {
        NavigationView {
            VStack {
                // Search Bar
                HStack {
                    Image(systemName: "magnifyingglass")
                        .foregroundColor(.gray)
                    
                    TextField("Search documents, authors, tags...", text: $searchQuery)
                        .textFieldStyle(PlainTextFieldStyle())
                        .onChange(of: searchQuery) { _ in
                            documentManager.searchText = searchQuery
                        }
                    
                    if !searchQuery.isEmpty {
                        Button(action: {
                            searchQuery = ""
                            documentManager.searchText = ""
                        }) {
                            Image(systemName: "xmark.circle.fill")
                                .foregroundColor(.gray)
                        }
                    }
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(10)
                .padding(.horizontal)
                
                // Quick Filters
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 12) {
                        QuickFilterButton(title: "All", isSelected: documentManager.selectedTags.isEmpty) {
                            documentManager.selectedTags.removeAll()
                        }
                        
                        ForEach(popularTags, id: \.self) { tag in
                            QuickFilterButton(
                                title: tag.capitalized,
                                isSelected: documentManager.selectedTags.contains(tag)
                            ) {
                                if documentManager.selectedTags.contains(tag) {
                                    documentManager.selectedTags.remove(tag)
                                } else {
                                    documentManager.selectedTags.insert(tag)
                                }
                            }
                        }
                    }
                    .padding(.horizontal)
                }
                .padding(.vertical, 8)
                
                // Search Results
                if searchQuery.isEmpty && documentManager.selectedTags.isEmpty {
                    // Empty State
                    VStack(spacing: 20) {
                        Spacer()
                        
                        Image(systemName: "magnifyingglass")
                            .font(.system(size: 60))
                            .foregroundColor(.gray)
                        
                        VStack(spacing: 8) {
                            Text("Search Your Documents")
                                .font(.title2)
                                .fontWeight(.semibold)
                            
                            Text("Find documents by name, author, content, or tags")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                                .multilineTextAlignment(.center)
                        }
                        
                        // Popular Searches
                        VStack(alignment: .leading, spacing: 12) {
                            Text("Popular Searches")
                                .font(.headline)
                                .padding(.top)
                            
                            LazyVGrid(columns: [
                                GridItem(.flexible()),
                                GridItem(.flexible())
                            ], spacing: 8) {
                                ForEach(popularSearches, id: \.self) { search in
                                    Button(action: {
                                        searchQuery = search
                                        documentManager.searchText = search
                                    }) {
                                        Text(search)
                                            .font(.subheadline)
                                            .foregroundColor(.blue)
                                            .padding(.horizontal, 12)
                                            .padding(.vertical, 6)
                                            .background(Color.blue.opacity(0.1))
                                            .cornerRadius(8)
                                    }
                                }
                            }
                        }
                        .padding()
                        
                        Spacer()
                    }
                } else {
                    // Search Results
                    VStack(alignment: .leading, spacing: 16) {
                        HStack {
                            Text("\(documentManager.filteredDocuments.count) result\(documentManager.filteredDocuments.count == 1 ? "" : "s")")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            
                            Spacer()
                            
                            Button(action: {
                                showingFilters = true
                            }) {
                                HStack(spacing: 4) {
                                    Image(systemName: "line.horizontal.3.decrease.circle")
                                    Text("Sort & Filter")
                                }
                                .font(.subheadline)
                                .foregroundColor(.blue)
                            }
                        }
                        .padding(.horizontal)
                        
                        if documentManager.filteredDocuments.isEmpty {
                            VStack(spacing: 16) {
                                Image(systemName: "doc.text.magnifyingglass")
                                    .font(.system(size: 50))
                                    .foregroundColor(.gray)
                                
                                Text("No documents found")
                                    .font(.headline)
                                
                                Text("Try adjusting your search terms or filters")
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                                    .multilineTextAlignment(.center)
                            }
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                        } else {
                            List(documentManager.filteredDocuments, id: \.id) { document in
                                SearchResultRow(document: document, searchQuery: searchQuery)
                                    .listRowInsets(EdgeInsets(top: 8, leading: 16, bottom: 8, trailing: 16))
                            }
                            .listStyle(PlainListStyle())
                        }
                    }
                }
            }
            .navigationTitle("Search")
            .navigationBarItems(
                trailing: Button(action: {
                    showingFilters = true
                }) {
                    Image(systemName: "slider.horizontal.3")
                }
            )
        }
        .sheet(isPresented: $showingFilters) {
            FilterView()
                .environmentObject(documentManager)
        }
    }
    
    private var popularTags: [String] {
        Array(Set(documentManager.documents.flatMap { $0.tags }))
            .sorted()
            .prefix(6)
            .map { $0 }
    }
    
    private var popularSearches: [String] {
        ["financial", "reports", "presentations", "projects", "meeting", "analytics"]
    }
}

struct QuickFilterButton: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.subheadline)
                .fontWeight(isSelected ? .semibold : .regular)
                .foregroundColor(isSelected ? .white : .blue)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(isSelected ? Color.blue : Color.blue.opacity(0.1))
                .cornerRadius(20)
        }
    }
}

struct SearchResultRow: View {
    let document: Document
    let searchQuery: String
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: document.iconName)
                .font(.title2)
                .foregroundColor(document.iconColor)
                .frame(width: 40, height: 40)
                .background(document.iconColor.opacity(0.1))
                .cornerRadius(8)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(highlightedText(document.name, query: searchQuery))
                    .font(.headline)
                    .lineLimit(2)
                
                HStack {
                    Text("by \(document.author)")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    
                    Spacer()
                    
                    Text(document.formattedSize)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                
                HStack {
                    Text(document.formattedDate)
                        .font(.caption)
                        .foregroundColor(.secondary)
                    
                    Spacer()
                    
                    if !document.tags.isEmpty {
                        HStack(spacing: 4) {
                            ForEach(document.tags.prefix(2), id: \.self) { tag in
                                Text(tag)
                                    .font(.caption2)
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 2)
                                    .background(searchQuery.lowercased().contains(tag.lowercased()) ? Color.yellow.opacity(0.3) : Color.blue.opacity(0.1))
                                    .foregroundColor(.blue)
                                    .cornerRadius(4)
                            }
                        }
                    }
                }
                
                // Match reasons
                if !matchReasons(for: document).isEmpty {
                    HStack(spacing: 8) {
                        ForEach(matchReasons(for: document), id: \.self) { reason in
                            Text(reason)
                                .font(.caption2)
                                .foregroundColor(.orange)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(Color.orange.opacity(0.1))
                                .cornerRadius(4)
                        }
                    }
                }
            }
            
            VStack {
                if document.isFavorite {
                    Image(systemName: "star.fill")
                        .foregroundColor(.yellow)
                        .font(.caption)
                }
                
                if document.isShared {
                    Image(systemName: "person.2.fill")
                        .foregroundColor(.green)
                        .font(.caption)
                }
            }
        }
        .padding(.vertical, 4)
    }
    
    private func highlightedText(_ text: String, query: String) -> AttributedString {
        var attributedString = AttributedString(text)
        
        if !query.isEmpty {
            let range = text.range(of: query, options: .caseInsensitive)
            if let range = range {
                let nsRange = NSRange(range, in: text)
                if let attributedRange = Range(nsRange, in: attributedString) {
                    attributedString[attributedRange].backgroundColor = .yellow.opacity(0.3)
                }
            }
        }
        
        return attributedString
    }
    
    private func matchReasons(for document: Document) -> [String] {
        var reasons: [String] = []
        let query = searchQuery.lowercased()
        
        if document.name.lowercased().contains(query) {
            reasons.append("Name match")
        }
        
        if document.author.lowercased().contains(query) {
            reasons.append("Author match")
        }
        
        if document.tags.joined().lowercased().contains(query) {
            reasons.append("Tag match")
        }
        
        return reasons
    }
}
