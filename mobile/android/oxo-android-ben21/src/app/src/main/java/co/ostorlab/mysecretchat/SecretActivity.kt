package co.ostorlab.mysecretchat

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecretActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secret)

        val userSecret = getUserSecretFromStorage()
        val secretTextView = findViewById<TextView>(R.id.secret_text)
        secretTextView.text = "Jetons utilisateur sensibles : $userSecret"

        // Exemple : Exécution d'une opération privilégiée
        performPrivilegedAction()
    }

    private fun getUserSecretFromStorage(): String {
        val prefs: SharedPreferences = getSharedPreferences("secret_prefs", MODE_PRIVATE)
        return prefs.getString("user_secret_token", "Aucun jeton trouvé") ?: "Aucun jeton trouvé"
    }

    private fun performPrivilegedAction() {
        Log.d("SecretActivity", "Opération privilégiée exécutée")
        updateAdminPrivileges()
    }

    private fun updateAdminPrivileges() {
        // ToDo
    }
}
