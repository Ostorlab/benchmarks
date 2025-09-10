import Foundation
import UIKit

class SessionManager: ObservableObject {
    @Published var isLoggedIn = false
    @Published var currentUser: PatientData?
    
    private let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
    
    init() {
        loadSession()
    }
    
    func login(username: String, password: String, pin: String) -> Bool {
        if username == "patient123" && password == "medical2024" {
            let authToken = generateToken()
            let userId = "PT-\(Int.random(in: 1000...9999))"
            let doctorId = "DR-\(Int.random(in: 100...999))"
            
            saveSession(token: authToken, userId: userId, doctorId: doctorId)
            saveCredentials(username: username, password: password, pin: pin)
            
            currentUser = PatientData(
                id: userId,
                name: "John Smith",
                dateOfBirth: "1985-03-15",
                bloodType: "A+",
                allergies: ["Penicillin", "Shellfish"],
                conditions: ["Hypertension", "Diabetes Type 2"]
            )
            
            cacheMedicalRecords()
            isLoggedIn = true
            return true
        }
        return false
    }
    
    func logout() {
        isLoggedIn = false
        currentUser = nil
    }
    
    private func generateToken() -> String {
        return "MED_TOKEN_\(UUID().uuidString.prefix(16))"
    }
    
    private func saveSession(token: String, userId: String, doctorId: String) {
        let sessionFile = documentsPath.appendingPathComponent("user_session.plist")
        
        let sessionData: [String: Any] = [
            "auth_token": token,
            "user_id": userId,
            "doctor_id": doctorId,
            "login_timestamp": Date().timeIntervalSince1970,
            "permissions": ["read_records", "book_appointments", "access_prescriptions"],
            "device_id": UIDevice.current.identifierForVendor?.uuidString ?? "unknown",
            "last_access": Date().description
        ]
        
        try? (sessionData as NSDictionary).write(to: sessionFile)
    }
    
    private func saveCredentials(username: String, password: String, pin: String) {
        UserDefaults.standard.set(username, forKey: "saved_username")
        UserDefaults.standard.set(password, forKey: "saved_password")
        UserDefaults.standard.set(pin, forKey: "medical_pin")
        UserDefaults.standard.set(true, forKey: "remember_login")
        UserDefaults.standard.set(Date().timeIntervalSince1970, forKey: "last_login")
    }
    
    private func cacheMedicalRecords() {
        let cacheDir = documentsPath.appendingPathComponent("medical_cache")
        try? FileManager.default.createDirectory(at: cacheDir, withIntermediateDirectories: true)
        
        let patientId = currentUser?.id ?? "unknown"
        let recordFile = cacheDir.appendingPathComponent("\(patientId)_record.plist")
        
        let medicalData: [String: Any] = [
            "patient_id": patientId,
            "patient_name": currentUser?.name ?? "",
            "ssn": "123-45-6789",
            "insurance_id": "INS-789456123",
            "diagnosis": "Chronic hypertension with complications",
            "prescription": "Lisinopril 10mg daily, Metformin 500mg twice daily",
            "doctor_notes": "Patient shows good compliance with medication. Blood pressure well controlled.",
            "lab_results": [
                "glucose": "126 mg/dL",
                "bp_systolic": "125",
                "bp_diastolic": "82",
                "cholesterol": "195 mg/dL"
            ],
            "last_visit": Date().description,
            "next_appointment": "2025-10-15 14:30:00",
            "emergency_contact": "Jane Smith - 555-0123"
        ]
        
        try? (medicalData as NSDictionary).write(to: recordFile)
        
        let prescriptionFile = cacheDir.appendingPathComponent("\(patientId)_prescriptions.plist")
        let prescriptionData: [String: Any] = [
            "active_medications": [
                ["name": "Lisinopril", "dosage": "10mg", "frequency": "Daily", "prescriber": "Dr. Johnson"],
                ["name": "Metformin", "dosage": "500mg", "frequency": "Twice daily", "prescriber": "Dr. Johnson"]
            ],
            "pharmacy": "CVS Pharmacy - Main Street",
            "insurance_copay": "$15.00"
        ]
        
        try? (prescriptionData as NSDictionary).write(to: prescriptionFile)
    }
    
    private func loadSession() {
        let sessionFile = documentsPath.appendingPathComponent("user_session.plist")
        if FileManager.default.fileExists(atPath: sessionFile.path),
           let sessionData = NSDictionary(contentsOf: sessionFile) {
            if let userId = sessionData["user_id"] as? String {
                currentUser = PatientData(
                    id: userId,
                    name: "John Smith",
                    dateOfBirth: "1985-03-15",
                    bloodType: "A+",
                    allergies: ["Penicillin", "Shellfish"],
                    conditions: ["Hypertension", "Diabetes Type 2"]
                )
                isLoggedIn = true
            }
        }
    }
}
