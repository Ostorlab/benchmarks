package com.securebank.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "securebank.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_ACCOUNT_NUMBER = "account_number";
    private static final String COLUMN_BALANCE = "balance";
    private static final String COLUMN_CREATED_AT = "created_at";

    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String COLUMN_TRANS_ID = "transaction_id";
    private static final String COLUMN_FROM_USERNAME = "from_username";
    private static final String COLUMN_TO_ACCOUNT = "to_account";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_TRANS_TYPE = "transaction_type";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_TRANS_DATE = "transaction_date";
    private static final String COLUMN_STATUS = "status";

    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL,"
            + COLUMN_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_PHONE + " TEXT,"
            + COLUMN_ADDRESS + " TEXT,"
            + COLUMN_ACCOUNT_NUMBER + " TEXT UNIQUE,"
            + COLUMN_BALANCE + " REAL DEFAULT 0.0,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    private static final String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
            + COLUMN_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_FROM_USERNAME + " TEXT NOT NULL,"
            + COLUMN_TO_ACCOUNT + " TEXT NOT NULL,"
            + COLUMN_AMOUNT + " REAL NOT NULL,"
            + COLUMN_TRANS_TYPE + " TEXT NOT NULL,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_TRANS_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + COLUMN_STATUS + " TEXT DEFAULT 'SUCCESS'"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
        insertDefaultUsers(db);
        insertSampleTransactions(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CREATE_TRANSACTIONS_TABLE);
            insertSampleTransactions(db);
        }
    }

    private void insertDefaultUsers(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_USERNAME, "demo");
        values.put(COLUMN_PASSWORD, "password");
        values.put(COLUMN_EMAIL, "demo@securebank.com");
        values.put(COLUMN_PHONE, "+1 (555) 123-4567");
        values.put(COLUMN_ADDRESS, "123 Main St, Anytown, USA");
        values.put(COLUMN_ACCOUNT_NUMBER, "4532-1234-5678-9012");
        values.put(COLUMN_BALANCE, 15432.50);
        db.insert(TABLE_USERS, null, values);

        values.clear();
        values.put(COLUMN_USERNAME, "admin");
        values.put(COLUMN_PASSWORD, "admin123");
        values.put(COLUMN_EMAIL, "admin@securebank.com");
        values.put(COLUMN_PHONE, "+1 (555) 987-6543");
        values.put(COLUMN_ADDRESS, "456 Admin Ave, Banktown, USA");
        values.put(COLUMN_ACCOUNT_NUMBER, "4532-9876-5432-1098");
        values.put(COLUMN_BALANCE, 25000.00);
        db.insert(TABLE_USERS, null, values);

        values.clear();
        values.put(COLUMN_USERNAME, "user");
        values.put(COLUMN_PASSWORD, "user123");
        values.put(COLUMN_EMAIL, "user@securebank.com");
        values.put(COLUMN_PHONE, "+1 (555) 555-5555");
        values.put(COLUMN_ADDRESS, "789 User Blvd, Clientville, USA");
        values.put(COLUMN_ACCOUNT_NUMBER, "4532-5555-7777-3333");
        values.put(COLUMN_BALANCE, 8750.25);
        db.insert(TABLE_USERS, null, values);

        values.clear();
        values.put(COLUMN_USERNAME, "alice");
        values.put(COLUMN_PASSWORD, "alice123");
        values.put(COLUMN_EMAIL, "alice@securebank.com");
        values.put(COLUMN_PHONE, "+1 (555) 111-2222");
        values.put(COLUMN_ADDRESS, "101 Alice Lane, Wonderland, USA");
        values.put(COLUMN_ACCOUNT_NUMBER, "4532-1111-2222-3333");
        values.put(COLUMN_BALANCE, 12500.75);
        db.insert(TABLE_USERS, null, values);

        values.clear();
        values.put(COLUMN_USERNAME, "bob");
        values.put(COLUMN_PASSWORD, "bob123");
        values.put(COLUMN_EMAIL, "bob@securebank.com");
        values.put(COLUMN_PHONE, "+1 (555) 333-4444");
        values.put(COLUMN_ADDRESS, "202 Bob Street, Builder City, USA");
        values.put(COLUMN_ACCOUNT_NUMBER, "4532-3333-4444-5555");
        values.put(COLUMN_BALANCE, 7890.50);
        db.insert(TABLE_USERS, null, values);
    }

    private void insertSampleTransactions(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_FROM_USERNAME, "demo");
        values.put(COLUMN_TO_ACCOUNT, "4532-9876-5432-1098");
        values.put(COLUMN_AMOUNT, 250.00);
        values.put(COLUMN_TRANS_TYPE, "TRANSFER");
        values.put(COLUMN_DESCRIPTION, "Transfer to Admin Account");
        values.put(COLUMN_TRANS_DATE, "2025-08-25 14:30:00");
        db.insert(TABLE_TRANSACTIONS, null, values);

        values.clear();
        values.put(COLUMN_FROM_USERNAME, "demo");
        values.put(COLUMN_TO_ACCOUNT, "ATM");
        values.put(COLUMN_AMOUNT, 100.00);
        values.put(COLUMN_TRANS_TYPE, "WITHDRAWAL");
        values.put(COLUMN_DESCRIPTION, "ATM Withdrawal");
        values.put(COLUMN_TRANS_DATE, "2025-08-24 10:15:00");
        db.insert(TABLE_TRANSACTIONS, null, values);

        values.clear();
        values.put(COLUMN_FROM_USERNAME, "demo");
        values.put(COLUMN_TO_ACCOUNT, "MERCHANT");
        values.put(COLUMN_AMOUNT, 45.99);
        values.put(COLUMN_TRANS_TYPE, "PURCHASE");
        values.put(COLUMN_DESCRIPTION, "Online Purchase - Amazon");
        values.put(COLUMN_TRANS_DATE, "2025-08-23 16:45:00");
        db.insert(TABLE_TRANSACTIONS, null, values);
    }

    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
            user.setAccountNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_NUMBER)));
            user.setBalance(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BALANCE)));
        }
        cursor.close();
        return user;
    }

    public boolean registerUser(String username, String password, String email, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();

        String accountNumber = generateAccountNumber();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_ACCOUNT_NUMBER, accountNumber);
        values.put(COLUMN_BALANCE, 1000.00);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean updateUserProfile(String username, String email, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_ADDRESS, address);

        int result = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        return result > 0;
    }

    public boolean updateBalance(String username, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_BALANCE, newBalance);

        int result = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        return result > 0;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private String generateAccountNumber() {
        return "4532-" + String.format("%04d", (int)(Math.random() * 10000)) + "-" +
               String.format("%04d", (int)(Math.random() * 10000)) + "-" +
               String.format("%04d", (int)(Math.random() * 10000));
    }

    public boolean transferMoney(String fromUsername, String toAccountNumber, double amount, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            User fromUser = getUserByUsername(fromUsername);
            if (fromUser == null || fromUser.getBalance() < amount) {
                return false;
            }

            User toUser = getUserByAccountNumber(toAccountNumber);
            if (toUser == null) {
                return false;
            }

            double newFromBalance = fromUser.getBalance() - amount;
            double newToBalance = toUser.getBalance() + amount;

            boolean fromUpdated = updateBalance(fromUsername, newFromBalance);
            boolean toUpdated = updateBalance(toUser.getUsername(), newToBalance);

            if (fromUpdated && toUpdated) {
                ContentValues transactionValues = new ContentValues();
                transactionValues.put(COLUMN_FROM_USERNAME, fromUsername);
                transactionValues.put(COLUMN_TO_ACCOUNT, toAccountNumber);
                transactionValues.put(COLUMN_AMOUNT, amount);
                transactionValues.put(COLUMN_TRANS_TYPE, "TRANSFER");
                transactionValues.put(COLUMN_DESCRIPTION, description);
                transactionValues.put(COLUMN_STATUS, "SUCCESS");

                long result = db.insert(TABLE_TRANSACTIONS, null, transactionValues);
                if (result != -1) {
                    db.setTransactionSuccessful();
                    return true;
                }
            }
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public User getUserByAccountNumber(String accountNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_ACCOUNT_NUMBER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{accountNumber});

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            user.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)));
            user.setAccountNumber(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCOUNT_NUMBER)));
            user.setBalance(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BALANCE)));
        }
        cursor.close();
        return user;
    }

    public List<Transaction> getTransactionHistory(String username) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE " + COLUMN_FROM_USERNAME + " = ? ORDER BY " + COLUMN_TRANS_DATE + " DESC LIMIT 20";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        while (cursor.moveToNext()) {
            Transaction transaction = new Transaction();
            transaction.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TRANS_ID)));
            transaction.setFromUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FROM_USERNAME)));
            transaction.setToAccount(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TO_ACCOUNT)));
            transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)));
            transaction.setTransactionType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_TYPE)));
            transaction.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            transaction.setTransactionDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TRANS_DATE)));
            transaction.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            transactions.add(transaction);
        }
        cursor.close();
        return transactions;
    }
}
