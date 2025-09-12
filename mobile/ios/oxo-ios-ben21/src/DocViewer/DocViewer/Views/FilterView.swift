import SwiftUI

struct FilterView: View {
    @EnvironmentObject var documentManager: DocumentManager
    @Environment(\.presentationMode) var presentationMode
    @State private var tempSelectedTags: Set<String> = []
    @State private var tempSortOption: DocumentManager.SortOption = .dateModified
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 24) {
                    // Sort Options
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Sort By")
                            .font(.headline)
                        
                        VStack(spacing: 8) {
                            ForEach(DocumentManager.SortOption.allCases, id: \.self) { option in
                                Button(action: {
                                    tempSortOption = option
                                }) {
                                    HStack {
                                        Image(systemName: tempSortOption == option ? "largecircle.fill.circle" : "circle")
                                            .foregroundColor(tempSortOption == option ? .blue : .gray)
                                        
                                        Text(option.rawValue)
                                            .font(.subheadline)
                                            .foregroundColor(.primary)
                                        
                                        Spacer()
                                    }
                                    .padding(.vertical, 4)
                                }
                                .buttonStyle(PlainButtonStyle())
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Document Type Filter
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Document Types")
                            .font(.headline)
                        
                        LazyVGrid(columns: [
                            GridItem(.flexible()),
                            GridItem(.flexible())
                        ], spacing: 12) {
                            ForEach(DocumentType.allCases, id: \.self) { type in
                                DocumentTypeFilterButton(
                                    type: type,
                                    isSelected: documentsByType[type]?.count ?? 0 > 0
                                ) {
                                    // Handle document type filtering
                                }
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Tags Filter
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Tags")
                            .font(.headline)
                        
                        if documentManager.allTags.isEmpty {
                            Text("No tags available")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        } else {
                            LazyVGrid(columns: [
                                GridItem(.adaptive(minimum: 100))
                            ], spacing: 8) {
                                ForEach(documentManager.allTags, id: \.self) { tag in
                                    TagFilterButton(
                                        tag: tag,
                                        isSelected: tempSelectedTags.contains(tag)
                                    ) {
                                        if tempSelectedTags.contains(tag) {
                                            tempSelectedTags.remove(tag)
                                        } else {
                                            tempSelectedTags.insert(tag)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Date Range Filter
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Date Range")
                            .font(.headline)
                        
                        VStack(spacing: 12) {
                            DateRangeButton(title: "Today", isSelected: false) {
                                // Filter by today
                            }
                            
                            DateRangeButton(title: "This Week", isSelected: false) {
                                // Filter by this week
                            }
                            
                            DateRangeButton(title: "This Month", isSelected: false) {
                                // Filter by this month
                            }
                            
                            DateRangeButton(title: "This Year", isSelected: false) {
                                // Filter by this year
                            }
                        }
                    }
                    
                    Divider()
                    
                    // Author Filter
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Authors")
                            .font(.headline)
                        
                        LazyVGrid(columns: [
                            GridItem(.flexible()),
                            GridItem(.flexible())
                        ], spacing: 8) {
                            ForEach(uniqueAuthors, id: \.self) { author in
                                AuthorFilterButton(
                                    author: author,
                                    documentCount: documentsByAuthor[author]?.count ?? 0,
                                    isSelected: false
                                ) {
                                    // Filter by author
                                }
                            }
                        }
                    }
                    
                    Spacer(minLength: 100)
                }
                .padding()
            }
            .navigationTitle("Filter & Sort")
            .navigationBarItems(
                leading: Button("Reset") {
                    resetFilters()
                },
                trailing: Button("Apply") {
                    applyFilters()
                }
            )
        }
        .onAppear {
            tempSelectedTags = documentManager.selectedTags
            tempSortOption = documentManager.sortOption
        }
    }
    
    private var documentsByType: [DocumentType: [Document]] {
        Dictionary(grouping: documentManager.documents) { $0.type }
    }
    
    private var documentsByAuthor: [String: [Document]] {
        Dictionary(grouping: documentManager.documents) { $0.author }
    }
    
    private var uniqueAuthors: [String] {
        Array(Set(documentManager.documents.map { $0.author })).sorted()
    }
    
    private func resetFilters() {
        tempSelectedTags.removeAll()
        tempSortOption = .dateModified
        documentManager.selectedTags.removeAll()
        documentManager.sortOption = .dateModified
        documentManager.searchText = ""
    }
    
    private func applyFilters() {
        documentManager.selectedTags = tempSelectedTags
        documentManager.sortOption = tempSortOption
        presentationMode.wrappedValue.dismiss()
    }
}

struct DocumentTypeFilterButton: View {
    let type: DocumentType
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 8) {
                Image(systemName: iconName(for: type))
                    .foregroundColor(color(for: type))
                
                VStack(alignment: .leading, spacing: 2) {
                    Text(type.displayName)
                        .font(.caption)
                        .fontWeight(.medium)
                        .lineLimit(1)
                    
                    Text(type.rawValue.uppercased())
                        .font(.caption2)
                        .foregroundColor(.secondary)
                }
                
                Spacer()
            }
            .padding(8)
            .background(isSelected ? color(for: type).opacity(0.2) : Color(.systemGray6))
            .cornerRadius(8)
        }
        .buttonStyle(PlainButtonStyle())
    }
    
    private func iconName(for type: DocumentType) -> String {
        switch type {
        case .pdf: return "doc.richtext"
        case .word: return "doc.text"
        case .excel: return "tablecells"
        case .powerpoint: return "play.rectangle"
        case .image: return "photo"
        case .text: return "doc.plaintext"
        case .html: return "globe"
        }
    }
    
    private func color(for type: DocumentType) -> Color {
        switch type {
        case .pdf: return .red
        case .word: return .blue
        case .excel: return .green
        case .powerpoint: return .orange
        case .image: return .purple
        case .text: return .gray
        case .html: return .indigo
        }
    }
}

struct TagFilterButton: View {
    let tag: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(tag.capitalized)
                .font(.caption)
                .fontWeight(isSelected ? .semibold : .regular)
                .foregroundColor(isSelected ? .white : .blue)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(isSelected ? Color.blue : Color.blue.opacity(0.1))
                .cornerRadius(16)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

struct DateRangeButton: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack {
                Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                    .foregroundColor(isSelected ? .blue : .gray)
                
                Text(title)
                    .font(.subheadline)
                    .foregroundColor(.primary)
                
                Spacer()
            }
            .padding(.vertical, 4)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

struct AuthorFilterButton: View {
    let author: String
    let documentCount: Int
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Circle()
                        .fill(Color.blue.gradient)
                        .frame(width: 24, height: 24)
                        .overlay(
                            Text(String(author.prefix(1).uppercased()))
                                .font(.caption2)
                                .fontWeight(.bold)
                                .foregroundColor(.white)
                        )
                    
                    Spacer()
                    
                    if isSelected {
                        Image(systemName: "checkmark.circle.fill")
                            .foregroundColor(.blue)
                            .font(.caption)
                    }
                }
                
                Text(author)
                    .font(.caption)
                    .fontWeight(.medium)
                    .lineLimit(1)
                
                Text("\(documentCount) doc\(documentCount == 1 ? "" : "s")")
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
            .padding(8)
            .background(isSelected ? Color.blue.opacity(0.1) : Color(.systemGray6))
            .cornerRadius(8)
        }
        .buttonStyle(PlainButtonStyle())
    }
}
