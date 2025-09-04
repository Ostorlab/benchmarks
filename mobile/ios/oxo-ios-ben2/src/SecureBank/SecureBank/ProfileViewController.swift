//
//  ProfileViewController.swift
//  SecureBank
//
//  Created by Ostorlab Ostorlab on 9/4/25.
//

import UIKit

struct UserProfile {
    let userId: String
    let name: String
    let email: String
    let phone: String
    let address: String
    let accountNumber: String
    let balance: String
    let ssn: String
}

class ProfileViewController: UIViewController {
    
    var userId: String = "123"
    
    private let userProfiles: [String: UserProfile] = [
        "123": UserProfile(
            userId: "123",
            name: "John Smith",
            email: "john.smith@email.com", 
            phone: "+1 (555) 123-4567",
            address: "123 Main St, New York, NY 10001",
            accountNumber: "****-****-****-1234",
            balance: "$12,345.67",
            ssn: "***-**-1234"
        ),
        "456": UserProfile(
            userId: "456", 
            name: "Sarah Johnson",
            email: "sarah.johnson@email.com",
            phone: "+1 (555) 987-6543", 
            address: "456 Oak Ave, Los Angeles, CA 90210",
            accountNumber: "****-****-****-5678",
            balance: "$8,976.32",
            ssn: "***-**-5678"
        ),
        "789": UserProfile(
            userId: "789",
            name: "Mike Wilson", 
            email: "mike.wilson@email.com",
            phone: "+1 (555) 555-0199",
            address: "789 Pine St, Chicago, IL 60601", 
            accountNumber: "****-****-****-9012",
            balance: "$25,678.90",
            ssn: "***-**-9012"
        ),
        "999": UserProfile(
            userId: "999",
            name: "Admin User",
            email: "admin@securebank.com",
            phone: "+1 (555) 000-0001",
            address: "1 Bank Plaza, New York, NY 10005",
            accountNumber: "****-****-****-0001", 
            balance: "$999,999.99",
            ssn: "***-**-0001"
        )
    ]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    private func setupUI() {
        title = "Profile"
        view.backgroundColor = .systemBackground
        
        guard let profile = userProfiles[userId] else {
            showErrorProfile()
            return
        }
        
        let scrollView = UIScrollView()
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(scrollView)
        
        let contentView = UIView()
        contentView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(contentView)
        
        let profileImageView = UIImageView()
        profileImageView.backgroundColor = .systemBlue
        profileImageView.layer.cornerRadius = 50
        profileImageView.clipsToBounds = true
        profileImageView.translatesAutoresizingMaskIntoConstraints = false
        
        let nameLabel = UILabel()
        nameLabel.text = profile.name
        nameLabel.font = UIFont.boldSystemFont(ofSize: 24)
        nameLabel.textAlignment = .center
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        
        let userIdLabel = UILabel()
        userIdLabel.text = "User ID: \(profile.userId)"
        userIdLabel.font = UIFont.systemFont(ofSize: 14)
        userIdLabel.textColor = .secondaryLabel
        userIdLabel.textAlignment = .center
        userIdLabel.translatesAutoresizingMaskIntoConstraints = false
        
        let infoStackView = UIStackView()
        infoStackView.axis = .vertical
        infoStackView.spacing = 16
        infoStackView.translatesAutoresizingMaskIntoConstraints = false
        
        let infoItems = [
            ("Email", profile.email),
            ("Phone", profile.phone), 
            ("Address", profile.address),
            ("Account", profile.accountNumber),
            ("Balance", profile.balance),
            ("SSN", profile.ssn)
        ]
        
        for (title, value) in infoItems {
            let infoView = createInfoView(title: title, value: value)
            infoStackView.addArrangedSubview(infoView)
        }
        
        contentView.addSubview(profileImageView)
        contentView.addSubview(nameLabel)
        contentView.addSubview(userIdLabel)
        contentView.addSubview(infoStackView)
        
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
            
            profileImageView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 30),
            profileImageView.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            profileImageView.widthAnchor.constraint(equalToConstant: 100),
            profileImageView.heightAnchor.constraint(equalToConstant: 100),
            
            nameLabel.topAnchor.constraint(equalTo: profileImageView.bottomAnchor, constant: 16),
            nameLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            nameLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            
            userIdLabel.topAnchor.constraint(equalTo: nameLabel.bottomAnchor, constant: 4),
            userIdLabel.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            userIdLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            
            infoStackView.topAnchor.constraint(equalTo: userIdLabel.bottomAnchor, constant: 30),
            infoStackView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 20),
            infoStackView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -20),
            infoStackView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -30)
        ])
    }
    
    private func createInfoView(title: String, value: String) -> UIView {
        let container = UIView()
        container.backgroundColor = .secondarySystemBackground
        container.layer.cornerRadius = 8
        container.translatesAutoresizingMaskIntoConstraints = false
        
        let titleLabel = UILabel()
        titleLabel.text = title
        titleLabel.font = UIFont.boldSystemFont(ofSize: 16)
        titleLabel.textColor = .label
        
        let valueLabel = UILabel()
        valueLabel.text = value
        valueLabel.font = UIFont.systemFont(ofSize: 16)
        valueLabel.textColor = .secondaryLabel
        valueLabel.numberOfLines = 0
        
        let stackView = UIStackView(arrangedSubviews: [titleLabel, valueLabel])
        stackView.axis = .vertical
        stackView.spacing = 4
        stackView.translatesAutoresizingMaskIntoConstraints = false
        
        container.addSubview(stackView)
        
        NSLayoutConstraint.activate([
            stackView.topAnchor.constraint(equalTo: container.topAnchor, constant: 12),
            stackView.leadingAnchor.constraint(equalTo: container.leadingAnchor, constant: 16),
            stackView.trailingAnchor.constraint(equalTo: container.trailingAnchor, constant: -16),
            stackView.bottomAnchor.constraint(equalTo: container.bottomAnchor, constant: -12)
        ])
        
        return container
    }
    
    private func showErrorProfile() {
        title = "Profile Not Found"
        
        let errorLabel = UILabel()
        errorLabel.text = "User profile not found for ID: \(userId)"
        errorLabel.textAlignment = .center
        errorLabel.font = UIFont.systemFont(ofSize: 18)
        errorLabel.textColor = .systemRed
        errorLabel.translatesAutoresizingMaskIntoConstraints = false
        
        view.addSubview(errorLabel)
        
        NSLayoutConstraint.activate([
            errorLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            errorLabel.centerYAnchor.constraint(equalTo: view.centerYAnchor)
        ])
    }
}