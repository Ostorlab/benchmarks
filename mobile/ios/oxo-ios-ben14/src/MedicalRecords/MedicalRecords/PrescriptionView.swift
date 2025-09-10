import SwiftUI

struct PrescriptionView: View {
    @State private var selectedPrescription: Prescription?
    
    private let samplePrescriptions = [
        Prescription(
            patientId: "PT-1234",
            medicationName: "Lisinopril",
            dosage: "10mg",
            frequency: "Once daily",
            prescribedBy: "Dr. Sarah Johnson",
            datePrescibed: Date().addingTimeInterval(-86400 * 30),
            refillsRemaining: 3
        ),
        Prescription(
            patientId: "PT-1234",
            medicationName: "Metformin",
            dosage: "500mg",
            frequency: "Twice daily with meals",
            prescribedBy: "Dr. Michael Chen",
            datePrescibed: Date().addingTimeInterval(-86400 * 45),
            refillsRemaining: 2
        ),
        Prescription(
            patientId: "PT-1234",
            medicationName: "Atorvastatin",
            dosage: "20mg",
            frequency: "Once daily at bedtime",
            prescribedBy: "Dr. Sarah Johnson",
            datePrescibed: Date().addingTimeInterval(-86400 * 60),
            refillsRemaining: 5
        )
    ]
    
    var body: some View {
        NavigationView {
            List {
                Section("Active Prescriptions") {
                    ForEach(samplePrescriptions.filter { $0.refillsRemaining > 0 }) { prescription in
                        PrescriptionRow(prescription: prescription)
                            .onTapGesture {
                                selectedPrescription = prescription
                            }
                    }
                }
                
                Section("Expired Prescriptions") {
                    ForEach(samplePrescriptions.filter { $0.refillsRemaining == 0 }) { prescription in
                        PrescriptionRow(prescription: prescription)
                            .onTapGesture {
                                selectedPrescription = prescription
                            }
                    }
                }
                
                Section("Pharmacy Information") {
                    HStack {
                        Image(systemName: "cross.fill")
                            .foregroundColor(.red)
                        VStack(alignment: .leading) {
                            Text("CVS Pharmacy")
                                .font(.headline)
                            Text("123 Main Street")
                                .font(.caption)
                                .foregroundColor(.secondary)
                            Text("Phone: (555) 123-4567")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
            }
            .navigationTitle("Prescriptions")
            .sheet(item: $selectedPrescription) { prescription in
                PrescriptionDetailView(prescription: prescription)
            }
        }
    }
}

struct PrescriptionRow: View {
    let prescription: Prescription
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(prescription.medicationName)
                    .font(.headline)
                Spacer()
                Text("\(prescription.refillsRemaining) refills")
                    .font(.caption)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(prescription.refillsRemaining > 0 ? Color.green.opacity(0.2) : Color.red.opacity(0.2))
                    .foregroundColor(prescription.refillsRemaining > 0 ? .green : .red)
                    .cornerRadius(8)
            }
            
            Text("\(prescription.dosage) - \(prescription.frequency)")
                .font(.subheadline)
                .foregroundColor(.primary)
            
            Text("Prescribed by \(prescription.prescribedBy)")
                .font(.caption)
                .foregroundColor(.blue)
            
            Text("Date: \(prescription.datePrescibed, style: .date)")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(.vertical, 4)
    }
}

struct PrescriptionDetailView: View {
    let prescription: Prescription
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Medication")
                            .font(.headline)
                        Text(prescription.medicationName)
                            .font(.title2)
                            .fontWeight(.semibold)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Dosage & Frequency")
                            .font(.headline)
                        Text("\(prescription.dosage) - \(prescription.frequency)")
                            .font(.body)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Prescribed By")
                            .font(.headline)
                        Text(prescription.prescribedBy)
                            .font(.body)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Date Prescribed")
                            .font(.headline)
                        Text(prescription.datePrescibed, style: .date)
                            .font(.body)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Refills Remaining")
                            .font(.headline)
                        Text("\(prescription.refillsRemaining)")
                            .font(.body)
                            .fontWeight(.semibold)
                    }
                    
                    if prescription.refillsRemaining > 0 {
                        Button(action: {}) {
                            HStack {
                                Image(systemName: "arrow.clockwise")
                                Text("Request Refill")
                            }
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(10)
                        }
                    }
                    
                    Spacer()
                }
                .padding()
            }
            .navigationTitle("Prescription Details")
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
    PrescriptionView()
}
