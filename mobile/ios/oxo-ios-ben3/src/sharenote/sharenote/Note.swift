// File: Note.swift
import Foundation

struct Note: Identifiable, Codable {
    var id = UUID()
    var title: String
    var content: String
    var date: Date

    init(title: String, content: String, date: Date = Date()) {
        self.title = title
        self.content = content
        self.date = date
    }
}
