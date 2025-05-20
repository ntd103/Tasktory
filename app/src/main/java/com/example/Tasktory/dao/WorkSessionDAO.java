package com.example.Tasktory.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.Tasktory.database.DbHelper;
import com.example.Tasktory.model.WorkSession;

import java.util.ArrayList;
import java.util.List;

public class WorkSessionDAO {
    private DbHelper dbHelper;

    public WorkSessionDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long insert(WorkSession session) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", session.getUserId());
        values.put("start_time", session.getStartTime());
        values.put("end_time", session.getEndTime());
        values.put("session_type", session.getSessionType());
        return db.insert("work_sessions", null, values);
    }

    public List<WorkSession> getAll(int userId) {
        List<WorkSession> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM work_sessions WHERE user_id=? ORDER BY start_time DESC", 
                                  new String[]{String.valueOf(userId)});
        
        if (cursor.moveToFirst()) {
            do {
                WorkSession session = new WorkSession();
                session.setId(cursor.getInt(0));
                session.setUserId(cursor.getInt(1));
                session.setStartTime(cursor.getLong(2));
                session.setEndTime(cursor.getLong(3));
                session.setSessionType(cursor.getString(4));
                list.add(session);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int getCompletedSessionsCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM work_sessions WHERE user_id=? AND session_type=?",
                new String[]{ String.valueOf(userId), "work" }
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public void deleteAllSessions(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("work_sessions", "user_id=?", new String[]{String.valueOf(userId)});
    }

}
