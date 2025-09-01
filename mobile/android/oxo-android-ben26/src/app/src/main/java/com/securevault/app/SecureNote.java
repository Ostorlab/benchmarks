package com.securevault.app;

public class SecureNote {
    private int id;
    private String title;
    private String encryptedContent;
    private long dateCreated;
    private long dateModified;

    public SecureNote() {}

    public SecureNote(String title, String encryptedContent) {
        this.title = title;
        this.encryptedContent = encryptedContent;
        this.dateCreated = System.currentTimeMillis();
        this.dateModified = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEncryptedContent() { return encryptedContent; }
    public void setEncryptedContent(String encryptedContent) { this.encryptedContent = encryptedContent; }

    public long getDateCreated() { return dateCreated; }
    public void setDateCreated(long dateCreated) { this.dateCreated = dateCreated; }

    public long getDateModified() { return dateModified; }
    public void setDateModified(long dateModified) { this.dateModified = dateModified; }
}
