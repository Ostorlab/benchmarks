//
//  SettingsViewController.swift
//  PhotoShare Studio
//
//  Settings screen with various app options
//

import UIKit

class SettingsViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    private let settingsOptions = [
        ("General", ["Auto Upload Photos", "Storage Management", "Backup Settings"]),
        ("WiFi Sharing", ["Enable WiFi Sharing", "Share Password", "Connection Timeout"]),
        ("Privacy", ["Photo Access", "Location Services", "Analytics"]),
        ("Account", ["Premium Subscription", "Sync Settings", "Sign Out"])
    ]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupTableView()
    }
    
    private func setupUI() {
        title = "Settings"
        view.backgroundColor = UIColor.systemGroupedBackground
        
        navigationItem.rightBarButtonItem = UIBarButtonItem(
            title: "Done",
            style: .done,
            target: self,
            action: #selector(dismissSettings)
        )
    }
    
    private func setupTableView() {
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "SettingsCell")
    }
    
    @objc private func dismissSettings() {
        dismiss(animated: true)
    }
}

// MARK: - UITableView DataSource and Delegate
extension SettingsViewController: UITableViewDataSource, UITableViewDelegate {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return settingsOptions.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return settingsOptions[section].1.count
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return settingsOptions[section].0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "SettingsCell", for: indexPath)
        let option = settingsOptions[indexPath.section].1[indexPath.row]
        
        cell.textLabel?.text = option
        cell.accessoryType = .disclosureIndicator
        
        // Add switches for some options
        if option.contains("Enable") || option.contains("Auto") {
            let switchControl = UISwitch()
            switchControl.isOn = true
            cell.accessoryView = switchControl
        } else {
            cell.accessoryView = nil
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        let option = settingsOptions[indexPath.section].1[indexPath.row]
        
        switch option {
        case "Sign Out":
            showSignOutConfirmation()
        case "WiFi Sharing":
            showWiFiSharingSettings()
        case "Storage Management":
            showStorageInfo()
        case "Premium Subscription":
            showSubscriptionInfo()
        default:
            // Generic settings
            let alert = UIAlertController(title: option, message: "Configure \(option) settings here", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .default))
            present(alert, animated: true)
        }
    }
    
    private func showSignOutConfirmation() {
        let alert = UIAlertController(title: "Sign Out", 
                                    message: "Are you sure you want to sign out? This will stop WiFi sharing and require re-authentication.", 
                                    preferredStyle: .alert)
        
        alert.addAction(UIAlertAction(title: "Sign Out", style: .destructive) { _ in
            // Stop WiFi server if running
            WiFiSharingServer.shared.stopServer()
            
            // Logout
            AuthManager.shared.logout()
            
            // Dismiss settings and notify main app
            self.dismiss(animated: true) {
                NotificationCenter.default.post(name: .userDidLogout, object: nil)
            }
        })
        
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
        present(alert, animated: true)
    }
    
    private func showWiFiSharingSettings() {
        let isRunning = WiFiSharingServer.shared.isServerRunning()
        let message = isRunning ? 
            "WiFi sharing is currently ACTIVE. You can access photos at:\n\nhttp://\(WiFiSharingServer.shared.getDeviceIPAddress()):8080" :
            "WiFi sharing is currently INACTIVE. Start it to share photos with other devices."
        
        let alert = UIAlertController(title: "WiFi Sharing", message: message, preferredStyle: .alert)
        
        if isRunning {
            alert.addAction(UIAlertAction(title: "Stop Server", style: .destructive) { _ in
                WiFiSharingServer.shared.stopServer()
            })
        } else {
            alert.addAction(UIAlertAction(title: "Start Server", style: .default) { _ in
                WiFiSharingServer.shared.startServer()
            })
        }
        
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
    
    private func showStorageInfo() {
        let documentsPath = PhotoManager.shared.getDocumentsDirectory()
        
        // Calculate storage usage (simplified)
        var totalSize: Int64 = 0
        if let enumerator = FileManager.default.enumerator(at: documentsPath, includingPropertiesForKeys: [.fileSizeKey]) {
            for case let fileURL as URL in enumerator {
                if let fileSize = try? fileURL.resourceValues(forKeys: [.fileSizeKey]).fileSize {
                    totalSize += Int64(fileSize)
                }
            }
        }
        
        let sizeInMB = Double(totalSize) / 1024.0 / 1024.0
        let message = String(format: "PhotoShare Studio is using %.1f MB of storage.\n\nThis includes all photos, albums, and app data.", sizeInMB)
        
        let alert = UIAlertController(title: "Storage Usage", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
    
    private func showSubscriptionInfo() {
        let user = AuthManager.shared.getCurrentUser() ?? "User"
        let message = "Hello \(user)!\n\nPremium Features:\n• Unlimited photo storage\n• Advanced sharing options\n• Priority sync\n• Ad-free experience\n\nStatus: Active (Expires Dec 2024)"
        
        let alert = UIAlertController(title: "Premium Subscription", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Manage", style: .default) { _ in
            // Would open subscription management
        })
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}