// File: VaultView.swift
import SwiftUI

struct VaultView: View {
    @EnvironmentObject var authStore: AuthStore

    var body: some View {
        VStack(spacing: 20) {
            Image(systemName: "lock.open.fill")
                .font(.system(size: 60))
                .foregroundColor(.green)
            
            Text("Vault Unlocked")
                .font(.largeTitle)
                .fontWeight(.bold)
                .foregroundColor(.green)

            Divider().padding(.vertical)

            VStack(alignment: .leading, spacing: 10) {
                Text("Sensitive Information:").font(.headline)
                Text("• Email: user@company.com")
                Text("• API Token: abc123-xyz456-789")
                Text("• Internal ID: 789123")
            }
            .padding()
            .background(Color.gray.opacity(0.1))
            .cornerRadius(10)

            Spacer()

            Button(action: {
                // Lock the vault again
                authStore.isAuthenticated = false
            }) {
                Text("Lock Vault")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.bordered)
            .controlSize(.large)
            .padding(.horizontal)
        }
        .padding()
    }
}

#Preview {
    VaultView()
        .environmentObject(AuthStore())
}
