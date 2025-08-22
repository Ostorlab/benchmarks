package co.ostorlab.ben15;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "BankingSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SESSION_ID = "sessionId";
    
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        
        if (!pref.contains(KEY_PASSWORD)) {
            editor.putString(KEY_PASSWORD, DEFAULT_PASSWORD);
            editor.apply();
        }
    }
    
    public boolean login(String username, String password) {
        String storedPassword = pref.getString(KEY_PASSWORD, DEFAULT_PASSWORD);
        
        if (DEFAULT_USERNAME.equals(username) && storedPassword.equals(password)) {
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_SESSION_ID, generateSessionId());
            editor.apply();
            return true;
        }
        return false;
    }
    
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public String getUsername() {
        return pref.getString(KEY_USERNAME, "");
    }
    
    public String getSessionId() {
        return pref.getString(KEY_SESSION_ID, "");
    }
    
    public boolean changePassword(String currentPassword, String newPassword) {
        String storedPassword = pref.getString(KEY_PASSWORD, DEFAULT_PASSWORD);
        
        if (storedPassword.equals(currentPassword)) {
            editor.putString(KEY_PASSWORD, newPassword);
            editor.apply();
            return true;
        }
        return false;
    }
    
    public void logout() {
        editor.clear();
        editor.apply();
    }
    
    private String generateSessionId() {
        return "session_" + System.currentTimeMillis();
    }
}