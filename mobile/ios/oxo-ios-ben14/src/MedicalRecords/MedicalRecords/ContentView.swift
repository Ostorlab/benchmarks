//
//  ContentView.swift
//  MedicalRecords
//
//  Created by Alaeddine Mesbahi on 9/10/25.
//

import SwiftUI

struct ContentView: View {
    @EnvironmentObject var sessionManager: SessionManager
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            DashboardView()
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("Dashboard")
                }
                .tag(0)
            
            MedicalRecordView()
                .tabItem {
                    Image(systemName: "doc.text.fill")
                    Text("Records")
                }
                .tag(1)
            
            AppointmentView()
                .tabItem {
                    Image(systemName: "calendar")
                    Text("Appointments")
                }
                .tag(2)
            
            PrescriptionView()
                .tabItem {
                    Image(systemName: "pills.fill")
                    Text("Prescriptions")
                }
                .tag(3)
            
            SettingsView()
                .tabItem {
                    Image(systemName: "gear")
                    Text("Settings")
                }
                .tag(4)
        }
        .environmentObject(sessionManager)
    }
}

struct DashboardView: View {
    @EnvironmentObject var sessionManager: SessionManager
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    if let patient = sessionManager.currentUser {
                        VStack(alignment: .leading, spacing: 15) {
                            HStack {
                                Image(systemName: "person.circle.fill")
                                    .font(.system(size: 60))
                                    .foregroundColor(.blue)
                                
                                VStack(alignment: .leading) {
                                    Text("Welcome, \(patient.name)")
                                        .font(.title2)
                                        .fontWeight(.semibold)
                                    Text("Patient ID: \(patient.id)")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                    Text("DOB: \(patient.dateOfBirth)")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                                Spacer()
                            }
                            .padding()
                            .background(Color(.systemGray6))
                            .cornerRadius(12)
                        }
                        
                        LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 2), spacing: 15) {
                            DashboardCard(title: "Blood Type", value: patient.bloodType, icon: "drop.fill", color: .red)
                            DashboardCard(title: "Conditions", value: "\(patient.conditions.count)", icon: "heart.fill", color: .orange)
                            DashboardCard(title: "Allergies", value: "\(patient.allergies.count)", icon: "exclamationmark.triangle.fill", color: .yellow)
                            DashboardCard(title: "Next Appointment", value: "Oct 15", icon: "calendar", color: .green)
                        }
                        
                        VStack(alignment: .leading, spacing: 10) {
                            Text("Recent Activity")
                                .font(.headline)
                            
                            VStack(spacing: 8) {
                                ActivityRow(title: "Lab results received", time: "2 hours ago", icon: "doc.text")
                                ActivityRow(title: "Prescription refilled", time: "1 day ago", icon: "pills")
                                ActivityRow(title: "Appointment scheduled", time: "3 days ago", icon: "calendar")
                            }
                            .padding()
                            .background(Color(.systemGray6))
                            .cornerRadius(12)
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("Medical Dashboard")
        }
    }
}

struct DashboardCard: View {
    let title: String
    let value: String
    let icon: String
    let color: Color
    
    var body: some View {
        VStack {
            Image(systemName: icon)
                .font(.largeTitle)
                .foregroundColor(color)
            
            Text(value)
                .font(.title2)
                .fontWeight(.bold)
            
            Text(title)
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(height: 100)
        .frame(maxWidth: .infinity)
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}

struct ActivityRow: View {
    let title: String
    let time: String
    let icon: String
    
    var body: some View {
        HStack {
            Image(systemName: icon)
                .foregroundColor(.blue)
            
            VStack(alignment: .leading) {
                Text(title)
                    .font(.subheadline)
                Text(time)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
        }
    }
}

struct SettingsView: View {
    @EnvironmentObject var sessionManager: SessionManager
    
    var body: some View {
        NavigationView {
            List {
                Section("Account") {
                    HStack {
                        Image(systemName: "person.fill")
                        Text("Profile Settings")
                    }
                    
                    HStack {
                        Image(systemName: "lock.fill")
                        Text("Privacy & Security")
                    }
                }
                
                Section("Preferences") {
                    HStack {
                        Image(systemName: "bell.fill")
                        Text("Notifications")
                    }
                    
                    HStack {
                        Image(systemName: "moon.fill")
                        Text("Dark Mode")
                    }
                }
                
                Section {
                    Button(action: {
                        sessionManager.logout()
                    }) {
                        HStack {
                            Image(systemName: "arrow.right.square.fill")
                                .foregroundColor(.red)
                            Text("Sign Out")
                                .foregroundColor(.red)
                        }
                    }
                }
            }
            .navigationTitle("Settings")
        }
    }
}

#Preview {
    ContentView()
        .environmentObject(SessionManager())
}
