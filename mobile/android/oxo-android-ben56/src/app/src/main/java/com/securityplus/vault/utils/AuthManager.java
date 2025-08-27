package com.securityplus.vault.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Centralized authentication manager with multiple security vulnerabilities
 * for demonstration purposes in security research.
 */
public class AuthManager {
    
    private static final String PREFS_NAME = "SecureVaultPrefs";
    private static final String KEY_AUTHENTICATED = "is_authenticated";
    private static final String KEY_LAST_AUTH_TIME = "last_auth_time";
    private static final String KEY_SESSION_TOKEN = "session_token";
    
    // VULNERABILITY 1: Static authentication state persists across app lifecycle
    private static boolean isCurrentlyAuthenticated = false;
    private static long lastAuthenticationTime = 0;
    private static String currentSessionToken = null;
    
    // VULNERABILITY 2: Weak session timeout (static, easily manipulated)
    private static final long SESSION_TIMEOUT = 300000; // 5 minutes in milliseconds
    
    public static boolean isAuthenticated(Context context) {
        // Check static variable first (VULNERABLE - persists across lifecycle)
        if (isCurrentlyAuthenticated) {
            // VULNERABILITY 3: No session timeout check in static path
            return true;
        }
        
        // Fallback to SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_AUTHENTICATED, false);
    }
    
    public static void setAuthenticated(Context context, boolean authenticated) {
        // VULNERABILITY 4: Race condition - static and SharedPreferences updated separately
        isCurrentlyAuthenticated = authenticated;
        
        if (authenticated) {
            lastAuthenticationTime = System.currentTimeMillis();
            currentSessionToken = generateSessionToken();
        }
        
        // Delayed SharedPreferences update creates race condition window
        new Thread(() -> {
            try {
                Thread.sleep(100); // Small delay creates race condition
                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit()
                    .putBoolean(KEY_AUTHENTICATED, authenticated)
                    .putLong(KEY_LAST_AUTH_TIME, lastAuthenticationTime)
                    .putString(KEY_SESSION_TOKEN, currentSessionToken)
                    .apply();
            } catch (InterruptedException e) {
                // Ignore
            }
        }).start();
    }
    
    public static boolean isSessionValid(Context context) {
        long currentTime = System.currentTimeMillis();
        
        // VULNERABILITY 5: Session check only works if static state exists
        if (lastAuthenticationTime == 0) {
            // Fallback to SharedPreferences (but doesn't update static variables!)
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            lastAuthenticationTime = prefs.getLong(KEY_LAST_AUTH_TIME, 0);
        }
        
        return (currentTime - lastAuthenticationTime) < SESSION_TIMEOUT;
    }
    
    public static void clearAuthentication(Context context) {
        // VULNERABILITY 6: Inconsistent clearing - static vs SharedPreferences
        isCurrentlyAuthenticated = false;
        // Sometimes forget to clear static variables during app switching
        if (Math.random() > 0.3) { // 70% chance to clear properly
            lastAuthenticationTime = 0;
            currentSessionToken = null;
        }
        
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
            .putBoolean(KEY_AUTHENTICATED, false)
            .putLong(KEY_LAST_AUTH_TIME, 0)
            .putString(KEY_SESSION_TOKEN, null)
            .apply();
    }
    
    // VULNERABILITY 7: Intent parameter bypass
    public static boolean checkIntentBypass(Context context, android.content.Intent intent) {
        if (intent != null) {
            // Debug mode bypass (common in development builds left in production)
            if (intent.getBooleanExtra("debug_skip_auth", false)) {
                setAuthenticated(context, true);
                return true;
            }
            
            // Admin bypass (intended for admin tools but accessible via intents)
            if ("admin_access".equals(intent.getStringExtra("access_mode"))) {
                setAuthenticated(context, true);
                return true;
            }
            
            // Emergency bypass (social engineering vector)
            if (intent.getBooleanExtra("emergency_access", false)) {
                setAuthenticated(context, true);
                return true;
            }
        }
        return false;
    }
    
    private static String generateSessionToken() {
        return "session_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    // VULNERABILITY 8: Development method left in production
    public static void forceAuthentication(Context context) {
        isCurrentlyAuthenticated = true;
        lastAuthenticationTime = System.currentTimeMillis();
        currentSessionToken = generateSessionToken();
        
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
            .putBoolean(KEY_AUTHENTICATED, true)
            .putLong(KEY_LAST_AUTH_TIME, lastAuthenticationTime)
            .putString(KEY_SESSION_TOKEN, currentSessionToken)
            .apply();
    }
}