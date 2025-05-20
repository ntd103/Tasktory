package com.example.Tasktory.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.Tasktory.database.DbHelper;
import com.example.Tasktory.model.Habit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HabitDAO {
    private DbHelper dbHelper;

    public HabitDAO(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long insert(Habit habit) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", habit.getName());
        values.put("frequency_type", habit.getFrequencyType());
        values.put("frequency_data", habit.getFrequencyData());
        values.put("completed_count", habit.getCompletedCount());
        values.put("user_id", habit.getUserId());
        values.put("is_completed", habit.isCompleted() ? 1 : 0);
        values.put("last_reset_date", habit.getLastResetDate());
        values.put("start_date", habit.getStartDate().getTime());
        values.put("target_days", habit.getTargetDays());
        values.put("all_day_goal", habit.isAllDayGoal() ? 1 : 0);
        values.put("reminder_time", habit.getReminderTime());
        values.put("note", habit.getNote());
        values.put("times_per_day", habit.getTimesPerDay());
        return db.insert("habits", null, values);
    }

    public int update(Habit habit) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", habit.getName());
        values.put("frequency_type", habit.getFrequencyType());
        values.put("frequency_data", habit.getFrequencyData());
        values.put("completed_count", habit.getCompletedCount());
        values.put("is_completed", habit.isCompleted() ? 1 : 0);
        values.put("last_reset_date", habit.getLastResetDate());
        values.put("start_date", habit.getStartDate().getTime());
        values.put("target_days", habit.getTargetDays());
        values.put("all_day_goal", habit.isAllDayGoal() ? 1 : 0);
        values.put("reminder_time", habit.getReminderTime());
        values.put("note", habit.getNote());
        values.put("times_per_day", habit.getTimesPerDay());
        return db.update("habits", values, "id=?", new String[]{String.valueOf(habit.getId())});
    }

    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("habits", "id=?", new String[]{String.valueOf(id)});
    }

    public List<Habit> getAll(int userId) {
        List<Habit> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM habits WHERE user_id=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                try {
                    Habit habit = new Habit();
                    int idIndex = cursor.getColumnIndexOrThrow("id");
                    int nameIndex = cursor.getColumnIndexOrThrow("name");
                    int frequencyTypeIndex = cursor.getColumnIndexOrThrow("frequency_type");
                    int frequencyDataIndex = cursor.getColumnIndexOrThrow("frequency_data");
                    int completedCountIndex = cursor.getColumnIndexOrThrow("completed_count");
                    int userIdIndex = cursor.getColumnIndexOrThrow("user_id");
                    int isCompletedIndex = cursor.getColumnIndexOrThrow("is_completed");
                    int lastResetDateIndex = cursor.getColumnIndexOrThrow("last_reset_date");
                    int startDateIndex = cursor.getColumnIndexOrThrow("start_date");
                    int allDayGoalIndex = cursor.getColumnIndexOrThrow("all_day_goal");
                    int reminderTimeIndex = cursor.getColumnIndexOrThrow("reminder_time");
                    int noteIndex = cursor.getColumnIndexOrThrow("note");
                    int timesPerDayIndex = cursor.getColumnIndexOrThrow("times_per_day");
                    
                    habit.setId(cursor.getInt(idIndex));
                    habit.setName(cursor.getString(nameIndex));
                    habit.setFrequencyType(cursor.getString(frequencyTypeIndex));
                    habit.setFrequencyData(cursor.getString(frequencyDataIndex));
                    habit.setCompletedCount(cursor.getInt(completedCountIndex));
                    habit.setUserId(cursor.getInt(userIdIndex));
                    habit.setCompleted(cursor.getInt(isCompletedIndex) == 1);
                    habit.setLastResetDate(cursor.getLong(lastResetDateIndex));
                    habit.setStartDate(new Date(cursor.getLong(startDateIndex)));
                    habit.setReminderTime(cursor.getString(reminderTimeIndex));
                    habit.setNote(cursor.getString(noteIndex));
                    habit.setTimesPerDay(cursor.getInt(timesPerDayIndex));
                    
                    try {
                        int targetDaysIndex = cursor.getColumnIndexOrThrow("target_days");
                        if (!cursor.isNull(targetDaysIndex)) {
                            habit.setTargetDays(cursor.getInt(targetDaysIndex));
                        }
                    } catch (IllegalArgumentException e) {
                        // target_days column is optional
                    }
                    
                    habit.setAllDayGoal(cursor.getInt(allDayGoalIndex) == 1);
                    list.add(habit);
                } catch (IllegalArgumentException e) {
                    // Log error and skip this record if column not found
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Habit getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM habits WHERE id=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            try {
                Habit habit = new Habit();
                int idIndex = cursor.getColumnIndexOrThrow("id");
                int nameIndex = cursor.getColumnIndexOrThrow("name");
                int frequencyTypeIndex = cursor.getColumnIndexOrThrow("frequency_type");
                int frequencyDataIndex = cursor.getColumnIndexOrThrow("frequency_data");
                int completedCountIndex = cursor.getColumnIndexOrThrow("completed_count");
                int userIdIndex = cursor.getColumnIndexOrThrow("user_id");
                int isCompletedIndex = cursor.getColumnIndexOrThrow("is_completed");
                int lastResetDateIndex = cursor.getColumnIndexOrThrow("last_reset_date");
                int startDateIndex = cursor.getColumnIndexOrThrow("start_date");
                int allDayGoalIndex = cursor.getColumnIndexOrThrow("all_day_goal");
                int reminderTimeIndex = cursor.getColumnIndexOrThrow("reminder_time");
                int noteIndex = cursor.getColumnIndexOrThrow("note");
                int timesPerDayIndex = cursor.getColumnIndexOrThrow("times_per_day");
                
                habit.setId(cursor.getInt(idIndex));
                habit.setName(cursor.getString(nameIndex));
                habit.setFrequencyType(cursor.getString(frequencyTypeIndex));
                habit.setFrequencyData(cursor.getString(frequencyDataIndex));
                habit.setCompletedCount(cursor.getInt(completedCountIndex));
                habit.setUserId(cursor.getInt(userIdIndex));
                habit.setCompleted(cursor.getInt(isCompletedIndex) == 1);
                habit.setLastResetDate(cursor.getLong(lastResetDateIndex));
                habit.setStartDate(new Date(cursor.getLong(startDateIndex)));
                habit.setReminderTime(cursor.getString(reminderTimeIndex));
                habit.setNote(cursor.getString(noteIndex));
                habit.setTimesPerDay(cursor.getInt(timesPerDayIndex));
                
                try {
                    int targetDaysIndex = cursor.getColumnIndexOrThrow("target_days");
                    if (!cursor.isNull(targetDaysIndex)) {
                        habit.setTargetDays(cursor.getInt(targetDaysIndex));
                    }
                } catch (IllegalArgumentException e) {
                    // target_days column is optional
                }
                
                habit.setAllDayGoal(cursor.getInt(allDayGoalIndex) == 1);
                cursor.close();
                return habit;
            } catch (IllegalArgumentException e) {
                // Log error if column not found
                e.printStackTrace();
                cursor.close();
                return null;
            }
        }
        cursor.close();
        return null;
    }
}
