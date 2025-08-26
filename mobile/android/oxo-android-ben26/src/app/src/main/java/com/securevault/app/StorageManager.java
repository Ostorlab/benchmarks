package com.securevault.app;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StorageManager {
    private static final String PREFS_NAME = "SecureVaultPrefs";
    private static final String KEY_PASSWORDS = "passwords";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_MASTER_KEY = "master_key";
    private static final String KEY_USER_HASH = "user_hash";
    private static final String KEY_USER_SALT = "user_salt";

    private SharedPreferences prefs;
    private Context context;

    public StorageManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveMasterKey(String masterKey) {
        prefs.edit().putString(KEY_MASTER_KEY, masterKey).apply();
    }

    public String getMasterKey() {
        return prefs.getString(KEY_MASTER_KEY, null);
    }

    public void saveUserCredentials(String username, String passwordHash, String salt) {
        prefs.edit()
                .putString(KEY_USER_HASH, passwordHash)
                .putString(KEY_USER_SALT, salt)
                .apply();
    }

    public boolean validateUser(String username, String password) {
        String storedHash = prefs.getString(KEY_USER_HASH, null);
        String storedSalt = prefs.getString(KEY_USER_SALT, null);

        if (storedHash == null || storedSalt == null) {
            return false;
        }

        String inputHash = CryptoManager.hashPassword(password, android.util.Base64.decode(storedSalt, android.util.Base64.DEFAULT));
        return storedHash.equals(inputHash);
    }

    public void savePasswordEntry(PasswordEntry entry) {
        List<PasswordEntry> passwords = getPasswordEntries();
        entry.setId(passwords.size() + 1);
        passwords.add(entry);
        savePasswordEntries(passwords);
    }

    public List<PasswordEntry> getPasswordEntries() {
        String json = prefs.getString(KEY_PASSWORDS, "[]");
        List<PasswordEntry> passwords = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                PasswordEntry entry = new PasswordEntry();
                entry.setId(obj.getInt("id"));
                entry.setSiteName(obj.getString("siteName"));
                entry.setUsername(obj.getString("username"));
                entry.setEncryptedPassword(obj.getString("encryptedPassword"));
                entry.setDateCreated(obj.getLong("dateCreated"));
                entry.setDateModified(obj.getLong("dateModified"));
                passwords.add(entry);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return passwords;
    }

    private void savePasswordEntries(List<PasswordEntry> passwords) {
        JSONArray array = new JSONArray();

        try {
            for (PasswordEntry entry : passwords) {
                JSONObject obj = new JSONObject();
                obj.put("id", entry.getId());
                obj.put("siteName", entry.getSiteName());
                obj.put("username", entry.getUsername());
                obj.put("encryptedPassword", entry.getEncryptedPassword());
                obj.put("dateCreated", entry.getDateCreated());
                obj.put("dateModified", entry.getDateModified());
                array.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        prefs.edit().putString(KEY_PASSWORDS, array.toString()).apply();
    }

    public void saveSecureNote(SecureNote note) {
        List<SecureNote> notes = getSecureNotes();
        note.setId(notes.size() + 1);
        notes.add(note);
        saveSecureNotes(notes);
    }

    public List<SecureNote> getSecureNotes() {
        String json = prefs.getString(KEY_NOTES, "[]");
        List<SecureNote> notes = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                SecureNote note = new SecureNote();
                note.setId(obj.getInt("id"));
                note.setTitle(obj.getString("title"));
                note.setEncryptedContent(obj.getString("encryptedContent"));
                note.setDateCreated(obj.getLong("dateCreated"));
                note.setDateModified(obj.getLong("dateModified"));
                notes.add(note);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return notes;
    }

    private void saveSecureNotes(List<SecureNote> notes) {
        JSONArray array = new JSONArray();

        try {
            for (SecureNote note : notes) {
                JSONObject obj = new JSONObject();
                obj.put("id", note.getId());
                obj.put("title", note.getTitle());
                obj.put("encryptedContent", note.getEncryptedContent());
                obj.put("dateCreated", note.getDateCreated());
                obj.put("dateModified", note.getDateModified());
                array.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        prefs.edit().putString(KEY_NOTES, array.toString()).apply();
    }

    public boolean isUserRegistered() {
        return prefs.getString(KEY_USER_HASH, null) != null;
    }

    public void clearAllData() {
        prefs.edit().clear().apply();
    }
}
