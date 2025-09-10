//
//  MedicalRecordsApp.swift
//  MedicalRecords
//
//  Created by Alaeddine Mesbahi on 9/10/25.
//

import SwiftUI

@main
struct MedicalRecordsApp: App {
    @StateObject private var sessionManager = SessionManager()
    
    var body: some Scene {
        WindowGroup {
            if sessionManager.isLoggedIn {
                ContentView()
                    .environmentObject(sessionManager)
            } else {
                LoginView()
                    .environmentObject(sessionManager)
            }
        }
    }
}
