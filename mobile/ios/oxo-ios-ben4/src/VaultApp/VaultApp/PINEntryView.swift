import SwiftUI

struct PINEntryView: View {
    @EnvironmentObject var authStore: AuthStore
    @State private var enteredPIN = ""
    @State private var showWrongPassword = false
    
    var body: some View {
        VStack(spacing: 20) {
            Text("Secure Vault")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            Text("Enter 4-digit PIN")
                .font(.headline)
            
            HStack(spacing: 10) {
                ForEach(0..<4) { index in
                    Circle()
                        .stroke(Color.blue, lineWidth: 2)
                        .frame(width: 20, height: 20)
                        .overlay(
                            enteredPIN.count > index ? 
                            Circle().fill(Color.blue) : nil
                        )
                }
            }
            .padding(.vertical, 20)
            
            if showWrongPassword {
                Text("Wrong password")
                    .foregroundColor(.red)
                    .font(.subheadline)
            }
            
            Spacer()
            
            // Numeric keypad
            LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 3), spacing: 20) {
                ForEach(1...9, id: \.self) { number in
                    Button(action: {
                        if enteredPIN.count < 4 {
                            enteredPIN += "\(number)"
                        }
                    }) {
                        Text("\(number)")
                            .font(.title)
                            .frame(width: 70, height: 70)
                            .background(Color.blue.opacity(0.1))
                            .clipShape(Circle())
                    }
                }
                
                // Empty space where Face ID button was
                Color.clear
                    .frame(width: 70, height: 70)
                
                Button(action: {
                    if enteredPIN.count < 4 {
                        enteredPIN += "0"
                    }
                }) {
                    Text("0")
                        .font(.title)
                        .frame(width: 70, height: 70)
                        .background(Color.blue.opacity(0.1))
                        .clipShape(Circle())
                }
                
                Button(action: {
                    if !enteredPIN.isEmpty {
                        enteredPIN.removeLast()
                    }
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
                let isValid = authStore.validatePIN(enteredPIN)
                if !isValid {
                    showWrongPassword = true
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        showWrongPassword = false
                    }
                }
                enteredPIN = ""
            }) {
                Text("Unlock")
                    .font(.headline)
                    .foregroundColor(.white)
                    .padding(.vertical, 15)
                    .frame(maxWidth: .infinity)
                    .background(Color.blue)
                    .cornerRadius(10)
            }
            .padding(.horizontal)
            .padding(.bottom, 30)
        }
        .padding()
    }
}

struct PINEntryView_Previews: PreviewProvider {
    static var previews: some View {
        PINEntryView()
            .environmentObject(AuthStore())
    }
}
