import SwiftUI

struct AppointmentView: View {
    @State private var selectedAppointment: Appointment?
    @State private var showingNewAppointment = false
    
    private let sampleAppointments = [
        Appointment(
            patientId: "PT-1234",
            doctorName: "Dr. Sarah Johnson",
            specialty: "Cardiology",
            date: Date().addingTimeInterval(86400 * 5),
            reason: "Follow-up for hypertension",
            status: .scheduled
        ),
        Appointment(
            patientId: "PT-1234",
            doctorName: "Dr. Michael Chen",
            specialty: "Endocrinology",
            date: Date().addingTimeInterval(86400 * 12),
            reason: "Diabetes management review",
            status: .scheduled
        ),
        Appointment(
            patientId: "PT-1234",
            doctorName: "Dr. Emily Rodriguez",
            specialty: "Internal Medicine",
            date: Date().addingTimeInterval(-86400 * 7),
            reason: "Annual physical exam",
            status: .completed
        )
    ]
    
    var body: some View {
        NavigationView {
            List {
                Section("Upcoming Appointments") {
                    ForEach(sampleAppointments.filter { $0.status == .scheduled }) { appointment in
                        AppointmentRow(appointment: appointment)
                            .onTapGesture {
                                selectedAppointment = appointment
                            }
                    }
                }
                
                Section("Past Appointments") {
                    ForEach(sampleAppointments.filter { $0.status == .completed }) { appointment in
                        AppointmentRow(appointment: appointment)
                            .onTapGesture {
                                selectedAppointment = appointment
                            }
                    }
                }
            }
            .navigationTitle("Appointments")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { showingNewAppointment = true }) {
                        Image(systemName: "plus")
                    }
                }
            }
            .sheet(item: $selectedAppointment) { appointment in
                AppointmentDetailView(appointment: appointment)
            }
            .sheet(isPresented: $showingNewAppointment) {
                NewAppointmentView()
            }
        }
    }
}

struct AppointmentRow: View {
    let appointment: Appointment
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(appointment.doctorName)
                    .font(.headline)
                Spacer()
                Text(appointment.date, style: .date)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Text(appointment.specialty)
                .font(.subheadline)
                .foregroundColor(.blue)
            
            Text(appointment.reason)
                .font(.body)
                .foregroundColor(.primary)
            
            HStack {
                Text(appointment.date, style: .time)
                    .font(.caption)
                    .foregroundColor(.secondary)
                Spacer()
                Text(appointment.status.rawValue)
                    .font(.caption)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(appointment.status == .scheduled ? Color.green.opacity(0.2) : Color.gray.opacity(0.2))
                    .foregroundColor(appointment.status == .scheduled ? .green : .gray)
                    .cornerRadius(8)
            }
        }
        .padding(.vertical, 4)
    }
}

struct AppointmentDetailView: View {
    let appointment: Appointment
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Doctor")
                            .font(.headline)
                        Text(appointment.doctorName)
                            .font(.body)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Specialty")
                            .font(.headline)
                        Text(appointment.specialty)
                            .font(.body)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Date & Time")
                            .font(.headline)
                        Text(appointment.date, style: .date)
                            .font(.body)
                        Text(appointment.date, style: .time)
                            .font(.body)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Reason")
                            .font(.headline)
                        Text(appointment.reason)
                            .font(.body)
                    }
                    
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Status")
                            .font(.headline)
                        Text(appointment.status.rawValue)
                            .font(.body)
                    }
                    
                    Spacer()
                }
                .padding()
            }
            .navigationTitle("Appointment Details")
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

struct NewAppointmentView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var selectedDoctor = "Dr. Sarah Johnson"
    @State private var selectedSpecialty = "Cardiology"
    @State private var appointmentDate = Date()
    @State private var reason = ""
    
    private let doctors = ["Dr. Sarah Johnson", "Dr. Michael Chen", "Dr. Emily Rodriguez"]
    private let specialties = ["Cardiology", "Endocrinology", "Internal Medicine", "Dermatology"]
    
    var body: some View {
        NavigationView {
            Form {
                Section("Appointment Details") {
                    Picker("Doctor", selection: $selectedDoctor) {
                        ForEach(doctors, id: \.self) { doctor in
                            Text(doctor).tag(doctor)
                        }
                    }
                    
                    Picker("Specialty", selection: $selectedSpecialty) {
                        ForEach(specialties, id: \.self) { specialty in
                            Text(specialty).tag(specialty)
                        }
                    }
                    
                    DatePicker("Date & Time", selection: $appointmentDate, displayedComponents: [.date, .hourAndMinute])
                    
                    TextField("Reason for visit", text: $reason, axis: .vertical)
                        .lineLimit(3...6)
                }
            }
            .navigationTitle("New Appointment")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Book") {
                        dismiss()
                    }
                    .disabled(reason.isEmpty)
                }
            }
        }
    }
}

#Preview {
    AppointmentView()
}
