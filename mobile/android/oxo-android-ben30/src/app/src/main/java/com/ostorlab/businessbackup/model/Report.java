package com.ostorlab.businessbackup.model;

/**
 * Report entity representing a generated business report
 */
public class Report {
    private long id;
    private String title;
    private String type;
    private String content;
    private long generatedAt;
    private String filePath;
    private int customerCount;
    private String status;

    public static final String TYPE_MONTHLY = "monthly";
    public static final String TYPE_QUARTERLY = "quarterly";
    public static final String TYPE_ANNUAL = "annual";
    public static final String TYPE_CUSTOMER = "customer";
    public static final String TYPE_SALES = "sales";

    public static final String STATUS_GENERATED = "generated";
    public static final String STATUS_GENERATING = "generating";
    public static final String STATUS_FAILED = "failed";

    public Report() {
        this.generatedAt = System.currentTimeMillis();
        this.status = STATUS_GENERATING;
    }

    public Report(String title, String type) {
        this();
        this.title = title;
        this.type = type;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(long generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(int customerCount) {
        this.customerCount = customerCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", generatedAt=" + generatedAt +
                ", customerCount=" + customerCount +
                ", status='" + status + '\'' +
                '}';
    }
}
