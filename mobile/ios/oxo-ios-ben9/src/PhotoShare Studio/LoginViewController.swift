//
//  LoginViewController.swift
//  PhotoShare Studio
//
//  Login screen for authentication
//

import UIKit

class LoginViewController: UIViewController {
    
    @IBOutlet weak var usernameTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var loginButton: UIButton!
    @IBOutlet weak var statusLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    private func setupUI() {
        title = "Sign In"
        view.backgroundColor = UIColor.systemBackground
        
        // Pre-fill for demo purposes
        usernameTextField.text = "ostorlab"
        passwordTextField.text = "ostorlab"
        
        loginButton.backgroundColor = UIColor.systemBlue
        loginButton.setTitleColor(UIColor.white, for: .normal)
        loginButton.layer.cornerRadius = 8
        
        statusLabel.textColor = UIColor.systemGray
        statusLabel.text = "Enter credentials to access PhotoShare Studio"
        
        // Add targets
        loginButton.addTarget(self, action: #selector(loginTapped), for: .touchUpInside)
    }
    
    @objc private func loginTapped() {
        guard let username = usernameTextField.text, !username.isEmpty,
              let password = passwordTextField.text, !password.isEmpty else {
            showError("Please enter both username and password")
            return
        }
        
        if AuthManager.shared.login(username: username, password: password) {
            statusLabel.textColor = UIColor.systemGreen
            statusLabel.text = "Login successful!"
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                self.dismiss(animated: true) {
                    // Notify main app about login
                    NotificationCenter.default.post(name: .userDidLogin, object: nil)
                }
            }
        } else {
            showError("Invalid credentials. Try ostorlab/ostorlab")
        }
    }
    
    private func showError(_ message: String) {
        statusLabel.textColor = UIColor.systemRed
        statusLabel.text = message
        
        // Shake animation
        let animation = CAKeyframeAnimation(keyPath: "transform.translation.x")
        animation.timingFunction = CAMediaTimingFunction(name: .linear)
        animation.duration = 0.6
        animation.values = [-20.0, 20.0, -20.0, 20.0, -10.0, 10.0, -5.0, 5.0, 0.0]
        view.layer.add(animation, forKey: "shake")
    }
}

extension Notification.Name {
    static let userDidLogin = Notification.Name("userDidLogin")
    static let userDidLogout = Notification.Name("userDidLogout")
}