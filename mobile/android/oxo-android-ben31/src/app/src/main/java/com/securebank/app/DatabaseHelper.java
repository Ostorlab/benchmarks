package com.securebank.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "securebank.db";
    private static final int DATABASE_VERSION = 1;

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        insertDefaultUsers(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
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
}
