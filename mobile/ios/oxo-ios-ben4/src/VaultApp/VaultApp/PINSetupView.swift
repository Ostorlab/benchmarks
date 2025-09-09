import SwiftUI

struct PINSetupView: View {
    @EnvironmentObject var authStore: AuthStore
    @State private var enteredPIN = ""
    @State private var confirmPIN = ""
    @State private var isPINConfirmation = false
    @State private var showError = false
    @State private var errorMessage = ""
    
    var body: some View {
        VStack(spacing: 20) {
            Text("Secure Vault")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            Text(isPINConfirmation ? "Confirm your 4-digit PIN" : "Create a 4-digit PIN")
                .font(.headline)
            
            HStack(spacing: 10) {
                ForEach(0..<4) { index in
                    Circle()
                        .stroke(Color.blue, lineWidth: 2)
                        .frame(width: 20, height: 20)
                        .overlay(
                            (isPINConfirmation ? confirmPIN.count > index : enteredPIN.count > index) ? 
                            Circle().fill(Color.blue) : nil
                        )
                }
            }
            .padding(.vertical, 20)
            
            if showError {
                Text(errorMessage)
                    .foregroundColor(.red)
                    .font(.subheadline)
                    .padding(.bottom, 10)
            }
            
            Spacer()
            
            // Numeric keypad
            LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 3), spacing: 20) {
                ForEach(1...9, id: \.self) { number in
                    Button(action: {
                        addDigit("\(number)")
                    }) {
                        Text("\(number)")
                            .font(.title)
                            .frame(width: 70, height: 70)
                            .background(Color.blue.opacity(0.1))
                            .clipShape(Circle())
                    }
                }
                
                // Empty space
                Color.clear
                    .frame(width: 70, height: 70)
                
                Button(action: {
                    addDigit("0")
                }) {
                    Text("0")
                        .font(.title)
                        .frame(width: 70, height: 70)
                        .background(Color.blue.opacity(0.1))
                        .clipShape(Circle())
                }
                
                Button(action: {
                    deleteDigit()
                }) {
                    Image(systemName: "delete.left")
                        .font(.title)
                        .frame(width: 70, height: 70)
                        .background(Color.red.opacity(0.1))
                        .clipShape(Circle())
                }
            }
            .padding(.horizontal)
            
            Button(action: {
                processPIN()
            }) {
                Text(isPINConfirmation ? "Confirm PIN" : "Next")
                    .font(.headline)
                    .foregroundColor(.white)
                    .padding(.vertical, 15)
                    .frame(maxWidth: .infinity)
                    .background(Color.blue)
                    .cornerRadius(10)
            }
            .padding(.horizontal)
            .padding(.bottom, 30)
            .disabled(isPINConfirmation ? confirmPIN.count < 4 : enteredPIN.count < 4)
        }
        .padding()
    }
    
    private func addDigit(_ digit: String) {
        if isPINConfirmation {
            if confirmPIN.count < 4 {
                confirmPIN += digit
            }
        } else {
            if enteredPIN.count < 4 {
                enteredPIN += digit
            }
        }
    }
    
    private func deleteDigit() {
        if isPINConfirmation {
            if !confirmPIN.isEmpty {
                confirmPIN.removeLast()
            }
        } else {
            if !enteredPIN.isEmpty {
                enteredPIN.removeLast()
            }
        }
    }
    
    private func processPIN() {
        if !isPINConfirmation {
            // First entry completed, move to confirmation
            isPINConfirmation = true
        } else {
            // Confirm PIN matches
            if enteredPIN == confirmPIN {
                let success = authStore.setupPIN(enteredPIN)
                if !success {
                    showError = true
                    errorMessage = "PIN must be 4 digits"
                    isPINConfirmation = false
                    enteredPIN = ""
                    confirmPIN = ""
                }
            } else {
                // PINs don't match
                showError = true
                errorMessage = "PINs don't match. Try again."
                isPINConfirmation = false
                enteredPIN = ""
                confirmPIN = ""
                
                // Hide error after 2 seconds
                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                    showError = false
                }
            }
        }
    }
}

struct PINSetupView_Previews: PreviewProvider {
    static var previews: some View {
        PINSetupView()
            .environmentObject(AuthStore())
    }
}