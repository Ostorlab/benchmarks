import SwiftUI

struct BackupView: View {
    @State private var backups: [BackupInfo] = []
    @State private var isCreatingBackup = false
    @State private var isRestoringBackup = false
    @State private var selectedBackupId: String?
    @State private var showingBackupDetails = false
    
    private let backupService = BackupService()
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                if backups.isEmpty && !isCreatingBackup {
                    Spacer()
                    VStack(spacing: 20) {
                        Image(systemName: "externaldrive")
                            .font(.system(size: 50))
                            .foregroundColor(.gray)
                        Text("No backups available")
                            .font(.headline)
                            .foregroundColor(.gray)
                        Text("Create your first backup to protect your documents")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .padding()
                    Spacer()
                } else {
                    List {
                        Section(header: Text("Available Backups")) {
                            ForEach(backups, id: \.id) { backup in
                                BackupRowView(
                                    backup: backup,
                                    isSelected: selectedBackupId == backup.id,
                                    onTap: { selectBackup(backup.id) },
                                    onDelete: { deleteBackup(backup.id) }
                                )
                            }
                        }
                        
                        if !backups.isEmpty {
                            Section(header: Text("Backup Information")) {
                                VStack(alignment: .leading, spacing: 10) {
                                    HStack {
                                        Image(systemName: "info.circle")
                                            .foregroundColor(.blue)
                                        Text("Backup Features")
                                            .font(.headline)
                                    }
                                    
                                    VStack(alignment: .leading, spacing: 6) {
                                        FeatureRow(icon: "checkmark.circle", text: "SHA-1 integrity verification", color: .green)
                                        FeatureRow(icon: "checkmark.circle", text: "Manifest checksum validation", color: .green)
                                        FeatureRow(icon: "checkmark.circle", text: "Automatic file deduplication", color: .green)
                                        FeatureRow(icon: "checkmark.circle", text: "Cross-platform compatibility", color: .green)
                                    }
                                }
                                .padding(.vertical, 5)
                            }
                        }
                    }
                    .listStyle(InsetGroupedListStyle())
                    
                    VStack(spacing: 15) {
                        HStack(spacing: 15) {
                            Button(action: createBackup) {
                                HStack {
                                    if isCreatingBackup {
                                        ProgressView()
                                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                            .scaleEffect(0.8)
                                    } else {
                                        Image(systemName: "plus.circle")
                                    }
                                    Text(isCreatingBackup ? "Creating..." : "Create Backup")
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                            }
                            .disabled(isCreatingBackup || isRestoringBackup)
                            
                            Button(action: restoreSelectedBackup) {
                                HStack {
                                    if isRestoringBackup {
                                        ProgressView()
                                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                            .scaleEffect(0.8)
                                    } else {
                                        Image(systemName: "arrow.clockwise")
                                    }
                                    Text(isRestoringBackup ? "Restoring..." : "Restore")
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(selectedBackupId == nil ? Color.gray.opacity(0.3) : Color.green)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                            }
                            .disabled(selectedBackupId == nil || isCreatingBackup || isRestoringBackup)
                        }
                        
                        Button(action: refreshBackups) {
                            HStack {
                                Image(systemName: "arrow.clockwise")
                                Text("Refresh List")
                            }
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.gray.opacity(0.2))
                            .foregroundColor(.primary)
                            .cornerRadius(10)
                        }
                        .disabled(isCreatingBackup || isRestoringBackup)
                    }
                    .padding()
                    .background(Color.gray.opacity(0.05))
                }
            }
            .navigationTitle("Backup & Restore")
            .onAppear {
                loadBackups()
            }
        }
    }
    
    private func loadBackups() {
        backups = backupService.getAvailableBackups()
    }
    
    private func selectBackup(_ backupId: String) {
        selectedBackupId = selectedBackupId == backupId ? nil : backupId
    }
    
    private func createBackup() {
        isCreatingBackup = true
        
        DispatchQueue.global(qos: .background).async {
            let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
            do {
                let documentURLs = try FileManager.default.contentsOfDirectory(at: documentsDirectory, includingPropertiesForKeys: nil)
                    .filter { !$0.lastPathComponent.hasPrefix(".") && $0.lastPathComponent != "document_index.json" }
                
                backupService.createBackup(documents: documentURLs)
                
                DispatchQueue.main.async {
                    loadBackups()
                    isCreatingBackup = false
                }
            } catch {
                DispatchQueue.main.async {
                    isCreatingBackup = false
                }
            }
        }
    }
    
    private func restoreSelectedBackup() {
        guard let backupId = selectedBackupId else { return }
        
        isRestoringBackup = true
        
        DispatchQueue.global(qos: .background).async {
            let success = backupService.restoreBackup(backupId: backupId)
            
            DispatchQueue.main.async {
                isRestoringBackup = false
                if success {
                    selectedBackupId = nil
                }
            }
        }
    }
    
    private func deleteBackup(_ backupId: String) {
        let success = backupService.deleteBackup(backupId: backupId)
        if success {
            loadBackups()
            if selectedBackupId == backupId {
                selectedBackupId = nil
            }
        }
    }
    
    private func refreshBackups() {
        loadBackups()
    }
}

struct BackupRowView: View {
    let backup: BackupInfo
    let isSelected: Bool
    let onTap: () -> Void
    let onDelete: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            HStack {
                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        Text("Backup \(backup.id.prefix(8))")
                            .font(.headline)
                            .foregroundColor(.primary)
                        
                        Spacer()
                        
                        if isSelected {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.blue)
                                .font(.title3)
                        } else {
                            Image(systemName: "circle")
                                .foregroundColor(.gray)
                                .font(.title3)
                        }
                    }
                    
                    HStack(spacing: 15) {
                        Label("\(backup.documentCount) docs", systemImage: "doc.text")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        Label(backup.formattedSize, systemImage: "externaldrive")
                            .font(.caption)
                            .foregroundColor(.secondary)
                        
                        Label("SHA-1", systemImage: "checkmark.seal")
                            .font(.caption)
                            .foregroundColor(.green)
                    }
                    
                    Text("Created: \(backup.formattedDate)")
                        .font(.caption2)
                        .foregroundColor(.secondary)
                }
                
                Spacer()
            }
            .padding(.vertical, 4)
        }
        .buttonStyle(PlainButtonStyle())
        .swipeActions(edge: .trailing, allowsFullSwipe: false) {
            Button(role: .destructive, action: onDelete) {
                Label("Delete", systemImage: "trash")
            }
        }
    }
}

struct FeatureRow: View {
    let icon: String
    let text: String
    let color: Color
    
    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: icon)
                .foregroundColor(color)
                .font(.caption)
            Text(text)
                .font(.caption)
                .foregroundColor(.secondary)
        }
    }
}
