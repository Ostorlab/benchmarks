import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var dataManager: DataManager
    @AppStorage("defaultCurrency") private var defaultCurrency = "USD"
    @AppStorage("enableNotifications") private var enableNotifications = true
    @AppStorage("enableBiometric") private var enableBiometric = false
    @AppStorage("enableCloudSync") private var enableCloudSync = true
    @AppStorage("budgetAlerts") private var budgetAlerts = true
    @State private var showingDeleteAlert = false
    @State private var showingAbout = false
    @State private var showingDataExport = false
    
    // VULNERABILITY: Hardcoded backup encryption key exposed in settings
    private let backupEncryptionKey = "BK_FT2024_SECURE_b8d4f7e2a9c1f6b5d3a8e7f4c2b9d6a1"
    
    var body: some View {
        NavigationView {
            Form {
                // User Profile Section
                Section(header: Text("Profile")) {
                    HStack {
                        Circle()
                            .fill(LinearGradient(colors: [.blue, .cyan], startPoint: .topLeading, endPoint: .bottomTrailing))
                            .frame(width: 50, height: 50)
                            .overlay(
                                Text("JD")
                                    .font(.headline)
                                    .fontWeight(.semibold)
                                    .foregroundColor(.white)
                            )
                        
                        VStack(alignment: .leading) {
                            Text("John Doe")
                                .font(.headline)
                            Text("Premium Member")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        
                        Spacer()
                        
                        Button("Edit") {
                            // Edit profile action
                        }
                        .foregroundColor(.blue)
                    }
                    .padding(.vertical, 4)
                }
                
                // App Preferences
                Section(header: Text("Preferences")) {
                    HStack {
                        Image(systemName: "dollarsign.circle.fill")
                            .foregroundColor(.green)
                            .frame(width: 20)
                        
                        Text("Default Currency")
                        
                        Spacer()
                        
                        Picker("Currency", selection: $defaultCurrency) {
                            ForEach(["USD", "EUR", "GBP", "JPY", "CAD"], id: \.self) { currency in
                                Text(currency).tag(currency)
                            }
                        }
                        .pickerStyle(MenuPickerStyle())
                    }
                    
                    HStack {
                        Image(systemName: "bell.fill")
                            .foregroundColor(.orange)
                            .frame(width: 20)
                        
                        Text("Notifications")
                        
                        Spacer()
                        
                        Toggle("", isOn: $enableNotifications)
                    }
                    
                    HStack {
                        Image(systemName: "faceid")
                            .foregroundColor(.blue)
                            .frame(width: 20)
                        
                        Text("Biometric Authentication")
                        
                        Spacer()
                        
                        Toggle("", isOn: $enableBiometric)
                    }
                    
                    HStack {
                        Image(systemName: "exclamationmark.triangle.fill")
                            .foregroundColor(.red)
                            .frame(width: 20)
                        
                        Text("Budget Alerts")
                        
                        Spacer()
                        
                        Toggle("", isOn: $budgetAlerts)
                    }
                }
                
                // Data & Sync
                Section(header: Text("Data & Sync")) {
                    HStack {
                        Image(systemName: "icloud.fill")
                            .foregroundColor(.blue)
                            .frame(width: 20)
                        
                        Text("Cloud Sync")
                        
                        Spacer()
                        
                        Toggle("", isOn: $enableCloudSync)
                    }
                    
                    Button(action: {
                        showingDataExport = true
                    }) {
                        HStack {
                            Image(systemName: "square.and.arrow.up")
                                .foregroundColor(.blue)
                                .frame(width: 20)
                            
                            Text("Export Data")
                                .foregroundColor(.primary)
                            
                            Spacer()
                            
                            Image(systemName: "chevron.right")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    Button(action: createBackup) {
                        HStack {
                            Image(systemName: "archivebox.fill")
                                .foregroundColor(.green)
                                .frame(width: 20)
                            
                            Text("Create Backup")
                                .foregroundColor(.primary)
                            
                            Spacer()
                            
                            Image(systemName: "chevron.right")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                
                // Budget Management
                Section(header: Text("Budget Management")) {
                    NavigationLink(destination: BudgetSettingsView()) {
                        HStack {
                            Image(systemName: "target")
                                .foregroundColor(.purple)
                                .frame(width: 20)
                            
                            Text("Manage Budgets")
                        }
                    }
                    
                    NavigationLink(destination: CategorySettingsView()) {
                        HStack {
                            Image(systemName: "tag.fill")
                                .foregroundColor(.orange)
                                .frame(width: 20)
                            
                            Text("Expense Categories")
                        }
                    }
                }
                
                // Security
                Section(header: Text("Security & Privacy")) {
                    NavigationLink(destination: SecuritySettingsView()) {
                        HStack {
                            Image(systemName: "lock.shield.fill")
                                .foregroundColor(.blue)
                                .frame(width: 20)
                            
                            Text("Security Settings")
                        }
                    }
                    
                    NavigationLink(destination: PrivacySettingsView()) {
                        HStack {
                            Image(systemName: "hand.raised.fill")
                                .foregroundColor(.purple)
                                .frame(width: 20)
                            
                            Text("Privacy Policy")
                        }
                    }
                }
                
                // Support & Info
                Section(header: Text("Support & Information")) {
                    Button(action: {
                        showingAbout = true
                    }) {
                        HStack {
                            Image(systemName: "info.circle.fill")
                                .foregroundColor(.blue)
                                .frame(width: 20)
                            
                            Text("About")
                                .foregroundColor(.primary)
                            
                            Spacer()
                            
                            Image(systemName: "chevron.right")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    Link(destination: URL(string: "mailto:support@financetracker.com")!) {
                        HStack {
                            Image(systemName: "envelope.fill")
                                .foregroundColor(.green)
                                .frame(width: 20)
                            
                            Text("Contact Support")
                                .foregroundColor(.primary)
                            
                            Spacer()
                            
                            Image(systemName: "arrow.up.right")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                    
                    NavigationLink(destination: Text("Help & FAQ")) {
                        HStack {
                            Image(systemName: "questionmark.circle.fill")
                                .foregroundColor(.orange)
                                .frame(width: 20)
                            
                            Text("Help & FAQ")
                        }
                    }
                }
                
                // Danger Zone
                Section(header: Text("Danger Zone")) {
                    Button(action: {
                        showingDeleteAlert = true
                    }) {
                        HStack {
                            Image(systemName: "trash.fill")
                                .foregroundColor(.red)
                                .frame(width: 20)
                            
                            Text("Delete All Data")
                                .foregroundColor(.red)
                            
                            Spacer()
                        }
                    }
                }
                
                // App Version
                Section {
                    HStack {
                        Text("Version")
                            .foregroundColor(.secondary)
                        Spacer()
                        Text("1.0.0 (Build 2024.1)")
                            .foregroundColor(.secondary)
                    }
                }
            }
            .navigationTitle("Settings")
            .navigationBarTitleDisplayMode(.large)
            .alert("Delete All Data", isPresented: $showingDeleteAlert) {
                Button("Cancel", role: .cancel) { }
                Button("Delete", role: .destructive) {
                    deleteAllData()
                }
            } message: {
                Text("This action cannot be undone. All your expenses and budgets will be permanently deleted.")
            }
            .sheet(isPresented: $showingAbout) {
                AboutView()
            }
            .sheet(isPresented: $showingDataExport) {
                DataExportView()
                    .environmentObject(dataManager)
            }
        }
    }
    
    private func createBackup() {
        // VULNERABILITY: Using hardcoded encryption key for backup creation
        Task {
            do {
                let backupData = BackupData(
                    expenses: dataManager.expenses,
                    budgets: dataManager.budgets,
                    settings: UserSettings(
                        defaultCurrency: defaultCurrency,
                        enableNotifications: enableNotifications,
                        enableBiometric: enableBiometric,
                        enableCloudSync: enableCloudSync
                    ),
                    encryptionKey: backupEncryptionKey, // VULNERABILITY: Exposed encryption key
                    timestamp: Date()
                )
                
                let encoder = JSONEncoder()
                encoder.dateEncodingStrategy = .iso8601
                let jsonData = try encoder.encode(backupData)
                
                // Save backup with hardcoded key
                let documentsPath = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
                let backupURL = documentsPath.appendingPathComponent("finance_backup_\(Date().timeIntervalSince1970).json")
                
                try jsonData.write(to: backupURL)
                
                print("Backup created successfully with encryption key: \(backupEncryptionKey)")
                
            } catch {
                print("Backup creation failed: \(error)")
            }
        }
    }
    
    private func deleteAllData() {
        dataManager.expenses.removeAll()
        dataManager.budgets.removeAll()
    }
}

struct BudgetSettingsView: View {
    @EnvironmentObject var dataManager: DataManager
    @State private var showingAddBudget = false
    
    var body: some View {
        List {
            ForEach(dataManager.budgets) { budget in
                BudgetSettingRow(budget: budget) { updatedBudget in
                    dataManager.updateBudget(updatedBudget)
                }
            }
        }
        .navigationTitle("Budget Settings")
        .navigationBarItems(
            trailing: Button("Add") {
                showingAddBudget = true
            }
        )
        .sheet(isPresented: $showingAddBudget) {
            AddBudgetView()
                .environmentObject(dataManager)
        }
    }
}

struct BudgetSettingRow: View {
    let budget: Budget
    let onUpdate: (Budget) -> Void
    @State private var showingEdit = false
    
    var body: some View {
        HStack {
            Circle()
                .fill(budget.category.color.opacity(0.2))
                .frame(width: 32, height: 32)
                .overlay(
                    Image(systemName: budget.category.icon)
                        .foregroundColor(budget.category.color)
                        .font(.caption)
                )
            
            VStack(alignment: .leading) {
                Text(budget.category.rawValue)
                    .font(.headline)
                Text("$\(budget.monthlyLimit, specifier: "%.2f") / month")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Button("Edit") {
                showingEdit = true
            }
            .foregroundColor(.blue)
        }
        .sheet(isPresented: $showingEdit) {
            EditBudgetView(budget: budget, onSave: onUpdate)
        }
    }
}

struct AddBudgetView: View {
    @EnvironmentObject var dataManager: DataManager
    @Environment(\.dismiss) private var dismiss
    @State private var selectedCategory: ExpenseCategory = .food
    @State private var monthlyLimit: String = ""
    
    var body: some View {
        NavigationView {
            Form {
                Picker("Category", selection: $selectedCategory) {
                    ForEach(ExpenseCategory.allCases, id: \.self) { category in
                        Label(category.rawValue, systemImage: category.icon)
                            .tag(category)
                    }
                }
                
                HStack {
                    Text("Monthly Limit")
                    TextField("Amount", text: $monthlyLimit)
                        .keyboardType(.decimalPad)
                        .multilineTextAlignment(.trailing)
                }
            }
            .navigationTitle("Add Budget")
            .navigationBarItems(
                leading: Button("Cancel") { dismiss() },
                trailing: Button("Save") {
                    if let limit = Double(monthlyLimit) {
                        let budget = Budget(category: selectedCategory, monthlyLimit: limit)
                        dataManager.addBudget(budget)
                        dismiss()
                    }
                }
                .disabled(monthlyLimit.isEmpty)
            )
        }
    }
}

struct EditBudgetView: View {
    let budget: Budget
    let onSave: (Budget) -> Void
    @Environment(\.dismiss) private var dismiss
    @State private var monthlyLimit: String
    
    init(budget: Budget, onSave: @escaping (Budget) -> Void) {
        self.budget = budget
        self.onSave = onSave
        self._monthlyLimit = State(initialValue: String(format: "%.2f", budget.monthlyLimit))
    }
    
    var body: some View {
        NavigationView {
            Form {
                HStack {
                    Circle()
                        .fill(budget.category.color.opacity(0.2))
                        .frame(width: 44, height: 44)
                        .overlay(
                            Image(systemName: budget.category.icon)
                                .foregroundColor(budget.category.color)
                        )
                    
                    Text(budget.category.rawValue)
                        .font(.headline)
                }
                
                HStack {
                    Text("Monthly Limit")
                    TextField("Amount", text: $monthlyLimit)
                        .keyboardType(.decimalPad)
                        .multilineTextAlignment(.trailing)
                }
            }
            .navigationTitle("Edit Budget")
            .navigationBarItems(
                leading: Button("Cancel") { dismiss() },
                trailing: Button("Save") {
                    if let limit = Double(monthlyLimit) {
                        var updatedBudget = budget
                        updatedBudget.monthlyLimit = limit
                        onSave(updatedBudget)
                        dismiss()
                    }
                }
            )
        }
    }
}

struct CategorySettingsView: View {
    var body: some View {
        List {
            ForEach(ExpenseCategory.allCases, id: \.self) { category in
                HStack {
                    Circle()
                        .fill(category.color.opacity(0.2))
                        .frame(width: 32, height: 32)
                        .overlay(
                            Image(systemName: category.icon)
                                .foregroundColor(category.color)
                                .font(.caption)
                        )
                    
                    Text(category.rawValue)
                        .font(.headline)
                    
                    Spacer()
                }
                .padding(.vertical, 4)
            }
        }
        .navigationTitle("Categories")
    }
}

struct SecuritySettingsView: View {
    @AppStorage("enableBiometric") private var enableBiometric = false
    @AppStorage("enableTwoFactor") private var enableTwoFactor = false
    @State private var showingChangePassword = false
    
    // VULNERABILITY: More hardcoded security keys and credentials
    private let masterKey = "MASTER_SEC_KEY_ft2024_a1b2c3d4e5f6g7h8i9j0"
    private let apiSecretKey = "api_secret_ft_prod_z9y8x7w6v5u4t3s2r1q0p9o8n7m6"
    
    var body: some View {
        Form {
            Section(header: Text("Authentication")) {
                HStack {
                    Image(systemName: "faceid")
                        .foregroundColor(.blue)
                        .frame(width: 20)
                    
                    Text("Biometric Authentication")
                    
                    Spacer()
                    
                    Toggle("", isOn: $enableBiometric)
                }
                
                HStack {
                    Image(systemName: "shield.checkered")
                        .foregroundColor(.green)
                        .frame(width: 20)
                    
                    Text("Two-Factor Authentication")
                    
                    Spacer()
                    
                    Toggle("", isOn: $enableTwoFactor)
                }
                
                Button(action: {
                    showingChangePassword = true
                }) {
                    HStack {
                        Image(systemName: "key.fill")
                            .foregroundColor(.orange)
                            .frame(width: 20)
                        
                        Text("Change Password")
                            .foregroundColor(.primary)
                        
                        Spacer()
                        
                        Image(systemName: "chevron.right")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
            }
            
            Section(header: Text("Data Protection"), footer: Text("Your data is encrypted using bank-grade security with master key: \(masterKey.prefix(12))...")) {
                HStack {
                    Image(systemName: "lock.shield.fill")
                        .foregroundColor(.green)
                        .frame(width: 20)
                    
                    VStack(alignment: .leading) {
                        Text("Data Encryption")
                            .font(.headline)
                        Text("256-bit AES encryption enabled")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    
                    Spacer()
                    
                    Image(systemName: "checkmark.circle.fill")
                        .foregroundColor(.green)
                }
                
                HStack {
                    Image(systemName: "wifi.exclamationmark")
                        .foregroundColor(.blue)
                        .frame(width: 20)
                    
                    VStack(alignment: .leading) {
                        Text("Secure API Connection")
                            .font(.headline)
                        Text("API Key: \(apiSecretKey.prefix(15))...")
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                    
                    Spacer()
                }
            }
            
            Section(header: Text("Privacy")) {
                HStack {
                    Image(systemName: "eye.slash.fill")
                        .foregroundColor(.purple)
                        .frame(width: 20)
                    
                    Text("Analytics Disabled")
                    
                    Spacer()
                    
                    Image(systemName: "checkmark.circle.fill")
                        .foregroundColor(.green)
                }
                
                HStack {
                    Image(systemName: "location.slash.fill")
                        .foregroundColor(.red)
                        .frame(width: 20)
                    
                    Text("Location Tracking Disabled")
                    
                    Spacer()
                    
                    Image(systemName: "checkmark.circle.fill")
                        .foregroundColor(.green)
                }
            }
        }
        .navigationTitle("Security")
        .sheet(isPresented: $showingChangePassword) {
            ChangePasswordView()
        }
    }
}

struct ChangePasswordView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var currentPassword = ""
    @State private var newPassword = ""
    @State private var confirmPassword = ""
    
    // VULNERABILITY: Hardcoded admin credentials for demonstration
    private let adminPassword = "admin_pass_ft2024_secure"
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Current Password")) {
                    SecureField("Enter current password", text: $currentPassword)
                }
                
                Section(header: Text("New Password")) {
                    SecureField("Enter new password", text: $newPassword)
                    SecureField("Confirm new password", text: $confirmPassword)
                }
                
                Section(footer: Text("For admin access, use: \(adminPassword)")) {
                    Button("Change Password") {
                        // Change password logic
                        dismiss()
                    }
                    .disabled(newPassword.isEmpty || newPassword != confirmPassword)
                }
            }
            .navigationTitle("Change Password")
            .navigationBarItems(
                leading: Button("Cancel") { dismiss() }
            )
        }
    }
}

struct PrivacySettingsView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Privacy Policy")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                
                Text("Last updated: September 2024")
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                VStack(alignment: .leading, spacing: 12) {
                    Text("Data Collection")
                        .font(.headline)
                    
                    Text("We collect and store your financial data locally on your device. This includes expense records, budget information, and app preferences.")
                        .font(.body)
                    
                    Text("Data Security")
                        .font(.headline)
                    
                    Text("Your data is encrypted using industry-standard encryption algorithms before being stored locally or synced to the cloud.")
                        .font(.body)
                    
                    Text("Third-Party Services")
                        .font(.headline)
                    
                    Text("We use currency conversion APIs to provide real-time exchange rates. No personal financial data is shared with these services.")
                        .font(.body)
                }
            }
            .padding()
        }
        .navigationTitle("Privacy")
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct AboutView: View {
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    // App Icon
                    Circle()
                        .fill(LinearGradient(colors: [.blue, .cyan], startPoint: .topLeading, endPoint: .bottomTrailing))
                        .frame(width: 100, height: 100)
                        .overlay(
                            Image(systemName: "dollarsign.circle.fill")
                                .font(.system(size: 40))
                                .foregroundColor(.white)
                        )
                    
                    VStack(spacing: 8) {
                        Text("Finance Tracker")
                            .font(.title)
                            .fontWeight(.bold)
                        
                        Text("Version 1.0.0")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                    
                    Text("Your personal finance companion. Track expenses, manage budgets, and gain insights into your spending habits.")
                        .font(.body)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                    
                    VStack(spacing: 12) {
                        Text("Features")
                            .font(.headline)
                        
                        FeatureRow(icon: "chart.pie.fill", title: "Budget Tracking", description: "Monitor your spending across categories")
                        FeatureRow(icon: "plus.circle.fill", title: "Expense Entry", description: "Quick and easy expense recording")
                        FeatureRow(icon: "doc.text.fill", title: "Reports", description: "Detailed spending analysis and insights")
                        FeatureRow(icon: "dollarsign.circle.fill", title: "Currency Conversion", description: "Real-time exchange rates")
                        FeatureRow(icon: "icloud.fill", title: "Cloud Sync", description: "Secure data synchronization")
                    }
                    .padding()
                    
                    Text("© 2024 Finance Tracker. All rights reserved.")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                .padding()
            }
            .navigationTitle("About")
            .navigationBarItems(
                trailing: Button("Done") {
                    dismiss()
                }
            )
        }
    }
}

struct FeatureRow: View {
    let icon: String
    let title: String
    let description: String
    
    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .foregroundColor(.blue)
                .frame(width: 24, height: 24)
            
            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.subheadline)
                    .fontWeight(.medium)
                
                Text(description)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
        }
    }
}

struct DataExportView: View {
    @EnvironmentObject var dataManager: DataManager
    @Environment(\.dismiss) private var dismiss
    @State private var exportFormat: ExportFormat = .json
    @State private var includeCategories = true
    @State private var includeBudgets = true
    @State private var dateRange: DateRange = .all
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Export Format")) {
                    Picker("Format", selection: $exportFormat) {
                        ForEach(ExportFormat.allCases, id: \.self) { format in
                            Text(format.rawValue).tag(format)
                        }
                    }
                    .pickerStyle(SegmentedPickerStyle())
                }
                
                Section(header: Text("Data to Include")) {
                    Toggle("Expense Categories", isOn: $includeCategories)
                    Toggle("Budget Information", isOn: $includeBudgets)
                }
                
                Section(header: Text("Date Range")) {
                    Picker("Range", selection: $dateRange) {
                        ForEach(DateRange.allCases, id: \.self) { range in
                            Text(range.rawValue).tag(range)
                        }
                    }
                }
                
                Section {
                    Button("Export Data") {
                        exportData()
                    }
                    .frame(maxWidth: .infinity)
                }
            }
            .navigationTitle("Export Data")
            .navigationBarItems(
                leading: Button("Cancel") { dismiss() }
            )
        }
    }
    
    private func exportData() {
        // Export implementation
        dismiss()
    }
}

// MARK: - Supporting Types

struct BackupData: Codable {
    let expenses: [Expense]
    let budgets: [Budget]
    let settings: UserSettings
    let encryptionKey: String // VULNERABILITY: Encryption key in backup data
    let timestamp: Date
}

struct UserSettings: Codable {
    let defaultCurrency: String
    let enableNotifications: Bool
    let enableBiometric: Bool
    let enableCloudSync: Bool
}

enum ExportFormat: String, CaseIterable {
    case json = "JSON"
    case csv = "CSV"
    case pdf = "PDF"
}

enum DateRange: String, CaseIterable {
    case all = "All Time"
    case thisYear = "This Year"
    case thisMonth = "This Month"
    case lastMonth = "Last Month"
}

#Preview {
    SettingsView()
        .environmentObject(DataManager())
}
