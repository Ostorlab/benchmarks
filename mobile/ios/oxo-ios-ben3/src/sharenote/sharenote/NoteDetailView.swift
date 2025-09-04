// File: NoteDetailView.swift
import SwiftUI
import WebKit

struct WebView: UIViewRepresentable {
    let htmlString: String
    func makeUIView(context: Context) -> WKWebView { return WKWebView() }
    func updateUIView(_ uiView: WKWebView, context: Context) {
        // VULNERABILITY: Directly loading unsanitized HTML
        let header = "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
        let fullHTML = header + htmlString
        uiView.loadHTMLString(fullHTML, baseURL: nil)
    }
}

struct NoteDetailView: View {
    let note: Note
    var body: some View {
        WebView(htmlString: note.content)
            .navigationTitle(note.title)
            .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    NavigationView {
        NoteDetailView(note: Note(title: "Test", content: "<h1>Test</h1>"))
    }
}
