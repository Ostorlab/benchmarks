import Foundation

struct PatientData: Identifiable, Codable {
    let id: String
    let name: String
    let dateOfBirth: String
    let bloodType: String
    let allergies: [String]
    let conditions: [String]
}

struct MedicalRecord: Identifiable, Codable {
    let id = UUID()
    let patientId: String
    let date: Date
    let diagnosis: String
    let treatment: String
    let doctorName: String
    let notes: String
}

struct Appointment: Identifiable, Codable {
    let id = UUID()
    let patientId: String
    let doctorName: String
    let specialty: String
    let date: Date
    let reason: String
    let status: AppointmentStatus
}

enum AppointmentStatus: String, Codable, CaseIterable {
    case scheduled = "Scheduled"
    case completed = "Completed"
    case cancelled = "Cancelled"
}

struct Prescription: Identifiable, Codable {
    let id = UUID()
    let patientId: String
    let medicationName: String
    let dosage: String
    let frequency: String
    let prescribedBy: String
    let datePrescibed: Date
    let refillsRemaining: Int
}

struct LabResult: Identifiable, Codable {
    let id = UUID()
    let patientId: String
    let testName: String
    let result: String
    let normalRange: String
    let date: Date
    let orderedBy: String
}
