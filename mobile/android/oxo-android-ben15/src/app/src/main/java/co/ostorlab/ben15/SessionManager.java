package co.ostorlab.ben15;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "BankingSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_SESSION_ID = "sessionId";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private ApiService apiService;
    
    public SessionManager(Context context) {
        this.context = context;
        this.apiService = new ApiService();
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    public interface LoginCallback {
        void onSuccess(String username);
        void onError(String error);
    }
    
    public void login(String username, String password, LoginCallback callback) {
        apiService.login(username, password, new ApiService.LoginCallback() {
            @Override
            public void onSuccess(String token, String user) {
                editor.putBoolean(KEY_IS_LOGGED_IN, true);
                editor.putString(KEY_USERNAME, user);
                editor.putString(KEY_TOKEN, token);
                editor.putString(KEY_SESSION_ID, generateSessionId());
                editor.apply();
                callback.onSuccess(user);
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
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
    
    public interface PasswordChangeCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public void changePassword(String currentPassword, String newPassword, PasswordChangeCallback callback) {
        String token = pref.getString(KEY_TOKEN, "");
        
        apiService.changePassword(token, currentPassword, newPassword, new ApiService.PasswordChangeCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }
    
    public void logout() {
        editor.clear();
        editor.apply();
    }
    
    public void invalidateSession() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_SESSION_ID);
        editor.apply();
    }
    
    private String generateSessionId() {
        return "session_" + System.currentTimeMillis();
    }
}