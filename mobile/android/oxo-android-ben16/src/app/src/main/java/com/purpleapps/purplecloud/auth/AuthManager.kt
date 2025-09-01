package com.purpleapps.purplecloud.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Manages user authentication, including login, logout, and session management.
 * It stores user credentials and access tokens in SharedPreferences.
 */
object AuthManager {
    private const val PREFS_NAME = "AuthPrefs"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val LOGIN_URL = "http://10.0.2.2:8000/token"

    /**
     * Retrieves the shared preferences for authentication data.
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Attempts to log in a user with the given email and password.
     * On success, it stores the user's email and access token.
     * @param context The application context.
     * @param email The user's email.
     * @param password The user's password.
     * @return `true` if login is successful, `false` otherwise.
     */
    suspend fun login(context: Context, email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL(LOGIN_URL)
            val postData = "username=${URLEncoder.encode(email, "UTF-8")}&password=${URLEncoder.encode(password, "UTF-8")}"

            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.doOutput = true

            conn.outputStream.use { os ->
                val input = postData.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(response)
                val accessToken = jsonObject.getString("access_token")

                getPrefs(context).edit {
                    putString(KEY_USER_EMAIL, email)
                    putString(KEY_ACCESS_TOKEN, accessToken)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Login failed", e)
            false
        }
    }

    /**
     * Checks if a user is currently logged in.
     * @param context The application context.
     * @return `true` if an access token exists, `false` otherwise.
     */
    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).contains(KEY_ACCESS_TOKEN)
    }

    /**
     * Gets the email of the currently logged-in user.
     * @param context The application context.
     * @return The user's email, or `null` if no user is logged in.
     */
    fun getCurrentUser(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_EMAIL, null)
    }

    /**
     * Gets the access token for the currently logged-in user.
     * @param context The application context.
     * @return The access token, or `null` if no user is logged in.
     */
    fun getAccessToken(context: Context): String? {
        return getPrefs(context).getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Logs out the current user by clearing their email and access token from storage.
     * @param context The application context.
     */
    fun logout(context: Context) {
        getPrefs(context).edit {
            remove(KEY_USER_EMAIL)
            remove(KEY_ACCESS_TOKEN)
        }
    }
}
