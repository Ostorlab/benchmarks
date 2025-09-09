import SwiftUI

struct VaultView: View {
    @EnvironmentObject var authStore: AuthStore
    @EnvironmentObject var secretsStore: SecretsStore
    @State private var showingAddSecret = false
    @State private var showingSettings = false
    
    var body: some View {
        NavigationView {
            List {
                ForEach(secretsStore.secrets) { secret in
                    NavigationLink(destination: SecretDetailView(secret: secret)) {
                        VStack(alignment: .leading) {
                            Text(secret.title)
                                .font(.headline)
                            Text(secret.username)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                .onDelete(perform: secretsStore.deleteSecret)
            }
            .navigationTitle("My Secrets")
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Menu {
                        Button("Lock") {
                            authStore.isAuthenticated = false
                        }
                        
                        Button("Settings") {
                            showingSettings = true
                        }
                    } label: {
                        Image(systemName: "gear")
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { showingAddSecret = true }) {
                        Image(systemName: "plus")
                    }
                }
            }
            .sheet(isPresented: $showingAddSecret) {
                AddSecretView()
            }
            .sheet(isPresented: $showingSettings) {
                SettingsView()
            }
        }
    }
}

struct SettingsView: View {
    @EnvironmentObject var authStore: AuthStore
    @Environment(\.presentationMode) var presentationMode
    @State private var showResetConfirmation = false
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Security")) {
                    Button("Reset PIN") {
                        showResetConfirmation = true
                    }
                    .foregroundColor(.red)
                }
                
                Section(header: Text("About")) {
                    Text("Secure Vault App")
                    Text("Version 1.0")
                }
            }
            .navigationTitle("Settings")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
            }
            .alert(isPresented: $showResetConfirmation) {
                Alert(
                    title: Text("Reset PIN"),
                    message: Text("Are you sure you want to reset your PIN? You will need to create a new PIN to access your vault."),
                    primaryButton: .destructive(Text("Reset")) {
                        authStore.resetPIN()
                        presentationMode.wrappedValue.dismiss()
                    },
                    secondaryButton: .cancel()
                )
            }
        }
    }
}

struct SecretDetailView: View {
    @EnvironmentObject var secretsStore: SecretsStore
    @State var secret: SecretItem
    @State private var isPasswordVisible = false
    
    var body: some View {
        Form {
            Section(header: Text("Credentials")) {
                TextField("Title", text: $secret.title)
                TextField("Username", text: $secret.username)
                
                HStack {
                    if isPasswordVisible {
                        TextField("Password", text: $secret.password)
                    } else {
                        SecureField("Password", text: $secret.password)
                    }
                    
                    Button(action: {
                        isPasswordVisible.toggle()
                    }) {
                        Image(systemName: isPasswordVisible ? "eye.slash" : "eye")
                            .foregroundColor(.blue)
                    }
                }
            }
            
            Section(header: Text("Notes")) {
                TextEditor(text: $secret.notes)
                    .frame(minHeight: 100)
            }
            
            Section {
                Button("Save Changes") {
                    secretsStore.updateSecret(secret)
                }
                .frame(maxWidth: .infinity)
            }
        }
        .navigationTitle("Edit Secret")
    }
}

struct AddSecretView: View {
    @EnvironmentObject var secretsStore: SecretsStore
    @Environment(\.presentationMode) var presentationMode
    @State private var newSecret = SecretItem(title: "", username: "", password: "", notes: "")
    @State private var isPasswordVisible = false
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Credentials")) {
                    TextField("Title", text: $newSecret.title)
                    TextField("Username", text: $newSecret.username)
                    
                    HStack {
                        if isPasswordVisible {
                            TextField("Password", text: $newSecret.password)
                        } else {
                            SecureField("Password", text: $newSecret.password)
                        }
                        
                        Button(action: {
                            isPasswordVisible.toggle()
                        }) {
                            Image(systemName: isPasswordVisible ? "eye.slash" : "eye")
                                .foregroundColor(.blue)
                        }
                    }
                }
                
                Section(header: Text("Notes")) {
                    TextEditor(text: $newSecret.notes)
                        .frame(minHeight: 100)
                }
            }
            .navigationTitle("Add New Secret")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        presentationMode.wrappedValue.dismiss()
                    }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        secretsStore.addSecret(newSecret)
                        presentationMode.wrappedValue.dismiss()
                    }
                    .disabled(newSecret.title.isEmpty || newSecret.password.isEmpty)
                }
            }
        }
    }
}

struct VaultView_Previews: PreviewProvider {
    static var previews: some View {
        VaultView()
            .environmentObject(AuthStore())
            .environmentObject(SecretsStore())
    }
}
