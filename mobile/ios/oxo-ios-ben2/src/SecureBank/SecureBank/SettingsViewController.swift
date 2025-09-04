//
//  SettingsViewController.swift
//  SecureBank
//
//  Created by Ostorlab Ostorlab on 9/4/25.
//

import UIKit

class SettingsViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    private func setupUI() {
        title = "Settings"
        view.backgroundColor = .systemBackground
        
        let tableView = UITableView(frame: .zero, style: .grouped)
        tableView.delegate = self
        tableView.dataSource = self
        tableView.translatesAutoresizingMaskIntoConstraints = false
        
        view.addSubview(tableView)
        
        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }
    
    private func logout() {
        UserDefaults.standard.set(false, forKey: "isLoggedIn")
        UserDefaults.standard.removeObject(forKey: "currentUserId")
        
        let alert = UIAlertController(title: "Logged Out", message: "You have been logged out successfully", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default) { _ in
            self.view.window?.rootViewController?.dismiss(animated: true)
        })
        present(alert, animated: true)
    }
    
    @objc private func mfaToggled(_ sender: UISwitch) {
        UserDefaults.standard.set(sender.isOn, forKey: "mfaEnabled")
        showSettingAlert(title: "Multi-Factor Authentication", message: sender.isOn ? "Enabled" : "Disabled")
    }
    
    @objc private func biometricToggled(_ sender: UISwitch) {
        UserDefaults.standard.set(sender.isOn, forKey: "biometricEnabled")
        showSettingAlert(title: "Biometric Security", message: sender.isOn ? "Enabled" : "Disabled")
    }
    
    private func showAccountInfo() {
        let alert = UIAlertController(title: "Account Information", message: "John Smith\nAccount: ****-****-****-1234\nMember since: January 2019\nAccount Type: Premium Checking", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Close", style: .default))
        present(alert, animated: true)
    }
    
    private func showSettingAlert(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}

extension SettingsViewController: UITableViewDataSource, UITableViewDelegate {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 4
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch section {
        case 0: return 2
        case 1: return 4  
        case 2: return 2
        case 3: return 1
        default: return 0
        }
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        switch section {
        case 0: return "Account"
        case 1: return "Advanced Security"
        case 2: return "Privacy & Protection"
        case 3: return "App"
        default: return nil
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell(style: .value1, reuseIdentifier: nil)
        
        switch indexPath.section {
        case 0:
            if indexPath.row == 0 {
                cell.textLabel?.text = "Change PIN"
                cell.accessoryType = .disclosureIndicator
            } else {
                cell.textLabel?.text = "Account Information"
                cell.accessoryType = .disclosureIndicator
            }
        case 1:
            if indexPath.row == 0 {
                cell.textLabel?.text = "256-bit AES Encryption"
                cell.detailTextLabel?.text = "Enabled"
                let checkmark = UILabel()
                checkmark.text = "✓"
                checkmark.textColor = .systemGreen
                cell.accessoryView = checkmark
            } else if indexPath.row == 1 {
                cell.textLabel?.text = "Multi-Factor Authentication"
                let switchView = UISwitch()
                switchView.isOn = UserDefaults.standard.bool(forKey: "mfaEnabled")
                switchView.addTarget(self, action: #selector(mfaToggled(_:)), for: .valueChanged)
                cell.accessoryView = switchView
            } else if indexPath.row == 2 {
                cell.textLabel?.text = "Biometric Security"
                let switchView = UISwitch()
                switchView.isOn = UserDefaults.standard.bool(forKey: "biometricEnabled")
                switchView.addTarget(self, action: #selector(biometricToggled(_:)), for: .valueChanged)
                cell.accessoryView = switchView
            } else {
                cell.textLabel?.text = "Advanced Threat Protection"
                cell.detailTextLabel?.text = "Active"
                let checkmark = UILabel()
                checkmark.text = "🛡️"
                cell.accessoryView = checkmark
            }
        case 2:
            if indexPath.row == 0 {
                cell.textLabel?.text = "Secure Data Transmission"
                cell.detailTextLabel?.text = "TLS 1.3"
                let checkmark = UILabel()
                checkmark.text = "🔒"
                cell.accessoryView = checkmark
            } else {
                cell.textLabel?.text = "Privacy Controls"
                cell.accessoryType = .disclosureIndicator
            }
        case 3:
            cell.textLabel?.text = "Logout"
            cell.textLabel?.textColor = .systemRed
        default:
            break
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        if indexPath.section == 0 && indexPath.row == 0 {
            showChangePINMessage()
        } else if indexPath.section == 0 && indexPath.row == 1 {
            showAccountInfo()
        } else if indexPath.section == 2 && indexPath.row == 1 {
            showPrivacyControls()
        } else if indexPath.section == 3 && indexPath.row == 0 {
            logout()
        }
    }
    
    private func showChangePINMessage() {
        let alert = UIAlertController(title: "Change PIN", message: "PIN changes are currently disabled during business hours for security reasons. Please try again after 6 PM EST or visit a branch location.", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
    
    private func showPrivacyControls() {
        let alert = UIAlertController(title: "Privacy Controls", message: "Data Sharing: Disabled\nMarketing Emails: Disabled\nLocation Services: Enabled\nAnalytics: Minimal\n\nAll privacy settings are managed through our secure web portal for enhanced security.", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}