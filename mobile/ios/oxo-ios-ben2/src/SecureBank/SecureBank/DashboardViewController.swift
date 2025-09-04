//
//  DashboardViewController.swift
//  SecureBank
//
//  Created by Ostorlab Ostorlab on 9/4/25.
//

import UIKit

class DashboardViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    private func setupUI() {
        title = "Dashboard"
        view.backgroundColor = .systemBackground
        
        let scrollView = UIScrollView()
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(scrollView)
        
        let contentView = UIView()
        contentView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(contentView)
        
        let welcomeLabel = UILabel()
        welcomeLabel.text = "Welcome to SecureBank"
        welcomeLabel.font = UIFont.boldSystemFont(ofSize: 24)
        welcomeLabel.textAlignment = .center
        welcomeLabel.translatesAutoresizingMaskIntoConstraints = false
        
        let balanceCard = createBalanceCard()
        let recentTransactionsLabel = createSectionLabel(text: "Recent Transactions")
        let transactionsList = createTransactionsList()
        
        let quickActionsLabel = createSectionLabel(text: "Quick Actions")
        let profileButton = createActionButton(title: "My Profile", action: #selector(profileTapped))
        let settingsButton = createActionButton(title: "Settings", action: #selector(settingsTapped))
        
        let stackView = UIStackView(arrangedSubviews: [
            welcomeLabel, balanceCard, recentTransactionsLabel, transactionsList,
            quickActionsLabel, profileButton, settingsButton
        ])
        stackView.axis = .vertical
        stackView.spacing = 20
        stackView.translatesAutoresizingMaskIntoConstraints = false
        
        contentView.addSubview(stackView)
        
        NSLayoutConstraint.activate([
            scrollView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            scrollView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            
            contentView.topAnchor.constraint(equalTo: scrollView.topAnchor),
            contentView.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor),
            contentView.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor),
            contentView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            contentView.widthAnchor.constraint(equalTo: scrollView.widthAnchor),
            
            stackView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 20),
            stackView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            stackView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            stackView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -20)
        ])
    }
    
    private func createBalanceCard() -> UIView {
        let card = UIView()
        card.backgroundColor = .systemBlue
        card.layer.cornerRadius = 12
        card.translatesAutoresizingMaskIntoConstraints = false
        
        let balanceLabel = UILabel()
        balanceLabel.text = "Account Balance"
        balanceLabel.textColor = .white
        balanceLabel.font = UIFont.systemFont(ofSize: 16)
        
        let amountLabel = UILabel()
        amountLabel.text = "$12,345.67"
        amountLabel.textColor = .white
        amountLabel.font = UIFont.boldSystemFont(ofSize: 32)
        
        let stackView = UIStackView(arrangedSubviews: [balanceLabel, amountLabel])
        stackView.axis = .vertical
        stackView.alignment = .center
        stackView.spacing = 8
        stackView.translatesAutoresizingMaskIntoConstraints = false
        
        card.addSubview(stackView)
        
        NSLayoutConstraint.activate([
            card.heightAnchor.constraint(equalToConstant: 120),
            stackView.centerXAnchor.constraint(equalTo: card.centerXAnchor),
            stackView.centerYAnchor.constraint(equalTo: card.centerYAnchor)
        ])
        
        return card
    }
    
    private func createSectionLabel(text: String) -> UILabel {
        let label = UILabel()
        label.text = text
        label.font = UIFont.boldSystemFont(ofSize: 20)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }
    
    private func createTransactionsList() -> UIView {
        let container = UIView()
        container.backgroundColor = .secondarySystemBackground
        container.layer.cornerRadius = 8
        container.translatesAutoresizingMaskIntoConstraints = false
        
        let transactions = [
            "STARBUCKS #47291 - $4.99",
            "SHELL 08547412 - $42.50", 
            "WALMART SUPERCENTER - $87.23",
            "AMAZON.COM AMZN.COM/BILL - $29.99",
            "CHASE CREDIT CRD AUTOPAY - $156.78"
        ]
        
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.spacing = 12
        stackView.translatesAutoresizingMaskIntoConstraints = false
        
        for (index, transaction) in transactions.enumerated() {
            let button = UIButton(type: .system)
            button.setTitle(transaction, for: .normal)
            button.setTitleColor(.label, for: .normal)
            button.titleLabel?.font = UIFont.systemFont(ofSize: 16)
            button.contentHorizontalAlignment = .leading
            button.tag = index
            button.addTarget(self, action: #selector(transactionTapped(_:)), for: .touchUpInside)
            stackView.addArrangedSubview(button)
        }
        
        container.addSubview(stackView)
        
        NSLayoutConstraint.activate([
            stackView.topAnchor.constraint(equalTo: container.topAnchor, constant: 16),
            stackView.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: 16),
            stackView.trailingAnchor.constraint(equalTo: container.trailingAnchor, constant: -16),
            stackView.bottomAnchor.constraint(equalTo: container.bottomAnchor, constant: -16)
        ])
        
        return container
    }
    
    private func createActionButton(title: String, action: Selector) -> UIButton {
        let button = UIButton(type: .system)
        button.setTitle(title, for: .normal)
        button.backgroundColor = .systemBlue
        button.setTitleColor(.white, for: .normal)
        button.titleLabel?.font = UIFont.systemFont(ofSize: 18)
        button.layer.cornerRadius = 8
        button.translatesAutoresizingMaskIntoConstraints = false
        button.addTarget(self, action: action, for: .touchUpInside)
        
        NSLayoutConstraint.activate([
            button.heightAnchor.constraint(equalToConstant: 50)
        ])
        
        return button
    }
    
    @objc private func profileTapped() {
        let currentUserId = UserDefaults.standard.string(forKey: "currentUserId") ?? "123"
        navigateToProfile(userId: currentUserId)
    }
    
    @objc private func settingsTapped() {
        let settingsVC = SettingsViewController()
        navigationController?.pushViewController(settingsVC, animated: true)
    }
    
    func navigateToProfile(userId: String) {
        let profileVC = ProfileViewController()
        profileVC.userId = userId
        navigationController?.pushViewController(profileVC, animated: true)
    }
    
    @objc private func transactionTapped(_ sender: UIButton) {
        let transactions = [
            "STARBUCKS #47291 - $4.99",
            "SHELL 08547412 - $42.50", 
            "WALMART SUPERCENTER - $87.23",
            "AMAZON.COM AMZN.COM/BILL - $29.99",
            "CHASE CREDIT CRD AUTOPAY - $156.78"
        ]
        
        let details = [
            "Date: Sept 3, 2025\nTime: 8:47 AM\nLocation: Downtown Seattle\nCategory: Food & Dining",
            "Date: Sept 2, 2025\nTime: 6:12 PM\nLocation: I-5 Northbound\nCategory: Gas & Automotive",
            "Date: Sept 1, 2025\nTime: 2:30 PM\nLocation: Bellevue Square\nCategory: Groceries",
            "Date: Aug 31, 2025\nTime: 11:45 PM\nLocation: Online Purchase\nCategory: Shopping",
            "Date: Sept 1, 2025\nTime: 12:01 AM\nLocation: Automated Payment\nCategory: Credit Card Payment"
        ]
        
        let transaction = transactions[sender.tag]
        let detail = details[sender.tag]
        
        let alert = UIAlertController(title: "Transaction Details", message: "\(transaction)\n\n\(detail)", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Close", style: .default))
        present(alert, animated: true)
    }
}