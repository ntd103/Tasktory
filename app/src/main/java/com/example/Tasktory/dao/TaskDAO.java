package com.example.Tasktory.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.Tasktory.database.DbHelper;
import com.example.Tasktory.model.Task;
import com.example.Tasktory.objects.UserSession;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskDAO {
    private final SQLiteDatabase db;

    public int getUserId() {
        UserSession userSession = UserSession.getInstance();
        return userSession.getUserId();
    }

    public TaskDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();

    }

    private Task parseTask(Cursor cursor) {
        Task task = new Task();
        task.setTaskId(cursor.getInt(cursor.getColumnIndexOrThrow("task_id")));
        task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        task.setNote(cursor.getString(cursor.getColumnIndexOrThrow("note")));
        int dueDateIndex = cursor.getColumnIndexOrThrow("due_date");
        if (!cursor.isNull(dueDateIndex)) {
            task.setDueDate(new Date(cursor.getLong(dueDateIndex)));
        } else {
            task.setDueDate(null);
        }
        int reminderTimeIndex = cursor.getColumnIndexOrThrow("reminder_time");
        if (!cursor.isNull(reminderTimeIndex)) {
            task.setReminderTime(new Date(cursor.getLong(reminderTimeIndex)));
        } else {
            task.setReminderTime(null);
        }
        int isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed"));
        task.setCompleted(isCompleted == 1);
        task.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
        task.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
        return task;
    }

    public Task findById(int taskId) {
        String query = "SELECT * FROM tasks WHERE user_id = ? AND task_id = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(getUserId()), String.valueOf(taskId)})) {
            if (cursor.moveToNext()) {
                return parseTask(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Task> findAll() {
        List<Task> taskList = new ArrayList<>();

        String query = "SELECT * FROM tasks WHERE user_id = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(getUserId())})) {
            while (cursor.moveToNext()) {
                taskList.add(parseTask(cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return taskList;
    }

    public List<Task> findAllByCategoryId(int categoryId) {
        List<Task> taskList = new ArrayList<>();

        String query = "SELECT * FROM tasks WHERE user_id = ? AND category_id = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(getUserId()), String.valueOf(categoryId)})) {
            while (cursor.moveToNext()) {
                taskList.add(parseTask(cursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return taskList;
    }

    public long save(Task task) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", task.getTitle());
        contentValues.put("note", task.getNote());
        if (task.getDueDate() != null) {
            contentValues.put("due_date", task.getDueDate().getTime());
        } else {
            contentValues.putNull("due_date");
        }
        if (task.getReminderTime() != null) {
            contentValues.put("reminder_time", task.getReminderTime().getTime());
        } else {
            contentValues.putNull("reminder_time");
        }
        contentValues.put("user_id", getUserId());
        contentValues.put("category_id", task.getCategoryId());

        return db.insert("tasks", null, contentValues);
    }

    public long updateTaskStatus(int taskId, boolean isCompleted) {
        ContentValues values = new ContentValues();
        values.put("is_completed", isCompleted ? 1 : 0);

        return db.update("tasks", values, "task_id = ?", new String[]{String.valueOf(taskId)});
    }

    public long updateTaskDueDate(int taskId, Date dueDate) {
        ContentValues contentValues = new ContentValues();
        if (dueDate != null) {
            contentValues.put("due_date", dueDate.getTime());
        } else {
            contentValues.putNull("due_date");
        }
        return db.update("tasks", contentValues, "task_id = ?", new String[]{String.valueOf(taskId)});
    }

    public long updateTaskReminderTime(long taskId, Date reminderTime) {
        ContentValues contentValues = new ContentValues();
        if (reminderTime != null) {
            contentValues.put("reminder_time", reminderTime.getTime());
        } else {
            contentValues.putNull("reminder_time");
        }
        return db.update("tasks", contentValues, "task_id = ?", new String[]{String.valueOf(taskId)});
    }

    public long updateTaskCategory(int taskId, int categoryId) {
        ContentValues values = new ContentValues();
        values.put("category_id", categoryId);
        return db.update("tasks", values, "task_id = ?", new String[]{String.valueOf(taskId)});
    }

    public long updateTaskTitleAndNote(int taskId, String newTitle, String newNote) {
        ContentValues values = new ContentValues();
        values.put("title", newTitle);
        values.put("note", newNote);

        return db.update("tasks", values, "task_id = ? AND user_id = ?",
                new String[]{String.valueOf(taskId), String.valueOf(getUserId())});
    }

    public long delete(int taskId) {
        return db.delete("tasks", "task_id = ? AND user_id = ?", new String[]{String.valueOf(taskId), String.valueOf(getUserId())});
    }

    public List<Task> searchTask(String keyword, ArrayList<Integer> selectedTagIds) {
        List<Task> tasks = new ArrayList<>();

        StringBuilder sqlQuery = new StringBuilder("SELECT DISTINCT t.* FROM tasks t ");
        sqlQuery.append("LEFT JOIN tasks_tags tt ON t.task_id = tt.task_id ");
        sqlQuery.append("LEFT JOIN tags tg ON tt.tag_id = tg.tag_id WHERE ");
        sqlQuery.append("(LOWER(t.title) LIKE ? OR LOWER(t.note) LIKE ?) ");
        if (!selectedTagIds.isEmpty()) {
            sqlQuery.append("AND tg.tag_id IN (");
            for (int i = 0; i < selectedTagIds.size(); i++) {
                sqlQuery.append("?");
                if (i < selectedTagIds.size() - 1) {
                    sqlQuery.append(", ");
                }
            }
            sqlQuery.append(") ");
        }
        List<String> selectionArgs = new ArrayList<>();
        selectionArgs.add("%" + keyword.toLowerCase() + "%");
        selectionArgs.add("%" + keyword.toLowerCase() + "%");
        if (!selectedTagIds.isEmpty()) {
            for (Integer tagId : selectedTagIds) {
                selectionArgs.add(String.valueOf(tagId));
            }
        }

        Cursor cursor = db.rawQuery(sqlQuery.toString(), selectionArgs.toArray(new String[0]));

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Task task = parseTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return tasks;
    }

}