// File: NoteStore.swift
import Foundation

class NoteStore: ObservableObject {
    @Published var notes: [Note] = [] {
        didSet {
            saveNotes()
        }
    }

    init() {
        loadNotes()
    }

    // Save notes to UserDefaults
    private func saveNotes() {
        if let encoded = try? JSONEncoder().encode(notes) {
            UserDefaults.standard.set(encoded, forKey: "SavedNotes")
        }
    }

    // Load notes from UserDefaults
    private func loadNotes() {
        if let data = UserDefaults.standard.data(forKey: "SavedNotes"),
           let decoded = try? JSONDecoder().decode([Note].self, from: data) {
            notes = decoded
        }
    }

    // Function to add a new note
    func addNote(title: String, content: String) {
        let newNote = Note(title: title, content: content)
        notes.insert(newNote, at: 0) // Add new notes at the top of the list
    }

    // Function to delete a note
    func deleteNote(at offsets: IndexSet) {
        notes.remove(atOffsets: offsets)
    }
}
