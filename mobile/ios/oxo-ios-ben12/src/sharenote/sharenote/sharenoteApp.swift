// File: ShareNoteApp.swift
import SwiftUI

@main
struct ShareNoteApp: App {
    @StateObject private var store = NoteStore()

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(store)
                .onOpenURL { url in
                    handleIncomingFile(url: url)
                }
        }
    }

    func handleIncomingFile(url: URL) {
        guard url.pathExtension == "html" else { return }
        do {
            let htmlContent = try String(contentsOf: url, encoding: .utf8)
            let newNote = Note(
                title: url.deletingPathExtension().lastPathComponent,
                content: htmlContent // VULNERABILITY: Storing raw HTML
            )
            // Add to the store, which will automatically save it
            store.notes.insert(newNote, at: 0)
            print("Imported note from: \(url.lastPathComponent)")

        } catch {
            print("Failed to import file: \(error)")
        }
    }
}

