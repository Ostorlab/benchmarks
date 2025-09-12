//
//  CloudView.swift
//  DocViewer
//
//  Created by elyousfi on 11/09/2025.
//

import SwiftUI

struct CloudView: View {
    @EnvironmentObject var documentManager: DocumentManager
    @EnvironmentObject var authManager: AuthenticationManager
    @State private var showingUploadSheet = false
    @State private var uploadProgress: Double = 0.0
    @State private var isUploading = false
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // Cloud Storage Status
                    if let user = authManager.currentUser {
                        VStack(spacing: 16) {
                            HStack {
                                Image(systemName: "icloud.fill")
                                    .font(.title)
                                    .foregroundColor(.blue)
                                
                                VStack(alignment: .leading) {
                                    Text("Cloud Storage")
                                        .font(.headline)
                                    Text("\(user.plan.displayName) Plan")
                                        .font(.subheadline)
                                        .foregroundColor(.secondary)
                                }
                                
                                Spacer()
                            }
                            
                            // Storage Usage
                            VStack(alignment: .leading, spacing: 8) {
                                HStack {
                                    Text("Storage Used")
                                        .font(.subheadline)
                                    
                                    Spacer()
                                    
                                    Text("\(user.formattedStorageUsed) of \(user.formattedStorageLimit)")
                                        .font(.subheadline)
                                        .foregroundColor(.secondary)
                                }
                                
                                ProgressView(value: user.storageUsedPercentage)
                                    .progressViewStyle(LinearProgressViewStyle(tint: storageColor(for: user.storageUsedPercentage)))
                                
                                Text("\(Int(user.storageUsedPercentage * 100))% used")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                            }
                        }
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(12)
                    }
                    
                    // Upload Progress
                    if isUploading {
                        VStack(spacing: 12) {
                            HStack {
                                Image(systemName: "icloud.and.arrow.up")
                                    .foregroundColor(.blue)
                                Text("Uploading to Cloud...")
                                    .font(.headline)
                                
                                Spacer()
                            }
                            
                            ProgressView(value: uploadProgress)
                                .progressViewStyle(LinearProgressViewStyle(tint: .blue))
                            
                            Text("\(Int(uploadProgress * 100))% complete")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        .padding()
                        .background(Color.blue.opacity(0.1))
                        .cornerRadius(12)
                    }
                    
                    // Cloud Folders
                    VStack(alignment: .leading, spacing: 16) {
                        HStack {
                            Text("Cloud Folders")
                                .font(.headline)
                            
                            Spacer()
                            
                            Button("New Folder") {
                                // Create new folder
                            }
                            .font(.subheadline)
                            .foregroundColor(.blue)
                        }
                        
                        LazyVGrid(columns: [
                            GridItem(.flexible()),
                            GridItem(.flexible())
                        ], spacing: 16) {
                            ForEach(documentManager.folders, id: \.id) { folder in
                                CloudFolderCard(folder: folder)
                            }
                        }
                    }
                    
                    // Recent Cloud Activity
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Recent Activity")
                            .font(.headline)
                        
                        LazyVStack(spacing: 12) {
                            ForEach(recentCloudActivities, id: \.id) { activity in
                                CloudActivityRow(activity: activity)
                            }
                        }
                    }
                    
                    // Cloud Services Integration
                    VStack(alignment: .leading, spacing: 16) {
                        Text("Connected Services")
                            .font(.headline)
                        
                        VStack(spacing: 12) {
                            CloudServiceRow(
                                name: "Google Drive",
                                icon: "globe",
                                isConnected: true,
                                lastSync: "2 hours ago"
                            )
                            
                            CloudServiceRow(
                                name: "Dropbox",
                                icon: "square.and.arrow.down",
                                isConnected: true,
                                lastSync: "5 hours ago"
                            )
                            
                            CloudServiceRow(
                                name: "OneDrive",
                                icon: "cloud",
                                isConnected: false,
                                lastSync: nil
                            )
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("Cloud")
            .navigationBarItems(
                trailing: Button(action: {
                    showingUploadSheet = true
                }) {
                    Image(systemName: "plus.circle")
                }
            )
            .refreshable {
                await refreshCloudData()
            }
        }
        .sheet(isPresented: $showingUploadSheet) {
            CloudUploadSheet(isUploading: $isUploading, uploadProgress: $uploadProgress)
                .environmentObject(documentManager)
        }
    }
    
    private func storageColor(for percentage: Double) -> Color {
        if percentage < 0.7 {
            return .green
        } else if percentage < 0.9 {
            return .orange
        } else {
            return .red
        }
    }
    
    private func refreshCloudData() async {
        try? await Task.sleep(nanoseconds: 1_500_000_000)
    }
    
    private var recentCloudActivities: [CloudActivity] {
        [
            CloudActivity(
                id: UUID(),
                type: .upload,
                fileName: "Q3 Financial Report.pdf",
                timestamp: Date().addingTimeInterval(-3600),
                status: .completed
            ),
            CloudActivity(
                id: UUID(),
                type: .sync,
                fileName: "Project Proposal - Mobile App.docx",
                timestamp: Date().addingTimeInterval(-7200),
                status: .completed
            ),
            CloudActivity(
                id: UUID(),
                type: .download,
                fileName: "Sales Dashboard.xlsx",
                timestamp: Date().addingTimeInterval(-10800),
                status: .completed
            ),
            CloudActivity(
                id: UUID(),
                type: .share,
                fileName: "Company Presentation.pptx",
                timestamp: Date().addingTimeInterval(-14400),
                status: .inProgress
            )
        ]
    }
}

