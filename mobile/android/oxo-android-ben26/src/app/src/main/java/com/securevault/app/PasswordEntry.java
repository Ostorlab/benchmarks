package com.securevault.app;

public class PasswordEntry {
    private int id;
    private String siteName;
    private String username;
    private String encryptedPassword;
    private long dateCreated;
    private long dateModified;

    public PasswordEntry() {}

    public PasswordEntry(String siteName, String username, String encryptedPassword) {
        this.siteName = siteName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.dateCreated = System.currentTimeMillis();
        this.dateModified = System.currentTimeMillis();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEncryptedPassword() { return encryptedPassword; }
    public void setEncryptedPassword(String encryptedPassword) { this.encryptedPassword = encryptedPassword; }

    public long getDateCreated() { return dateCreated; }
    public void setDateCreated(long dateCreated) { this.dateCreated = dateCreated; }

    public long getDateModified() { return dateModified; }
    public void setDateModified(long dateModified) { this.dateModified = dateModified; }
}
