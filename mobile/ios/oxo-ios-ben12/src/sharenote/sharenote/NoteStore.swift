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
        createSecretFile() // Create a secret file for SSRF demonstration
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
    
    // VULNERABILITY SETUP: Create a secret file that can be accessed via SSRF
    private func createSecretFile() {
        guard let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first else {
            return
        }
        
        let secretFileURL = documentsDirectory.appendingPathComponent("secret_credentials.txt")
        
        // Create secret content that would be sensitive in a real app
        let secretContent = """
        === CONFIDENTIAL USER CREDENTIALS ===
        Username: admin@company.com
        Password: SuperSecret123!
        API Key: sk-1234567890abcdef
        Session Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
        Database URL: mysql://user:pass@internal-db:3306/userdata
        
        === INTERNAL SYSTEM INFO ===
        App Version: 2.1.0
        Device ID: A1B2C3D4-E5F6-7890-ABCD-EF1234567890
        Installation Date: 2024-01-15
        Last Backup: 2024-03-10
        
        === SENSITIVE NOTES ===
        - User has access to financial data
        - Credit card ending in 4567 stored locally
        - Biometric data cached in keychain
        
        This file should NOT be accessible via web content!
        """
        
        // Write the secret file
        do {
            try secretContent.write(to: secretFileURL, atomically: true, encoding: .utf8)
            print("Secret file created at: \(secretFileURL.path)")
        } catch {
            print("Failed to create secret file: \(error)")
        }
        
        // Also create a config file with app paths for demonstration
        let configFileURL = documentsDirectory.appendingPathComponent("app_config.json")
        let configContent = """
        {
            "app_name": "ShareNote",
            "version": "1.0.0",
            "documents_path": "\(documentsDirectory.path)",
            "secret_file_path": "\(secretFileURL.path)",
            "vulnerability": "SSRF via file:// protocol in WebView",
            "impact": "Local file disclosure",
            "cve_reference": "Similar to HackerOne #746541"
        }
        """
        
        do {
            try configContent.write(to: configFileURL, atomically: true, encoding: .utf8)
            print("Config file created at: \(configFileURL.path)")
        } catch {
            print("Failed to create config file: \(error)")
        }
    }
    
    // Helper function to get the documents directory path for SSRF payloads
    func getDocumentsPath() -> String {
        guard let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first else {
            return "Unknown"
        }
        return documentsDirectory.path
    }
}