struct CloudFolderCard: View {
    let folder: CloudFolder
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: "folder.fill")
                    .font(.title2)
                    .foregroundColor(.blue)
                
                Spacer()
                
                if folder.isShared {
                    Image(systemName: "person.2.fill")
                        .font(.caption)
                        .foregroundColor(.green)
                }
            }
            
            Text(folder.name)
                .font(.headline)
                .lineLimit(2)
            
            Text("\(folder.documentCount) documents")
                .font(.subheadline)
                .foregroundColor(.secondary)
            
            Text(folder.formattedDate)
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}

struct CloudActivityRow: View {
    let activity: CloudActivity
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: activity.type.iconName)
                .font(.title3)
                .foregroundColor(activity.type.color)
                .frame(width: 24, height: 24)
            
            VStack(alignment: .leading, spacing: 2) {
                Text(activity.type.displayName)
                    .font(.subheadline)
                    .fontWeight(.medium)
                
                Text(activity.fileName)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            VStack(alignment: .trailing, spacing: 2) {
                Text(activity.formattedTime)
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                Text(activity.status.displayName)
                    .font(.caption2)
                    .padding(.horizontal, 6)
                    .padding(.vertical, 2)
                    .background(activity.status.color.opacity(0.2))
                    .foregroundColor(activity.status.color)
                    .cornerRadius(4)
            }
        }
        .padding(.vertical, 8)
    }
}

struct CloudServiceRow: View {
    let name: String
    let icon: String
    let isConnected: Bool
    let lastSync: String?
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .font(.title3)
                .foregroundColor(.blue)
                .frame(width: 24, height: 24)
            
            VStack(alignment: .leading, spacing: 2) {
                Text(name)
                    .font(.subheadline)
                    .fontWeight(.medium)
                
                if let lastSync = lastSync {
                    Text("Last sync: \(lastSync)")
                        .font(.caption)
                        .foregroundColor(.secondary)
                } else {
                    Text("Not connected")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
            
            Spacer()
            
            HStack(spacing: 8) {
                Circle()
                    .fill(isConnected ? Color.green : Color.gray)
                    .frame(width: 8, height: 8)
                
                Text(isConnected ? "Connected" : "Disconnected")
                    .font(.caption)
                    .foregroundColor(isConnected ? .green : .gray)
            }
        }
        .padding(.vertical, 8)
    }
}

struct CloudActivity: Identifiable {
    let id: UUID
    let type: ActivityType
    let fileName: String
    let timestamp: Date
    let status: ActivityStatus
    
    var formattedTime: String {
        let formatter = RelativeDateTimeFormatter()
        formatter.unitsStyle = .abbreviated
        return formatter.localizedString(for: timestamp, relativeTo: Date())
    }
    
    enum ActivityType {
        case upload, download, sync, share
        
        var iconName: String {
            switch self {
            case .upload: return "icloud.and.arrow.up"
            case .download: return "icloud.and.arrow.down"
            case .sync: return "arrow.triangle.2.circlepath"
            case .share: return "square.and.arrow.up"
            }
        }
        
        var color: Color {
            switch self {
            case .upload: return .blue
            case .download: return .green
            case .sync: return .orange
            case .share: return .purple
            }
        }
        
        var displayName: String {
            switch self {
            case .upload: return "Uploaded"
            case .download: return "Downloaded"
            case .sync: return "Synced"
            case .share: return "Shared"
            }
        }
    }
    
    enum ActivityStatus {
        case completed, inProgress, failed
        
        var color: Color {
            switch self {
            case .completed: return .green
            case .inProgress: return .orange
            case .failed: return .red
            }
        }
        
        var displayName: String {
            switch self {
            case .completed: return "Done"
            case .inProgress: return "..."
            case .failed: return "Error"
            }
        }
    }
}

struct CloudUploadSheet: View {
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject var documentManager: DocumentManager
    @Binding var isUploading: Bool
    @Binding var uploadProgress: Double
    @State private var selectedFiles: [String] = []
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Text("Upload to Cloud")
                    .font(.title2)
                    .fontWeight(.bold)
                
                Text("Select documents to upload to your cloud storage")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                
                List(documentManager.documents, id: \.id) { document in
                    HStack {
                        Button(action: {
                            if selectedFiles.contains(document.id.uuidString) {
                                selectedFiles.removeAll { $0 == document.id.uuidString }
                            } else {
                                selectedFiles.append(document.id.uuidString)
                            }
                        }) {
                            Image(systemName: selectedFiles.contains(document.id.uuidString) ? "checkmark.circle.fill" : "circle")
                                .foregroundColor(selectedFiles.contains(document.id.uuidString) ? .blue : .gray)
                        }
                        
                        DocumentRowView(document: document)
                    }
                }
                
                Button(action: {
                    startUpload()
                }) {
                    Text("Upload \(selectedFiles.count) File\(selectedFiles.count == 1 ? "" : "s")")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(selectedFiles.isEmpty ? Color.gray : Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .disabled(selectedFiles.isEmpty)
            }
            .padding()
            .navigationBarItems(
                leading: Button("Cancel") {
                    presentationMode.wrappedValue.dismiss()
                }
            )
        }
    }
    
    private func startUpload() {
        isUploading = true
        uploadProgress = 0.0
        presentationMode.wrappedValue.dismiss()
        
        Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { timer in
            uploadProgress += 0.05
            if uploadProgress >= 1.0 {
                uploadProgress = 1.0
                timer.invalidate()
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                    isUploading = false
                    uploadProgress = 0.0
                }
            }
        }
    }
}
