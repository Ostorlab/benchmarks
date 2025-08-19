package com.purpleapps.purplenotes

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var addNoteButton: FloatingActionButton
    private val notes = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.notesRecyclerView)
        addNoteButton = findViewById(R.id.addNoteButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        notesAdapter = NotesAdapter(notes)
        recyclerView.adapter = notesAdapter

        addNoteButton.setOnClickListener {
            showAddNoteDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        val notesDir = File(filesDir.parent, "notes")
        if (!notesDir.exists()) {
            notesDir.mkdirs()
        }

        val loadedNotes = notesDir.listFiles()?.sorted()?.map { it.readText() } ?: emptyList()
        notes.clear()
        notes.addAll(loadedNotes)
        notesAdapter.notifyDataSetChanged()
    }

    private fun showAddNoteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val noteTitleEditText = dialogView.findViewById<TextInputEditText>(R.id.noteTitleEditText)
        val noteInputEditText = dialogView.findViewById<TextInputEditText>(R.id.noteInputEditText)

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("New Note")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val noteTitle = noteTitleEditText.text.toString()
                val noteContent = noteInputEditText.text.toString()
                if (noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                    saveNote(noteTitle, noteContent)
                } else {
                    Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveNote(noteTitle: String, noteContent: String) {
        try {
            val notesDir = File(filesDir.parent, "notes")
            // VULNERABILITY: The filename is taken from user input and is not sanitized.
            // This could lead to a path traversal vulnerability.
            val noteFile = File(notesDir, noteTitle)
            Log.i("MainActivity", "Saving note to: ${noteFile.absolutePath}")
            noteFile.writeText(noteContent)
            loadNotes() // Reload notes to display the new one
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error saving note", e)
            Toast.makeText(this, "Error saving note", Toast.LENGTH_SHORT).show()
        }
    }
}
