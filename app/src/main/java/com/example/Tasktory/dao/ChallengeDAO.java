package com.example.Tasktory.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.Tasktory.database.DbHelper;
import com.example.Tasktory.model.Challenge;

import java.util.ArrayList;
import java.util.List;

public class ChallengeDAO {
    private DbHelper dbHelper;

    public ChallengeDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long insert(Challenge challenge) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", challenge.getName());
        values.put("description", challenge.getDescription());
        values.put("start_date", challenge.getStartDate());
        values.put("end_date", challenge.getEndDate());
        values.put("user_id", challenge.getUserId());
        values.put("is_completed", challenge.isCompleted() ? 1 : 0);
        return db.insert("challenges", null, values);
    }

    public int update(Challenge challenge) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", challenge.getName());
        values.put("description", challenge.getDescription());
        values.put("start_date", challenge.getStartDate());
        values.put("end_date", challenge.getEndDate());
        values.put("is_completed", challenge.isCompleted() ? 1 : 0);
        return db.update("challenges", values, "id=?", new String[]{String.valueOf(challenge.getId())});
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("challenges", "id=?", new String[]{String.valueOf(id)});
    }

    public List<Challenge> getAll(int userId) {
        List<Challenge> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM challenges WHERE user_id=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                Challenge challenge = new Challenge();
                challenge.setId(cursor.getInt(0));
                challenge.setName(cursor.getString(1));
                challenge.setDescription(cursor.getString(2));
                challenge.setStartDate(cursor.getString(3));
                challenge.setEndDate(cursor.getString(4));
                challenge.setUserId(cursor.getInt(5));
                challenge.setCompleted(cursor.getInt(6) == 1);
                list.add(challenge);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Challenge getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM challenges WHERE id=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            Challenge challenge = new Challenge();
            challenge.setId(cursor.getInt(0));
            challenge.setName(cursor.getString(1));
            challenge.setDescription(cursor.getString(2));
            challenge.setStartDate(cursor.getString(3));
            challenge.setEndDate(cursor.getString(4));
            challenge.setUserId(cursor.getInt(5));
            challenge.setCompleted(cursor.getInt(6) == 1);
            cursor.close();
            return challenge;
        }
        cursor.close();
        return null;
    }
}
