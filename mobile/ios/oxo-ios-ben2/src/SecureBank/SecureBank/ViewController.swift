//
//  ViewController.swift
//  SecureBank
//
//  Created by Ostorlab Ostorlab on 9/4/25.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        showLogin()
    }
    
    private func showLogin() {
        let loginVC = LoginViewController()
        loginVC.modalPresentationStyle = .fullScreen
        present(loginVC, animated: true)
    }
}