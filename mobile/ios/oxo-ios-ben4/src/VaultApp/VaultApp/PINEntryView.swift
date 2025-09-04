// File: PINEntryView.swift
import SwiftUI

struct PINEntryView: View {
    @EnvironmentObject var authStore: AuthStore
    @State private var enteredPIN = ""
    @State private var showingAlert = false
    @State private var alertMessage = ""

    var body: some View {
        VStack(spacing: 20) {
            Text("Vault App")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            Text("Enter your 4-digit PIN")
                .font(.headline)
                .foregroundColor(.secondary)

            // PIN input field
            SecureField("0000", text: $enteredPIN)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .keyboardType(.numberPad) // Show number pad
                .multilineTextAlignment(.center)
                .padding(.horizontal, 50)
                .onChange(of: enteredPIN) { oldValue, newValue in
                    // Limit input to 4 digits
                    if newValue.count > 4 {
                        enteredPIN = String(newValue.prefix(4))
                    }
                }

            // Unlock button
            Button(action: attemptUnlock) {
                Text("Unlock")
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .controlSize(.large)
            .padding(.horizontal, 50)
            .disabled(enteredPIN.count != 4) // Disable if not 4 digits

            // This displays the remaining attempts, but doesn't enforce them.
            Text("Attempts remaining: \(authStore.remainingAttempts)")
                .font(.caption)
                .foregroundColor(.secondary)

        }
        .padding()
        .alert("Access Denied", isPresented: $showingAlert) {
            Button("Try Again", role: .cancel) {
                // VULNERABILITY: Clear the field and allow infinite retries.
                enteredPIN = ""
            }
        } message: {
            Text(alertMessage)
        }
    }

    func attemptUnlock() {
        if authStore.validatePIN(enteredPIN) {
            // Success! Handled automatically by the authStore state change
        } else {
            // Show an alert, but don't lock the user out.
            alertMessage = "Incorrect PIN. You have \(authStore.remainingAttempts) attempts left."
            showingAlert = true
        }
    }
}

#Preview {
    PINEntryView()
        .environmentObject(AuthStore())
}
