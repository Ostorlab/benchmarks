import SwiftUI

struct MedicalRecordView: View {
    @EnvironmentObject var sessionManager: SessionManager
    @State private var selectedRecord: MedicalRecord?
    
    private let sampleRecords = [
        MedicalRecord(
            patientId: "PT-1234",
            date: Date().addingTimeInterval(-86400 * 7),
            diagnosis: "Hypertension Stage 1",
            treatment: "Lifestyle modifications, ACE inhibitor prescribed",
            doctorName: "Dr. Sarah Johnson",
            notes: "Patient shows good response to treatment. Blood pressure readings improved."
        ),
        MedicalRecord(
            patientId: "PT-1234",
            date: Date().addingTimeInterval(-86400 * 30),
            diagnosis: "Type 2 Diabetes Mellitus",
            treatment: "Metformin 500mg BID, dietary counseling",
            doctorName: "Dr. Michael Chen",
            notes: "HbA1c levels within target range. Continue current regimen."
        ),
        MedicalRecord(
            patientId: "PT-1234",
            date: Date().addingTimeInterval(-86400 * 60),
            diagnosis: "Annual Physical Examination",
            treatment: "Routine screening tests ordered",
            doctorName: "Dr. Sarah Johnson",
            notes: "Overall health status good. Patient advised on preventive care measures."
        )
    ]
    
    var body: some View {
        NavigationView {
            List {
                ForEach(sampleRecords) { record in
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Text(record.diagnosis)
                                .font(.headline)
                            Spacer()
                            Text(record.date, style: .date)
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        
                        Text("Dr. \(record.doctorName)")
                            .font(.subheadline)
                            .foregroundColor(.blue)
                        
                        Text(record.treatment)
                            .font(.body)
                            .foregroundColor(.primary)
                        
                        if !record.notes.isEmpty {
                            Text(record.notes)
                                .font(.caption)
                                .foregroundColor(.secondary)
                                .padding(.top, 4)
                        }
                    }
                    .padding(.vertical, 8)
                    .onTapGesture {
                        selectedRecord = record
                    }
                }
            }
            .navigationTitle("Medical Records")
            .sheet(item: $selectedRecord) { record in
                RecordDetailView(record: record)
            }
        }
    }
}

struct RecordDetailView: View {
    let record: MedicalRecord
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Diagnosis")
                            .font(.headline)
                        Text(record.diagnosis)
                            .font(.body)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Treatment")
                            .font(.headline)
                        Text(record.treatment)
                            .font(.body)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Doctor")
                            .font(.headline)
                        Text(record.doctorName)
                            .font(.body)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Date")
                            .font(.headline)
                        Text(record.date, style: .date)
                            .font(.body)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Notes")
                            .font(.headline)
                        Text(record.notes)
                            .font(.body)
                    }
                    
                    Spacer()
                }
                .padding()
            }
            .navigationTitle("Record Details")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        dismiss()
                    }
                }
            }
        }
    }
}

#Preview {
    MedicalRecordView()
        .environmentObject(SessionManager())
}
