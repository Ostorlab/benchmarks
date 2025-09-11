//
//  DashboardViewController.swift
//  SocialShareHub
//
//  User dashboard showing connected social accounts and management options

import UIKit

class DashboardViewController: UIViewController {
    
    // MARK: - UI Elements  
    @IBOutlet weak var welcomeBackLabel: UILabel!
    @IBOutlet weak var userNameLabel: UILabel!
    @IBOutlet weak var connectedAccountsLabel: UILabel!
    @IBOutlet weak var accountsTableView: UITableView!
    @IBOutlet weak var settingsButton: UIButton!
    @IBOutlet weak var logoutButton: UIButton!
    
    // MARK: - Properties
    var currentUser: User?
    private var mockPosts: [String] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupTableView()
        
        // Show loading state briefly to simulate fetching social media data
        showLoadingState()
    }
    
    private func showLoadingState() {
        connectedAccountsLabel.text = "Loading social accounts..."
        accountsTableView.alpha = 0.5
        
        // Simulate realistic loading time
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.2) {
            self.hideLoadingState()
        }
    }
    
    private func hideLoadingState() {
        guard let user = currentUser else { return }
        
        let accountsList = user.connectedAccounts.isEmpty ? "None" : user.connectedAccounts.joined(separator: ", ")
        connectedAccountsLabel.text = "Connected: \(accountsList)"
        
        UIView.animate(withDuration: 0.3) {
            self.accountsTableView.alpha = 1.0
        }
        
        // Generate realistic posts based on connected accounts
        generateRealisticPosts()
        accountsTableView.reloadData()
    }
    
    private func generateRealisticPosts() {
        guard let user = currentUser else { return }
        
        let basePosts = [
            "Just posted a new photo on \(user.connectedAccounts.first ?? "social media")! 📸",
            "Managing my \(user.connectedAccounts.count) connected accounts is so much easier now ✨",
            "Cross-posted to all my platforms - reach maximized! 🚀",
            "Analytics looking great across all channels 📊",
            "Scheduled 5 posts for this week - time saved! ⏰",
            "Love being able to see all my social activity in one place 💯",
            "Engagement is up 20% since using SocialShare Hub! 📈"
        ]
        
        // Add user-specific posts
        var userPosts = basePosts
        if user.connectedAccounts.contains("Instagram") {
            userPosts.append("New Instagram story just went live! Check it out 📱")
        }
        if user.connectedAccounts.contains("Twitter") {
            userPosts.append("Tweet thread performing really well - great engagement! 🐦")
        }
        if user.connectedAccounts.contains("LinkedIn") {
            userPosts.append("Professional update shared on LinkedIn 💼")
        }
        
        mockPosts = Array(userPosts.prefix(6))
    }
    
    // MARK: - UI Setup
    
    private func setupUI() {
        guard let user = currentUser else { return }
        
        welcomeBackLabel.text = "Welcome back!"
        welcomeBackLabel.font = UIFont.systemFont(ofSize: 24, weight: .bold)
        welcomeBackLabel.textColor = .label
        
        userNameLabel.text = user.name
        userNameLabel.font = UIFont.systemFont(ofSize: 20, weight: .medium)
        userNameLabel.textColor = .secondaryLabel
        
        let accountsList = user.connectedAccounts.isEmpty ? "None" : user.connectedAccounts.joined(separator: ", ")
        connectedAccountsLabel.text = "Connected: \(accountsList)"
        connectedAccountsLabel.font = UIFont.systemFont(ofSize: 16, weight: .regular)
        connectedAccountsLabel.textColor = .tertiaryLabel
        
        // Configure buttons
        settingsButton.setTitle("Account Settings", for: .normal)
        settingsButton.backgroundColor = .systemGray6
        settingsButton.setTitleColor(.label, for: .normal)
        settingsButton.layer.cornerRadius = 8
        
        logoutButton.setTitle("Sign Out", for: .normal)
        logoutButton.backgroundColor = .systemRed
        logoutButton.setTitleColor(.white, for: .normal)
        logoutButton.layer.cornerRadius = 8
        
        // Set up navigation
        self.title = "Dashboard"
        navigationItem.hidesBackButton = true
    }
    
    private func setupTableView() {
        accountsTableView.delegate = self
        accountsTableView.dataSource = self
        accountsTableView.register(UITableViewCell.self, forCellReuseIdentifier: "PostCell")
        accountsTableView.backgroundColor = .systemBackground
        accountsTableView.separatorStyle = .none
    }
    
    // MARK: - Button Actions
    
    @IBAction func settingsButtonTapped(_ sender: UIButton) {
        showSettingsAlert()
    }
    
    @IBAction func logoutButtonTapped(_ sender: UIButton) {
        let alert = UIAlertController(
            title: "Sign Out",
            message: "Are you sure you want to sign out?",
            preferredStyle: .alert
        )
        
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
        alert.addAction(UIAlertAction(title: "Sign Out", style: .destructive) { _ in
            self.performLogout()
        })
        
        present(alert, animated: true)
    }
    
    // MARK: - Private Methods
    
    private func showSettingsAlert() {
        guard let user = currentUser else { return }
        
        let message = """
        Account Details:
        
        Name: \(user.name)
        Email: \(user.email)
        Provider: \(user.provider.capitalized)
        User ID: \(user.id)
        
        Connected Accounts:
        \(user.connectedAccounts.isEmpty ? "None" : user.connectedAccounts.joined(separator: "\n"))
        """
        
        let alert = UIAlertController(
            title: "Account Information",
            message: message,
            preferredStyle: .alert
        )
        
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
    
    private func performLogout() {
        // Stop the OAuth server when logging out
        SocialAPIService.shared.stopServer()
        
        // Navigate back to login screen
        navigationController?.popToRootViewController(animated: true)
    }
}

// MARK: - TableView DataSource & Delegate

extension DashboardViewController: UITableViewDataSource, UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return mockPosts.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "PostCell", for: indexPath)
        
        // Configure cell appearance
        cell.textLabel?.text = mockPosts[indexPath.row]
        cell.textLabel?.font = UIFont.systemFont(ofSize: 16)
        cell.textLabel?.numberOfLines = 0
        cell.backgroundColor = .systemGray6
        cell.layer.cornerRadius = 8
        cell.selectionStyle = .none
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableView.automaticDimension
    }
    
    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return 60
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "Recent Social Activity"
    }
}