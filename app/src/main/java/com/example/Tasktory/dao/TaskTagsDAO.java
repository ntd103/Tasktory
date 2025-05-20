package com.example.Tasktory.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.Tasktory.database.DbHelper;
import com.example.Tasktory.model.Tag;
import com.example.Tasktory.objects.UserSession;

import java.util.ArrayList;
import java.util.List;

public class TaskTagsDAO {
    private final SQLiteDatabase db;

    public int getUserId() {
        UserSession userSession = UserSession.getInstance();
        return userSession.getUserId();
    }

    public TaskTagsDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();

    }

    public void create(int taskId, int[] selectedTags) {
        db.beginTransaction();
        try {
            for (int tagId : selectedTags) {
                ContentValues values = new ContentValues();
                values.put("task_id", taskId);
                values.put("tag_id", tagId);

                db.insertOrThrow("tasks_tags", null, values);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public List<Tag> getAllByTaskId(int taskId) {
        List<Tag> tags = new ArrayList<>();

        String query = "SELECT t.tag_id, t.name, t.color FROM tags t " +
                "JOIN tasks_tags tt ON t.tag_id = tt.tag_id " +
                "WHERE tt.task_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(taskId)});

        try {
            while (cursor.moveToNext()) {
                int tagId = cursor.getInt(cursor.getColumnIndexOrThrow("tag_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String color = cursor.getString(cursor.getColumnIndexOrThrow("color"));

                Tag tag = new Tag(tagId, name, color, getUserId());
                tags.add(tag);
            }
        } finally {
            cursor.close();
        }

        return tags;
    }

    public void deleteByTaskId(int taskId) {
        String query = "DELETE FROM tasks_tags WHERE task_id =?";
        db.execSQL(query, new String[]{String.valueOf(taskId)});
    }

    public int getTagCountByTaskId(int taskId) {
        int count = 0;
        String query = "SELECT COUNT(*) FROM tasks_tags WHERE task_id = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(taskId)});
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
}
