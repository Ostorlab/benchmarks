import SwiftUI

struct SettingsView: View {
    @Environment(\.presentationMode) var presentationMode
    @ObservedObject var analyticsService: AnalyticsService
    @State private var showingDataDetails = false
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Privacy & Data Sharing")) {
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Image(systemName: "cloud.fill")
                                .foregroundColor(analyticsService.isCloudSyncEnabled ? .blue : .gray)
                            VStack(alignment: .leading) {
                                Text("Enhanced Analytics")
                                    .font(.headline)
                                Text("Powered by InsightCorp Analytics")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                            Spacer()
                            Toggle("", isOn: $analyticsService.isCloudSyncEnabled)
                                .onChange(of: analyticsService.isCloudSyncEnabled) { _ in
                                    analyticsService.toggleCloudSync()
                                }
                        }
                        
                        if analyticsService.isCloudSyncEnabled {
                            Text("✅ Your activities are being synced for personalized insights")
                                .font(.caption)
                                .foregroundColor(.green)
                        } else {
                            Text("⏸️ Analytics disabled - data stays on your device")
                                .font(.caption)
                                .foregroundColor(.orange)
                        }
                    }
                    
                    Button(action: {
                        showingDataDetails = true
                    }) {
                        HStack {
                            Text("What data is shared?")
                                .foregroundColor(.primary)
                            Spacer()
                            Image(systemName: "chevron.right")
                                .foregroundColor(.secondary)
                                .font(.caption)
                        }
                    }
                    
                    if let lastSync = analyticsService.lastSyncTime {
                        HStack {
                            Text("Last Sync")
                            Spacer()
                            Text(lastSync, style: .relative)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                
                Section(header: Text("Analytics Partner")) {
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Image(systemName: "building.2.fill")
                                .foregroundColor(.blue)
                            Text("InsightCorp Analytics")
                                .font(.headline)
                        }
                        
                        Text("Trusted partner for activity insights and productivity analytics")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    
                    HStack {
                        Text("Service Status")
                        Spacer()
                        HStack {
                            Circle()
                                .fill(.green)
                                .frame(width: 8, height: 8)
                            Text("Operational")
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    HStack {
                        Text("Data Center")
                        Spacer()
                        Text("US-East")
                            .foregroundColor(.secondary)
                    }
                }
                
                Section(header: Text("App Information")) {
                    HStack {
                        Text("Version")
                        Spacer()
                        Text(Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "1.0")
                            .foregroundColor(.secondary)
                    }
                    
                    HStack {
                        Text("Build")
                        Spacer()
                        Text(Bundle.main.infoDictionary?["CFBundleVersion"] as? String ?? "1")
                            .foregroundColor(.secondary)
                    }
                    
                    HStack {
                        Text("Device ID")
                        Spacer()
                        Text(UIDevice.current.identifierForVendor?.uuidString.prefix(8) ?? "Unknown")
                            .foregroundColor(.secondary)
                            .font(.system(.caption, design: .monospaced))
                    }
                }
                
                Section(header: Text("Advanced Features")) {
                    Button("Generate Insights Report") {
                        analyticsService.performAdvancedAnalytics()
                    }
                    .foregroundColor(.blue)
                }
                
                Section(footer: Text("EventTracker uses InsightCorp Analytics to provide personalized insights. Your data is processed according to our privacy policy and their terms of service.")) {
                    EmptyView()
                }
            }
            .navigationTitle("Settings")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(
                trailing: Button("Done") {
                    presentationMode.wrappedValue.dismiss()
                }
            )
        }
        .sheet(isPresented: $showingDataDetails) {
            DataSharingDetailsView()
        }
    }
}

struct DataSharingDetailsView: View {
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Data Sharing Details")
                            .font(.largeTitle)
                            .fontWeight(.bold)
                        
                        Text("When Enhanced Analytics is enabled, the following information is shared with InsightCorp Analytics:")
                            .foregroundColor(.secondary)
                    }
                    
                    VStack(alignment: .leading, spacing: 12) {
                        DataCategoryView(
                            title: "Activity Information",
                            icon: "chart.bar.fill",
                            color: .blue,
                            items: [
                                "Activity titles and descriptions",
                                "Categories and duration",
                                "Timestamps and frequency",
                                "Personal notes and comments"
                            ]
                        )
                        
                        DataCategoryView(
                            title: "Location Data",
                            icon: "location.fill",
                            color: .green,
                            items: [
                                "Activity locations (when provided)",
                                "General geographic region",
                                "Time zone information"
                            ]
                        )
                        
                        DataCategoryView(
                            title: "Device Information",
                            icon: "iphone",
                            color: .orange,
                            items: [
                                "Device model and OS version",
                                "App version and build number",
                                "Device identifier for analytics",
                                "Device name (may contain personal info)"
                            ]
                        )
                        
                        DataCategoryView(
                            title: "Usage Patterns",
                            icon: "brain.head.profile",
                            color: .purple,
                            items: [
                                "Activity frequency and patterns",
                                "Most used categories",
                                "Session duration and timing",
                                "Productivity metrics"
                            ]
                        )
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Why is this data shared?")
                            .font(.headline)
                        
                        Text("InsightCorp Analytics uses this information to:")
                            .foregroundColor(.secondary)
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text("• Provide personalized productivity insights")
                            Text("• Identify patterns in your activities")
                            Text("• Suggest optimizations and improvements")
                            Text("• Compare your metrics with similar users")
                            Text("• Improve the overall service quality")
                        }
                        .foregroundColor(.secondary)
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("⚠️ Important Notice")
                            .font(.headline)
                            .foregroundColor(.red)
                        
                        Text("This data is transmitted to third-party servers and may be subject to their privacy policies and data handling practices.")
                            .foregroundColor(.secondary)
                    }
                    .padding()
                    .background(Color.red.opacity(0.1))
                    .cornerRadius(8)
                }
                .padding()
            }
            .navigationTitle("Data Sharing")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(
                trailing: Button("Close") {
                    presentationMode.wrappedValue.dismiss()
                }
            )
        }
    }
}

struct DataCategoryView: View {
    let title: String
    let icon: String
    let color: Color
    let items: [String]
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: icon)
                    .foregroundColor(color)
                Text(title)
                    .font(.headline)
            }
            
            VStack(alignment: .leading, spacing: 4) {
                ForEach(items, id: \.self) { item in
                    Text("• \(item)")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(8)
    }
}

#Preview {
    SettingsView(analyticsService: AnalyticsService())
}
