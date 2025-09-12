import SwiftUI

struct NewActivityView: View {
    @Environment(\.presentationMode) var presentationMode
    let activityStore: ActivityStore
    let analyticsService: AnalyticsService
    
    @State private var title = ""
    @State private var selectedCategory = ActivityCategory.work
    @State private var duration = 1.0
    @State private var notes = ""
    @State private var location = ""
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Activity Details")) {
                    TextField("Activity Title", text: $title)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    
                    Picker("Category", selection: $selectedCategory) {
                        ForEach(ActivityCategory.allCases, id: \.self) { category in
                            HStack {
                                Image(systemName: category.icon)
                                    .foregroundColor(category.color)
                                Text(category.rawValue)
                            }
                            .tag(category)
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                    
                    VStack(alignment: .leading) {
                        HStack {
                            Text("Duration")
                            Spacer()
                            Text("\(duration, specifier: "%.1f") hours")
                                .foregroundColor(.secondary)
                        }
                        
                        Slider(value: $duration, in: 0.1...12.0, step: 0.1) {
                            Text("Duration")
                        } minimumValueLabel: {
                            Text("0.1h")
                                .font(.caption)
                        } maximumValueLabel: {
                            Text("12h")
                                .font(.caption)
                        }
                    }
                }
                
                Section(header: Text("Additional Information")) {
                    TextField("Location (optional)", text: $location)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                    
                    VStack(alignment: .leading) {
                        Text("Notes")
                            .font(.headline)
                        TextField("Add any notes about this activity...", text: $notes, axis: .vertical)
                            .lineLimit(3...6)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }
                }
                
                Section(header: Text("Privacy & Sync"), 
                       footer: Text("When enabled, your activity data will be securely synced to InsightCorp Analytics for personalized insights and recommendations.")) {
                    HStack {
                        VStack(alignment: .leading) {
                            HStack {
                                Image(systemName: "cloud.fill")
                                    .foregroundColor(analyticsService.isCloudSyncEnabled ? .blue : .gray)
                                Text("Enhanced Analytics")
                                    .font(.headline)
                            }
                            Text("Sync to cloud for AI-powered insights")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                        
                        VStack {
                            Toggle("", isOn: .constant(analyticsService.isCloudSyncEnabled))
                                .disabled(true) // Show current state but don't allow changes here
                            
                            if analyticsService.isCloudSyncEnabled {
                                Text("Active")
                                    .font(.caption)
                                    .foregroundColor(.green)
                            } else {
                                Text("Disabled")
                                    .font(.caption)
                                    .foregroundColor(.gray)
                            }
                        }
                    }
                }
                
                if analyticsService.isCloudSyncEnabled {
                    Section {
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Image(systemName: "info.circle.fill")
                                    .foregroundColor(.blue)
                                Text("Data Sharing Notice")
                                    .font(.headline)
                            }
                            
                            Text("This activity will be shared with our analytics partner to provide:")
                                .font(.caption)
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text("• Personalized productivity insights")
                                Text("• Activity pattern analysis")
                                Text("• Recommendations for optimization")
                                Text("• Benchmarking against similar users")
                            }
                            .font(.caption)
                            .foregroundColor(.secondary)
                        }
                        .padding(.vertical, 4)
                    }
                }
            }
            .navigationTitle("New Activity")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(
                leading: Button("Cancel") {
                    presentationMode.wrappedValue.dismiss()
                },
                trailing: Button("Save") {
                    saveActivity()
                }
                .disabled(title.isEmpty)
                .fontWeight(.semibold)
            )
        }
    }
    
    private func saveActivity() {
        let activity = Activity(
            title: title,
            category: selectedCategory,
            duration: duration,
            notes: notes,
            location: location.isEmpty ? nil : location
        )
        
        activityStore.addActivity(activity)
        
        // Automatically sync new activity to analytics service when cloud sync is enabled
        if analyticsService.isCloudSyncEnabled {
            print("💾 Saving activity: \(title)")
            analyticsService.syncRecentActivities([activity])
        }
        
        presentationMode.wrappedValue.dismiss()
    }
}

#Preview {
    NewActivityView(
        activityStore: ActivityStore(),
        analyticsService: AnalyticsService()
    )
}
