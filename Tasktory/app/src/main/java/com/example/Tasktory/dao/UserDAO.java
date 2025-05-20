package com.example.Tasktory.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.Tasktory.database.DbHelper;
import com.example.Tasktory.model.User;

public class UserDAO {
    private final SQLiteDatabase db;

    public UserDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();

    }

    public boolean checkUser(String username, String password) {
        String sqlQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.rawQuery(sqlQuery, selectionArgs);
        if (cursor.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("Range")
    public int getUserId(String username, String password) {
        String sqlQuery = "SELECT user_id FROM users WHERE username = ? AND password = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.rawQuery(sqlQuery, selectionArgs);
        int userId = 0;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex("user_id"));
        }
        cursor.close();
        return userId;
    }

    @SuppressLint("Range")
    public int getUserIdByEmail(String email) {
        int userId = 0;
        String sqlQuery = "SELECT user_id FROM users WHERE email = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.rawQuery(sqlQuery, selectionArgs);
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex("user_id"));
        }
        cursor.close();
        return userId;
    }

    public boolean checkUserById(int userId, String password) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ? AND password = ?";
        String[] selectionArgs = {String.valueOf(userId), password};
        Cursor cursor = db.rawQuery(sqlQuery, selectionArgs);
        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        return userExists;
    }

    public boolean updatePassword(int userId, String newPassword) {
        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int rowsAffected = db.update("users", values, "user_id = ?", new String[]{String.valueOf(userId)});
        return rowsAffected > 0;
    }

    public long insert(User obj) {
        ContentValues values = new ContentValues();
        values.put("username", obj.getUsername());
        values.put("email", obj.getEmail());
        values.put("password", obj.getPassword());
        return db.insert("users", null, values);
    }

    public boolean checkExistUser(String username) {
        String sqlQuery = "SELECT * FROM users WHERE username = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.rawQuery(sqlQuery, selectionArgs);
        if (cursor.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkExistEmail(String email) {
        String sqlQuery = "SELECT * FROM users WHERE email = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.rawQuery(sqlQuery, selectionArgs);
        if (cursor.getCount() != 0) {
            return true;
        } else {
            return false;
        }
    }
}
