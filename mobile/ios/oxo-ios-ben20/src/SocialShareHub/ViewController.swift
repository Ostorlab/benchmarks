//
//  ViewController.swift
//  SocialShareHub
//
//  Main login screen with social authentication options

import UIKit

class ViewController: UIViewController {
    
    // MARK: - UI Elements
    @IBOutlet weak var logoImageView: UIImageView!
    @IBOutlet weak var welcomeLabel: UILabel!
    @IBOutlet weak var subtitleLabel: UILabel!
    @IBOutlet weak var facebookLoginButton: UIButton!
    @IBOutlet weak var twitterLoginButton: UIButton!
    
    // MARK: - Properties
    private var currentUser: User?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        startOAuthServer()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        // Always ensure server is running when returning to login screen
        startOAuthServer()
    }
    
    private func startOAuthServer() {
        SocialAPIService.shared.startServer()
        
        // Give server a moment to start up
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            print("✅ OAuth server should be ready for authentication")
        }
    }
    
    // MARK: - UI Setup
    
    private func setupUI() {
        // Configure welcome text
        welcomeLabel.text = "Welcome to SocialShare Hub"
        welcomeLabel.font = UIFont.systemFont(ofSize: 28, weight: .bold)
        welcomeLabel.textColor = .label
        
        subtitleLabel.text = "Connect your social accounts to start managing all your content in one place"
        subtitleLabel.font = UIFont.systemFont(ofSize: 16, weight: .regular)
        subtitleLabel.textColor = .secondaryLabel
        subtitleLabel.numberOfLines = 0
        
        // Configure social login buttons
        setupSocialButton(facebookLoginButton, title: "Continue with Facebook", color: UIColor.systemBlue, image: "f.square.fill")
        setupSocialButton(twitterLoginButton, title: "Continue with Twitter", color: UIColor.systemIndigo, image: "t.square.fill")
    }
    
    private func setupSocialButton(_ button: UIButton, title: String, color: UIColor, image: String) {
        button.setTitle(title, for: .normal)
        button.backgroundColor = color
        button.setTitleColor(.white, for: .normal)
        button.titleLabel?.font = UIFont.systemFont(ofSize: 18, weight: .semibold)
        button.layer.cornerRadius = 12
        button.layer.shadowColor = color.cgColor
        button.layer.shadowOpacity = 0.3
        button.layer.shadowOffset = CGSize(width: 0, height: 2)
        button.layer.shadowRadius = 4
    }
    
    // MARK: - Button Actions
    
    @IBAction func facebookLoginTapped(_ sender: UIButton) {
        showLoadingState(for: sender)
        
        SocialAuthService.shared.authenticateWithFacebook { [weak self] success, user in
            self?.hideLoadingState(for: sender)
            
            if success, let user = user {
                self?.currentUser = user
                // Navigate directly to dashboard - no alert needed for real apps
                self?.navigateToDashboard(user: user)
            } else {
                self?.showErrorMessage(title: "Login Failed", message: "Unable to sign in. Please try again.")
            }
        }
    }
    
    @IBAction func twitterLoginTapped(_ sender: UIButton) {
        showLoadingState(for: sender)
        
        SocialAuthService.shared.authenticateWithTwitter { [weak self] success, user in
            self?.hideLoadingState(for: sender)
            
            if success, let user = user {
                self?.currentUser = user
                // Navigate directly to dashboard - no alert needed for real apps
                self?.navigateToDashboard(user: user)
            } else {
                self?.showErrorMessage(title: "Login Failed", message: "Unable to sign in. Please try again.")
            }
        }
    }
    
    
    // MARK: - UI Feedback
    
    private func showLoadingState(for button: UIButton) {
        button.isEnabled = false
        let originalTitle = button.title(for: .normal)
        button.setTitle("Authenticating...", for: .normal)
        button.tag = originalTitle?.hash ?? 0
    }
    
    private func hideLoadingState(for button: UIButton) {
        button.isEnabled = true
        if button == facebookLoginButton {
            button.setTitle("Continue with Facebook", for: .normal)
        } else if button == twitterLoginButton {
            button.setTitle("Continue with Twitter", for: .normal)
        }
    }
    
    
    private func showErrorMessage(title: String, message: String) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
    
    // MARK: - Navigation
    
    private func navigateToDashboard(user: User) {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        if let dashboardVC = storyboard.instantiateViewController(withIdentifier: "DashboardViewController") as? DashboardViewController {
            dashboardVC.currentUser = user
            navigationController?.pushViewController(dashboardVC, animated: true)
        }
    }
}

