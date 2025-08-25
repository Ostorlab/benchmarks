package com.ostorlab.businessbackup.model;

/**
 * Backup entity representing a backup record
 */
public class BackupRecord {
    private long id;
    private String name;
    private String filePath;
    private long timestamp;
    private long size;
    private String status;
    private String description;

    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_IN_PROGRESS = "in_progress";

    public BackupRecord() {
        this.timestamp = System.currentTimeMillis();
        this.status = STATUS_IN_PROGRESS;
    }

    public BackupRecord(String name, String filePath, long size) {
        this();
        this.name = name;
        this.filePath = filePath;
        this.size = size;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get formatted file size
     */
    public String getFormattedSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }

    @Override
    public String toString() {
        return "BackupRecord{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", timestamp=" + timestamp +
                ", size=" + size +
                ", status='" + status + '\'' +
                '}';
    }
}
