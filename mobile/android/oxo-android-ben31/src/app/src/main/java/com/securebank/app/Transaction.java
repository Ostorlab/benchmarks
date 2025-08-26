package com.securebank.app;

public class Transaction {
    private int id;
    private String fromUsername;
    private String toAccount;
    private double amount;
    private String transactionType;
    private String description;
    private String transactionDate;
    private String status;

    public Transaction() {}

    public Transaction(String fromUsername, String toAccount, double amount, String transactionType, String description, String status) {
        this.fromUsername = fromUsername;
        this.toAccount = toAccount;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFromUsername() { return fromUsername; }
    public void setFromUsername(String fromUsername) { this.fromUsername = fromUsername; }

    public String getToAccount() { return toAccount; }
    public void setToAccount(String toAccount) { this.toAccount = toAccount; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
