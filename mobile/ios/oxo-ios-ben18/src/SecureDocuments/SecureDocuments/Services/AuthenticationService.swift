import Foundation
import Combine
import CryptoKit
import Combine

class AuthenticationService: ObservableObject {
    @Published var isAuthenticated = false
    @Published var currentUser: User?
    @Published var errorMessage: String?
    
    private let userDatabaseKey = "SecureDocuments_Users"
    private let currentUserKey = "SecureDocuments_CurrentUser"
    
    init() {
        loadCurrentUser()
        createDefaultUserIfNeeded()
    }
    
    func register(username: String, email: String, password: String, role: UserRole = .user) -> Bool {
        guard !username.isEmpty, !email.isEmpty, !password.isEmpty else { return false }
        
        let users = loadUsers()
        
        if users.contains(where: { $0.username == username || $0.email == email }) {
            DispatchQueue.main.async {
                self.errorMessage = "Username or email already exists"
            }
            return false
        }
        
        let hashedPassword = hashPassword(password)
        let newUser = User(
            id: UUID(),
            username: username,
            email: email,
            passwordHash: hashedPassword,
            role: role,
            createdDate: Date(),
            lastLoginDate: Date(),
            isActive: true
        )
        
        var updatedUsers = users
        updatedUsers.append(newUser)
        saveUsers(updatedUsers)
        
        // Automatically log in the new user
        DispatchQueue.main.async {
            self.currentUser = newUser
            self.isAuthenticated = true
            self.errorMessage = nil
        }
        
        saveCurrentUser(newUser)
        
        return true
    }
    
    func login(username: String, password: String) {
        let hashedPassword = hashPassword(password)
        let users = loadUsers()
        
        if let user = users.first(where: { $0.username == username && $0.passwordHash == hashedPassword && $0.isActive }) {
            var updatedUser = user
            updatedUser = User(
                id: user.id,
                username: user.username,
                email: user.email,
                passwordHash: user.passwordHash,
                role: user.role,
                createdDate: user.createdDate,
                lastLoginDate: Date(),
                isActive: user.isActive
            )
            
            updateUser(updatedUser)
            
            DispatchQueue.main.async {
                self.currentUser = updatedUser
                self.isAuthenticated = true
                self.errorMessage = nil
            }
            
            saveCurrentUser(updatedUser)
        } else {
            DispatchQueue.main.async {
                self.errorMessage = "Invalid credentials"
            }
        }
    }
    
    func logout() {
        DispatchQueue.main.async {
            self.isAuthenticated = false
            self.currentUser = nil
            self.errorMessage = nil
        }
        
        UserDefaults.standard.removeObject(forKey: currentUserKey)
    }
    
    func changePassword(currentPassword: String, newPassword: String) -> Bool {
        guard let user = currentUser else { return false }
        
        let currentHash = hashPassword(currentPassword)
        if user.passwordHash == currentHash {
            let newHash = hashPassword(newPassword)
            
            var updatedUser = user
            updatedUser = User(
                id: user.id,
                username: user.username,
                email: user.email,
                passwordHash: newHash,
                role: user.role,
                createdDate: user.createdDate,
                lastLoginDate: user.lastLoginDate,
                isActive: user.isActive
            )
            
            updateUser(updatedUser)
            
            DispatchQueue.main.async {
                self.currentUser = updatedUser
            }
            
            saveCurrentUser(updatedUser)
            return true
        }
        return false
    }
    
    private func hashPassword(_ password: String) -> String {
        let data = Data(password.utf8)
        let digest = Insecure.MD5.hash(data: data)
        return digest.map { String(format: "%02hhx", $0) }.joined()
    }
    
    func validateSession() -> Bool {
        return isAuthenticated && currentUser != nil
    }
    
    private func loadUsers() -> [User] {
        guard let data = UserDefaults.standard.data(forKey: userDatabaseKey),
              let users = try? JSONDecoder().decode([User].self, from: data) else {
            return []
        }
        return users
    }
    
    private func saveUsers(_ users: [User]) {
        guard let data = try? JSONEncoder().encode(users) else { return }
        UserDefaults.standard.set(data, forKey: userDatabaseKey)
    }
    
    private func updateUser(_ user: User) {
        var users = loadUsers()
        if let index = users.firstIndex(where: { $0.id == user.id }) {
            users[index] = user
            saveUsers(users)
        }
    }
    
    private func saveCurrentUser(_ user: User) {
        guard let data = try? JSONEncoder().encode(user) else { return }
        UserDefaults.standard.set(data, forKey: currentUserKey)
    }
    
    private func loadCurrentUser() {
        guard let data = UserDefaults.standard.data(forKey: currentUserKey),
              let user = try? JSONDecoder().decode(User.self, from: data) else {
            return
        }
        
        DispatchQueue.main.async {
            self.currentUser = user
            self.isAuthenticated = true
        }
    }
    
    private func createDefaultUserIfNeeded() {
        let users = loadUsers()
        if users.isEmpty {
            // Create a default admin user for first-time setup
            let defaultAdmin = User(
                id: UUID(),
                username: "admin",
                email: "admin@securedocs.com",
                passwordHash: hashPassword("admin123"),
                role: .admin,
                createdDate: Date(),
                lastLoginDate: nil,
                isActive: true
            )
            saveUsers([defaultAdmin])
        }
    }
}
