//
//  SceneDelegate.swift
//  SecureBank
//
//  Created by Ostorlab Ostorlab on 9/4/25.
//

import UIKit

class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        guard let _ = (scene as? UIWindowScene) else { return }
        
        if let urlContext = connectionOptions.urlContexts.first {
            handleDeepLink(url: urlContext.url)
        }
    }
    
    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        guard let url = URLContexts.first?.url else { return }
        handleDeepLink(url: url)
    }
    
    private func handleDeepLink(url: URL) {
        guard url.scheme == "securebank" else { return }
        guard let host = url.host else { return }
        
        switch host {
        case "profile":
            handleProfileDeepLink(url: url)
        default:
            break
        }
    }
    
    private func handleProfileDeepLink(url: URL) {
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
              let queryItems = components.queryItems else { return }
        
        var userId: String?
        
        for queryItem in queryItems {
            if queryItem.name == "user_id" {
                userId = queryItem.value
                break
            }
        }
        
        guard let targetUserId = userId else { return }
        
        DispatchQueue.main.async {
            self.navigateToProfile(userId: targetUserId)
        }
    }
    
    private func navigateToProfile(userId: String) {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let window = windowScene.windows.first else { return }
        
        let profileVC = ProfileViewController()
        profileVC.userId = userId
        
        if let navController = window.rootViewController?.presentedViewController as? UINavigationController {
            navController.pushViewController(profileVC, animated: true)
        } else if let presented = window.rootViewController?.presentedViewController {
            let navController = UINavigationController(rootViewController: profileVC)
            presented.present(navController, animated: true)
        } else {
            let navController = UINavigationController(rootViewController: profileVC)
            window.rootViewController?.present(navController, animated: true)
        }
    }

    func sceneDidDisconnect(_ scene: UIScene) {
    }

    func sceneDidBecomeActive(_ scene: UIScene) {
    }

    func sceneWillResignActive(_ scene: UIScene) {
    }

    func sceneWillEnterForeground(_ scene: UIScene) {
    }

    func sceneDidEnterBackground(_ scene: UIScene) {
    }

}