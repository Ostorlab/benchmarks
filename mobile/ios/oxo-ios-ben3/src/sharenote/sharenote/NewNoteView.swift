// File: NewNoteView.swift
import SwiftUI

struct NewNoteView: View {
    @EnvironmentObject var store: NoteStore
    @Binding var isPresented: Bool

    // State for the new note's input fields
    @State private var noteTitle = ""
    @State private var noteContent = ""

    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Note Info")) {
                    TextField("Title", text: $noteTitle)
                    TextField("HTML Content", text: $noteContent, axis: .vertical)
                        .lineLimit(5...) // Allows multiline input
                }

                Section(header: Text("Preview")) {
                    Text("Note preview will be shown when opened.")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
            .navigationTitle("New Note")
            .navigationBarItems(
                leading: Button("Cancel") {
                    isPresented = false
                },
                trailing: Button("Save") {
                    saveNote()
                }
                .disabled(noteTitle.isEmpty || noteContent.isEmpty) // Don't allow saving empty notes
            )
        }
    }

    private func saveNote() {
        // VULNERABILITY: We directly save the user's input without sanitization.
        store.addNote(title: noteTitle, content: noteContent)
        isPresented = false // Dismiss the sheet
    }
}

#Preview {
    NewNoteView(isPresented: .constant(true))
        .environmentObject(NoteStore())
}
