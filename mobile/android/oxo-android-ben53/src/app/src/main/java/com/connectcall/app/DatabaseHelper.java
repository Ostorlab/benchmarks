package com.connectcall.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "connectcall.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CONTACTS = "contacts";
    public static final String TABLE_CALL_HISTORY = "call_history";
    public static final String TABLE_PROFILE = "profile";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_TYPE = "type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createContactsTable = "CREATE TABLE " + TABLE_CONTACTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL," +
                COLUMN_PHONE + " TEXT NOT NULL," +
                COLUMN_EMAIL + " TEXT" + ")";

        String createCallHistoryTable = "CREATE TABLE " + TABLE_CALL_HISTORY + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL," +
                COLUMN_PHONE + " TEXT NOT NULL," +
                COLUMN_TIMESTAMP + " INTEGER," +
                COLUMN_DURATION + " INTEGER," +
                COLUMN_TYPE + " TEXT" + ")";

        String createProfileTable = "CREATE TABLE " + TABLE_PROFILE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT," +
                COLUMN_PHONE + " TEXT," +
                COLUMN_EMAIL + " TEXT" + ")";

        db.execSQL(createContactsTable);
        db.execSQL(createCallHistoryTable);
        db.execSQL(createProfileTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        onCreate(db);
    }
}
