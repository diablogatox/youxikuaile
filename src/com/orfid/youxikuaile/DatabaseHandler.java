package com.orfid.youxikuaile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/2/28.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "youxikuaile";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_LOGIN = "login";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_UID = "uid";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_PHONE = "phone";

//    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_UID + " INTEGER PRIMARY KEY,"
                + KEY_USERNAME + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_TOKEN + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_PHOTO + " TEXT)";
        db.execSQL(CREATE_LOGIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        onCreate(db);
    }

    public void addUser(String username, String uid, String password, String token, String photo, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LOGIN);
        ContentValues values = new ContentValues();
        values.put(KEY_UID, uid);
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_TOKEN, token);
        values.put(KEY_PHONE, phone);
        values.put(KEY_PHOTO, photo);
        db.insert(TABLE_LOGIN, null, values);
        db.close();
    }

    public void updateUser(String uid, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TOKEN, token);
        db.update(TABLE_LOGIN, values, KEY_UID + "=" + uid, null);
        db.close();
    }

    public HashMap getUserDetails() {
        HashMap user = new HashMap();
        String selectQuery = "SELECT * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("uid", cursor.getString(0));
            user.put("username", cursor.getString(1));
            user.put("password", cursor.getString(2));
            user.put("token", cursor.getString(3));
            user.put("phone", cursor.getString(4));
            user.put("photo", cursor.getString(5));
        }
        cursor.close();
        db.close();
        return user;
    }

    public int getRawCount() {
        String countQuery = "SELECT * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        cursor.close();
        db.close();
        return rowCount;
    }

    public void resetTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }
}
