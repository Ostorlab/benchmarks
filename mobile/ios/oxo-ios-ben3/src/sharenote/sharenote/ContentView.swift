// File: ContentView.swift
import SwiftUI

struct ContentView: View {
    @EnvironmentObject var store: NoteStore
    @State private var isShowingNewNoteSheet = false // Controls the presentation of the add sheet

    var body: some View {
        NavigationView {
            List {
                ForEach(store.notes) { note in
                    NavigationLink {
                        NoteDetailView(note: note)
                    } label: {
                        VStack(alignment: .leading) {
                            Text(note.title)
                                .font(.headline)
                                .lineLimit(1)
                            Text("Created: \(note.date.formatted(date: .abbreviated, time: .shortened))")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                .onDelete(perform: store.deleteNote) // Enable swipe-to-delete
            }
            .navigationTitle("ShareNote")
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    EditButton() // Edit button to enable deletion
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { isShowingNewNoteSheet = true }) {
                        Image(systemName: "plus")
                    }
                }
            }
            .sheet(isPresented: $isShowingNewNoteSheet) {
                // This sheet will present the note creation view
                NewNoteView(isPresented: $isShowingNewNoteSheet)
            }
            .overlay {
                if store.notes.isEmpty {
                    Text("No notes yet.\nTap the '+' to create one or share an .html file to this app.")
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                }
            }
        }
    }
}

#Preview {
    ContentView()
        .environmentObject(NoteStore())
}
