package com.fittracker.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fittracker.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_WORKOUTS = "workouts";
    private static final String TABLE_NUTRITION = "nutrition";
    private static final String TABLE_ACHIEVEMENTS = "achievements";

    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_AGE = "age";
    private static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    private static final String KEY_EMERGENCY_CONTACT = "emergency_contact";
    private static final String KEY_INSURANCE_NUMBER = "insurance_number";
    private static final String KEY_BILLING_ADDRESS = "billing_address";
    private static final String KEY_ACCOUNT_TYPE = "account_type";
    private static final String KEY_LAST_LOGIN = "last_login";
    private static final String KEY_CREATED_AT = "created_at";

    private static final String KEY_WORKOUT_TYPE = "workout_type";
    private static final String KEY_CALORIES = "calories";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_HEART_RATE = "heart_rate";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_GPS_COORDS = "gps_coordinates";
    private static final String KEY_TIMESTAMP = "timestamp";

    private static final String KEY_MEAL_TYPE = "meal_type";
    private static final String KEY_FOOD_ITEMS = "food_items";
    private static final String KEY_PROTEIN = "protein";
    private static final String KEY_CARBS = "carbs";
    private static final String KEY_FAT = "fat";
    private static final String KEY_PHOTO_PATH = "photo_path";

    private static final String KEY_ACHIEVEMENT_TYPE = "achievement_type";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_POINTS = "points";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " TEXT UNIQUE,"
            + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT UNIQUE,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_PHONE + " TEXT,"
            + KEY_WEIGHT + " INTEGER,"
            + KEY_HEIGHT + " INTEGER,"
            + KEY_AGE + " INTEGER,"
            + KEY_DATE_OF_BIRTH + " TEXT,"
            + KEY_EMERGENCY_CONTACT + " TEXT,"
            + KEY_INSURANCE_NUMBER + " TEXT,"
            + KEY_BILLING_ADDRESS + " TEXT,"
            + KEY_ACCOUNT_TYPE + " TEXT DEFAULT 'free',"
            + KEY_LAST_LOGIN + " INTEGER,"
            + KEY_CREATED_AT + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_WORKOUTS = "CREATE TABLE " + TABLE_WORKOUTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " TEXT,"
            + KEY_WORKOUT_TYPE + " TEXT,"
            + KEY_CALORIES + " INTEGER,"
            + KEY_DISTANCE + " REAL,"
            + KEY_DURATION + " INTEGER,"
            + KEY_HEART_RATE + " TEXT,"
            + KEY_LOCATION + " TEXT,"
            + KEY_GPS_COORDS + " TEXT,"
            + KEY_TIMESTAMP + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_NUTRITION = "CREATE TABLE " + TABLE_NUTRITION + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " TEXT,"
            + KEY_MEAL_TYPE + " TEXT,"
            + KEY_FOOD_ITEMS + " TEXT,"
            + KEY_CALORIES + " INTEGER,"
            + KEY_PROTEIN + " INTEGER,"
            + KEY_CARBS + " INTEGER,"
            + KEY_FAT + " INTEGER,"
            + KEY_PHOTO_PATH + " TEXT,"
            + KEY_TIMESTAMP + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_ACHIEVEMENTS = "CREATE TABLE " + TABLE_ACHIEVEMENTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USER_ID + " TEXT,"
            + KEY_ACHIEVEMENT_TYPE + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_POINTS + " INTEGER,"
            + KEY_TIMESTAMP + " INTEGER"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_WORKOUTS);
        db.execSQL(CREATE_TABLE_NUTRITION);
        db.execSQL(CREATE_TABLE_ACHIEVEMENTS);
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NUTRITION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENTS);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, "user_12345");
        values.put(KEY_NAME, "John Doe");
        values.put(KEY_EMAIL, "john.doe@email.com");
        values.put(KEY_PASSWORD, "password123");
        values.put(KEY_PHONE, "+1-555-0123");
        values.put(KEY_WEIGHT, 75);
        values.put(KEY_HEIGHT, 180);
        values.put(KEY_AGE, 28);
        values.put(KEY_DATE_OF_BIRTH, "1995-03-15");
        values.put(KEY_EMERGENCY_CONTACT, "Jane Doe +1-555-0124");
        values.put(KEY_INSURANCE_NUMBER, "INS123456789");
        values.put(KEY_BILLING_ADDRESS, "123 Main St, NYC, NY 10001");
        db.insert(TABLE_USERS, null, values);

        for (int i = 0; i < 5; i++) {
            ContentValues workoutValues = new ContentValues();
            workoutValues.put(KEY_USER_ID, "user_12345");
            workoutValues.put(KEY_WORKOUT_TYPE, "running");
            workoutValues.put(KEY_CALORIES, 300 + (i * 50));
            workoutValues.put(KEY_DISTANCE, 3.0 + (i * 0.5));
            workoutValues.put(KEY_DURATION, 1800 + (i * 300));
            workoutValues.put(KEY_HEART_RATE, "140 bpm");
            workoutValues.put(KEY_LOCATION, "Central Park, NYC");
            workoutValues.put(KEY_GPS_COORDS, "40.7829,-73.9654");
            workoutValues.put(KEY_TIMESTAMP, System.currentTimeMillis() - (i * 86400000));
            db.insert(TABLE_WORKOUTS, null, workoutValues);
        }
    }

    public long insertWorkout(String userId, String workoutType, int calories, double distance,
                             int duration, String heartRate, String location, String gpsCoords) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_WORKOUT_TYPE, workoutType);
        values.put(KEY_CALORIES, calories);
        values.put(KEY_DISTANCE, distance);
        values.put(KEY_DURATION, duration);
        values.put(KEY_HEART_RATE, heartRate);
        values.put(KEY_LOCATION, location);
        values.put(KEY_GPS_COORDS, gpsCoords);
        values.put(KEY_TIMESTAMP, System.currentTimeMillis());
        return db.insert(TABLE_WORKOUTS, null, values);
    }

    public long insertNutrition(String userId, String mealType, String foodItems, int calories,
                               int protein, int carbs, int fat, String photoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_MEAL_TYPE, mealType);
        values.put(KEY_FOOD_ITEMS, foodItems);
        values.put(KEY_CALORIES, calories);
        values.put(KEY_PROTEIN, protein);
        values.put(KEY_CARBS, carbs);
        values.put(KEY_FAT, fat);
        values.put(KEY_PHOTO_PATH, photoPath);
        values.put(KEY_TIMESTAMP, System.currentTimeMillis());
        return db.insert(TABLE_NUTRITION, null, values);
    }

    public User getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_USER_ID + "=?", new String[]{userId}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.setPhone(cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
            user.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_WEIGHT)));
            user.setHeight(cursor.getInt(cursor.getColumnIndex(KEY_HEIGHT)));
            user.setDateOfBirth(cursor.getString(cursor.getColumnIndex(KEY_DATE_OF_BIRTH)));
            user.setEmergencyContact(cursor.getString(cursor.getColumnIndex(KEY_EMERGENCY_CONTACT)));
            user.setInsuranceNumber(cursor.getString(cursor.getColumnIndex(KEY_INSURANCE_NUMBER)));
            user.setBillingAddress(cursor.getString(cursor.getColumnIndex(KEY_BILLING_ADDRESS)));
            cursor.close();
        }
        return user;
    }

    public List<Workout> getAllWorkouts(String userId) {
        List<Workout> workouts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUTS, null, KEY_USER_ID + "=?", new String[]{userId}, null, null, KEY_TIMESTAMP + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Workout workout = new Workout();
                workout.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                workout.setWorkoutType(cursor.getString(cursor.getColumnIndex(KEY_WORKOUT_TYPE)));
                workout.setCalories(cursor.getInt(cursor.getColumnIndex(KEY_CALORIES)));
                workout.setDistance(cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE)));
                workout.setDuration(cursor.getInt(cursor.getColumnIndex(KEY_DURATION)));
                workout.setHeartRate(cursor.getString(cursor.getColumnIndex(KEY_HEART_RATE)));
                workout.setLocation(cursor.getString(cursor.getColumnIndex(KEY_LOCATION)));
                workout.setGpsCoords(cursor.getString(cursor.getColumnIndex(KEY_GPS_COORDS)));
                workout.setTimestamp(cursor.getLong(cursor.getColumnIndex(KEY_TIMESTAMP)));
                workouts.add(workout);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return workouts;
    }

    public WorkoutStats getWorkoutStats(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*), SUM(" + KEY_CALORIES + "), SUM(" + KEY_DISTANCE + ") FROM "
                + TABLE_WORKOUTS + " WHERE " + KEY_USER_ID + "=?", new String[]{userId});

        WorkoutStats stats = new WorkoutStats();
        if (cursor != null && cursor.moveToFirst()) {
            stats.setTotalWorkouts(cursor.getInt(0));
            stats.setTotalCalories(cursor.getInt(1));
            stats.setTotalDistance(cursor.getDouble(2));
            cursor.close();
        }
        return stats;
    }

    public long registerUser(String userId, String name, String email, String password, String phone, int age, int weight, int height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, userId);
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_PHONE, phone);
        values.put(KEY_AGE, age);
        values.put(KEY_WEIGHT, weight);
        values.put(KEY_HEIGHT, height);
        values.put(KEY_ACCOUNT_TYPE, "free");
        values.put(KEY_CREATED_AT, System.currentTimeMillis());
        values.put(KEY_LAST_LOGIN, 0);
        return db.insert(TABLE_USERS, null, values);
    }

    public User authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_EMAIL + "=? AND " + KEY_PASSWORD + "=?",
                                new String[]{email, password}, null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
            user.setPhone(cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
            user.setWeight(cursor.getInt(cursor.getColumnIndex(KEY_WEIGHT)));
            user.setHeight(cursor.getInt(cursor.getColumnIndex(KEY_HEIGHT)));
            user.setAge(cursor.getInt(cursor.getColumnIndex(KEY_AGE)));
            user.setAccountType(cursor.getString(cursor.getColumnIndex(KEY_ACCOUNT_TYPE)));
            user.setLastLogin(cursor.getLong(cursor.getColumnIndex(KEY_LAST_LOGIN)));

            updateLastLogin(user.getUserId());
            cursor.close();
        }
        return user;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, KEY_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }

    public String getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USER_ID}, KEY_EMAIL + "=?", new String[]{email}, null, null, null);
        String userId = null;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getString(0);
            cursor.close();
        }
        return userId;
    }

    private void updateLastLogin(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LAST_LOGIN, System.currentTimeMillis());
        db.update(TABLE_USERS, values, KEY_USER_ID + "=?", new String[]{userId});
    }
}
