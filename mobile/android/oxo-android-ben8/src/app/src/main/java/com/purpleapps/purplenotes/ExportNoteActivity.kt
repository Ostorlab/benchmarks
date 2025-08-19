package com.purpleapps.purplenotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File

/**
 * This Activity is exported and is used to export note content to other applications.
 * It is vulnerable to Path Traversal, leading to arbitrary file disclosure.
 *
 * The vulnerability exists because the note name is received from an Intent extra and used
 * to construct a file path without proper sanitization. A malicious application can send an
 * Intent with a crafted note name containing path traversal sequences (e.g., "../../files/pwned.txt")
 * to read arbitrary files from within the application's data directory.
 *
 * To exploit this, an attacker would need to create an application that sends an Intent to this
 * activity, with the "note_name" extra containing the malicious file path. This can be done
 * for example using adb:
 * adb shell am start-activity -n com.purpleapps.purplenotes/.ReceiveExternalFilesActivity \
 *   -a com.purpleapps.purplnotes.action.EXPORT_NOTE \
 *   --es note_name "../../shared_prefs/com.purpleapps.purplenotes.purplenotes_preferences.xml"
 */
class ExportNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the note name from the intent extras. This is the user-controlled input.
        val noteName = intent.getStringExtra("note_name")

        if (noteName.isNullOrEmpty()) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        try {
            // VULNERABILITY: Path Traversal
            // The `noteName` is taken directly from the intent extra and is not sanitized.
            // It is concatenated with the base 'notes' directory path.
            // No sanitization is performed on `noteName` to remove directory traversal
            // sequences like "../".
            //
            // As a result, an attacker can craft a `noteName` like "../../shared_prefs/com.ostorlab.benchmark.vulnreceiver_preferences.xml"
            // to read a file from an arbitrary location within the app's data directory.
            val notesDir = File(filesDir.parent, "notes")
            val noteFile = File(notesDir, noteName)

            Log.i("ExportNoteActivity", "Exporting note from: $noteName")

            if (noteFile.exists() && noteFile.isFile) {
                val content = noteFile.readText()
                val resultIntent = Intent().apply {
                    putExtra("file_content", content)
                }
                Log.i("ExportNoteActivity",
                    "Sending back file content of the note."
                )
                setResult(RESULT_OK, resultIntent)
            } else {
                Log.i("ExportNoteActivity", "Note does not exist: $noteName")
                setResult(RESULT_CANCELED)
            }
        } catch (e: Exception) {
            Log.e("ExportNoteActivity", "Error exporting note", e)
            setResult(RESULT_CANCELED)
        }

        finish()
    }
}