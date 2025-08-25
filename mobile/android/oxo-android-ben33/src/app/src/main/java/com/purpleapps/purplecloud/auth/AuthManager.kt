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
import java.util.UUID

/**
 * Manages user authentication, including login, logout, and session management.
 * It stores user credentials and access tokens in SharedPreferences.
 */
object AuthManager {
    private const val PREFS_NAME = "AuthPrefs"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_STATE = "oauth_state"

    private const val BASE_URL = "http://10.0.2.2:8000"
    private const val TOKEN_URL = "$BASE_URL/token"
    private const val REVOKE_URL = "$BASE_URL/revoke"
    private const val AUTHORIZE_URL = "$BASE_URL/authorize"
    private const val USERINFO_URL = "$BASE_URL/userinfo"

    private const val CLIENT_ID = "purplecloud-client"
    private const val REDIRECT_URI = "com.purpleapps.purplecloud://oauth2/callback"


    /**
     * Retrieves the shared preferences for authentication data.
     */
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Generates the URL for the authorization endpoint and stores a state parameter to prevent CSRF.
     * @param context The application context.
     * @return The full URL to be opened in a browser.
     */
    fun getAuthorizationUrl(context: Context): String {
        val state = UUID.randomUUID().toString()
        getPrefs(context).edit {
            putString(KEY_STATE, state)
        }
        val encodedRedirectUri = URLEncoder.encode(REDIRECT_URI, "UTF-8")
        return "$AUTHORIZE_URL?response_type=code&client_id=$CLIENT_ID&redirect_uri=$encodedRedirectUri&state=$state"
    }

    /**
     * Exchanges an authorization code for an access token and refresh token.
     * @param context The application context.
     * @param code The authorization code from the redirect.
     * @param state The state from the redirect, for CSRF protection.
     * @return `true` if the exchange is successful, `false` otherwise.
     */
    suspend fun exchangeCodeForToken(context: Context, code: String, state: String): Boolean = withContext(Dispatchers.IO) {
        val savedState = getPrefs(context).getString(KEY_STATE, null)
        if (savedState != state) {
            Log.e("AuthManager", "State mismatch error for CSRF protection.")
            return@withContext false
        }
        // Clear the state after use
        getPrefs(context).edit { remove(KEY_STATE) }

        try {
            val url = URL(TOKEN_URL)
            val postData = "grant_type=authorization_code" +
                    "&code=${URLEncoder.encode(code, "UTF-8")}" +
                    "&redirect_uri=${URLEncoder.encode(REDIRECT_URI, "UTF-8")}" +
                    "&client_id=${URLEncoder.encode(CLIENT_ID, "UTF-8")}"

            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.doOutput = true

            conn.outputStream.use { os ->
                os.write(postData.toByteArray(Charsets.UTF_8))
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(response)
                val accessToken = jsonObject.getString("access_token")
                val refreshToken = jsonObject.getString("refresh_token")

                getPrefs(context).edit {
                    putString(KEY_ACCESS_TOKEN, accessToken)
                    putString(KEY_REFRESH_TOKEN, refreshToken)
                }
                // After getting tokens, fetch user info
                return@withContext fetchAndStoreUserInfo(context)
            } else {
                Log.e("AuthManager", "Token exchange failed: ${conn.responseCode} ${conn.responseMessage}")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Token exchange failed", e)
            false
        }
    }

    /**
     * Fetches user information from the /userinfo endpoint and stores it.
     * @param context The application context.
     * @return `true` if successful, `false` otherwise.
     */
    private suspend fun fetchAndStoreUserInfo(context: Context): Boolean = withContext(Dispatchers.IO) {
        val accessToken = getAccessToken(context) ?: return@withContext false
        try {
            val url = URL(USERINFO_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", "Bearer $accessToken")

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(response)
                val email = jsonObject.getString("email")
                getPrefs(context).edit {
                    putString(KEY_USER_EMAIL, email)
                }
                true
            } else {
                Log.e("AuthManager", "Failed to fetch user info: ${conn.responseCode} ${conn.responseMessage}")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Failed to fetch user info", e)
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
     * Gets the refresh token for the currently logged-in user.
     * @param context The application context.
     * @return The refresh token, or `null` if no user is logged in.
     */
    private fun getRefreshToken(context: Context): String? {
        return getPrefs(context).getString(KEY_REFRESH_TOKEN, null)
    }

    /**
     * Refreshes the access token using the stored refresh token.
     * @param context The application context.
     * @return The new access token, or `null` if refresh fails.
     */
    suspend fun refreshAccessToken(context: Context): String? = withContext(Dispatchers.IO) {
        val refreshToken = getRefreshToken(context) ?: return@withContext null

        try {
            val url = URL(TOKEN_URL)
            val postData = "grant_type=refresh_token&refresh_token=${URLEncoder.encode(refreshToken, "UTF-8")}"
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.doOutput = true

            conn.outputStream.use { os ->
                os.write(postData.toByteArray(Charsets.UTF_8))
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(response)
                val newAccessToken = jsonObject.getString("access_token")
                // The server might not return a new refresh token, so we only update the access token.
                getPrefs(context).edit {
                    putString(KEY_ACCESS_TOKEN, newAccessToken)
                }
                newAccessToken
            } else {
                Log.w("AuthManager", "Failed to refresh token: ${conn.responseCode} ${conn.responseMessage}")
                // If refresh token is invalid, log out user
                if (conn.responseCode == HttpURLConnection.HTTP_BAD_REQUEST || conn.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    logout(context)
                }
                null
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Token refresh failed", e)
            null
        }
    }

    /**
     * Logs out the current user by calling the revoke endpoint on the server
     * and clearing local credentials.
     * @param context The application context.
     */
    suspend fun logout(context: Context) = withContext(Dispatchers.IO) {
        val accessToken = getAccessToken(context)
        val refreshToken = getRefreshToken(context)

        // Try to revoke tokens on the server
        if (accessToken != null) {
            revokeToken(accessToken)
        }
        if (refreshToken != null) {
            revokeToken(refreshToken)
        }

        // Always clear local data
        getPrefs(context).edit {
            remove(KEY_USER_EMAIL)
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_STATE)
        }
    }

    private suspend fun revokeToken(token: String) {
        try {
            val url = URL(REVOKE_URL)
            val postData = "token=${URLEncoder.encode(token, "UTF-8")}"
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.doOutput = true

            conn.outputStream.use { os ->
                os.write(postData.toByteArray(Charsets.UTF_8))
            }

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                Log.i("AuthManager", "Successfully revoked token.")
            } else {
                Log.w(
                    "AuthManager",
                    "Failed to revoke token: ${conn.responseCode} ${conn.responseMessage}"
                )
            }
        } catch (e: Exception) {
            Log.e("AuthManager", "Token revocation failed", e)
        }
    }
}
