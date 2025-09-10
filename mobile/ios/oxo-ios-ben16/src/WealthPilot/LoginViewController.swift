//
//  LoginViewController.swift
//  WealthPilot
//
//  Created by Ostorlab Ostorlab on 9/10/25.
//

import UIKit

class LoginViewController: UIViewController {
    
    @IBOutlet weak var logoImageView: UIImageView!
    @IBOutlet weak var appNameLabel: UILabel!
    @IBOutlet weak var taglineLabel: UILabel!
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var loginButton: UIButton!
    @IBOutlet weak var errorLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }
    
    private func setupUI() {
        view.backgroundColor = UIColor.systemBackground
        
        appNameLabel.text = "WealthPilot"
        appNameLabel.font = UIFont.systemFont(ofSize: 32, weight: .bold)
        appNameLabel.textColor = UIColor.systemBlue
        
        taglineLabel.text = "Navigate Your Financial Future"
        taglineLabel.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        taglineLabel.textColor = UIColor.systemGray
        
        emailTextField.placeholder = "Email Address"
        emailTextField.keyboardType = .emailAddress
        emailTextField.autocapitalizationType = .none
        emailTextField.autocorrectionType = .no
        emailTextField.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        emailTextField.layer.borderWidth = 1
        emailTextField.layer.borderColor = UIColor.systemGray4.cgColor
        emailTextField.layer.cornerRadius = 8
        emailTextField.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 15, height: 0))
        emailTextField.leftViewMode = .always
        
        passwordTextField.placeholder = "Password"
        passwordTextField.isSecureTextEntry = true
        passwordTextField.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        passwordTextField.layer.borderWidth = 1
        passwordTextField.layer.borderColor = UIColor.systemGray4.cgColor
        passwordTextField.layer.cornerRadius = 8
        passwordTextField.leftView = UIView(frame: CGRect(x: 0, y: 0, width: 15, height: 0))
        passwordTextField.leftViewMode = .always
        
        loginButton.setTitle("Sign In to Portfolio", for: .normal)
        loginButton.backgroundColor = UIColor.systemBlue
        loginButton.setTitleColor(.white, for: .normal)
        loginButton.layer.cornerRadius = 8
        loginButton.titleLabel?.font = UIFont.systemFont(ofSize: 18, weight: .semibold)
        
        errorLabel.text = ""
        errorLabel.textColor = UIColor.systemRed
        errorLabel.font = UIFont.systemFont(ofSize: 14, weight: .medium)
        errorLabel.numberOfLines = 2
        errorLabel.textAlignment = .center
        
        logoImageView.backgroundColor = UIColor.systemBlue.withAlphaComponent(0.1)
        logoImageView.layer.cornerRadius = 25
        logoImageView.contentMode = .center
    }
    
    @IBAction func loginButtonTapped(_ sender: UIButton) {
        guard let email = emailTextField.text, !email.isEmpty else {
            showError("Please enter your email address")
            return
        }
        
        guard let password = passwordTextField.text, !password.isEmpty else {
            showError("Please enter your password")
            return
        }
        
        loginButton.isEnabled = false
        loginButton.setTitle("Signing In...", for: .normal)
        
        AuthenticationService.shared.authenticateUser(email: email, password: password) { [weak self] result in
            self?.loginButton.isEnabled = true
            self?.loginButton.setTitle("Sign In to Portfolio", for: .normal)
            
            switch result {
            case .success(_):
                self?.performSegue(withIdentifier: "showDashboard", sender: nil)
            case .failure(let error):
                self?.showError(error)
                self?.passwordTextField.text = ""
            }
        }
    }
    
    private func showError(_ message: String) {
        errorLabel.text = message
        errorLabel.isHidden = false
        
        UIView.animate(withDuration: 0.3) {
            self.emailTextField.layer.borderColor = UIColor.systemRed.cgColor
            self.passwordTextField.layer.borderColor = UIColor.systemRed.cgColor
        }
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            UIView.animate(withDuration: 0.3) {
                self.errorLabel.isHidden = true
                self.emailTextField.layer.borderColor = UIColor.systemGray4.cgColor
                self.passwordTextField.layer.borderColor = UIColor.systemGray4.cgColor
            }
        }
    }
}