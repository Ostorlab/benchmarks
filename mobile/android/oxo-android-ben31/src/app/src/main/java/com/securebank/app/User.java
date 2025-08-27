package com.securebank.app;

public class User {
    private int id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String accountNumber;
    private double balance;

    public User() {}

    public User(String username, String email, String phone, String address, String accountNumber, double balance) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
