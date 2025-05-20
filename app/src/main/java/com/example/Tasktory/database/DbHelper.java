package com.example.Tasktory.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "group05.db";
    private static final int DB_VERSION = 8;

    private static final String CREATE_TABLE_USERS = "CREATE TABLE users (" +
            "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT NOT NULL UNIQUE, " +
            "email TEXT NOT NULL UNIQUE, " +
            "password TEXT NOT NULL" +
            ");";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE categories (" +
            "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "icon TEXT, " +
            "color TEXT, " +
            "user_id INTEGER, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id)" +
            ");";

    private static final String CREATE_TABLE_TAGS = "CREATE TABLE tags (" +
            "tag_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "color TEXT, " +
            "user_id INTEGER, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id)" +
            ");";

    private static final String CREATE_TABLE_TASKS = "CREATE TABLE tasks (" +
            "task_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT, " +
            "note TEXT, " +
            "due_date DATE, " +
            "reminder_time DATE, " +
            "user_id INTEGER, " +
            "category_id INTEGER, " +
            "is_completed INTEGER DEFAULT 0, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id), " +
            "FOREIGN KEY(category_id) REFERENCES categories(category_id)" +
            ");";

    private static final String CREATE_TABLE_TASKS_TAGS = "CREATE TABLE tasks_tags (" +
            "task_tag_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "task_id INTEGER NOT NULL, " +
            "tag_id INTEGER NOT NULL, " +
            "FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE, " +
            "FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE" +
            ");";

    private static final String CREATE_TABLE_HABITS = "CREATE TABLE habits (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "frequency_type TEXT NOT NULL, " +
            "frequency_data TEXT NOT NULL, " +
            "completed_count INTEGER DEFAULT 0, " +
            "user_id INTEGER, " +
            "is_completed INTEGER DEFAULT 0, " +
            "last_reset_date INTEGER DEFAULT 0, " +
            "start_date INTEGER NOT NULL, " +
            "target_days INTEGER, " +
            "all_day_goal INTEGER DEFAULT 0, " +
            "reminder_time TEXT, " +
            "note TEXT, " +
            "times_per_day INTEGER DEFAULT 1, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id)" +
            ");";

    private static final String CREATE_TABLE_WORK_SESSIONS = "CREATE TABLE work_sessions (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER, " +
            "start_time INTEGER, " +
            "end_time INTEGER, " +
            "session_type TEXT, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id)" +
            ");";            

    private static final String CREATE_TABLE_CHALLENGES = "CREATE TABLE challenges (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "description TEXT, " +
            "start_date TEXT, " +
            "end_date TEXT, " +
            "user_id INTEGER, " +
            "is_completed INTEGER DEFAULT 0, " +
            "FOREIGN KEY(user_id) REFERENCES users(user_id)" +
            ");";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_TAGS);
        db.execSQL(CREATE_TABLE_TASKS);
        db.execSQL(CREATE_TABLE_TASKS_TAGS);
        db.execSQL(CREATE_TABLE_HABITS);
        db.execSQL(CREATE_TABLE_CHALLENGES);
        db.execSQL(CREATE_TABLE_WORK_SESSIONS);

        db.execSQL(InitData.INSERT_USERS);
        db.execSQL(InitData.INSERT_CATEGORIES);
        db.execSQL(InitData.INSERT_TAGS);
        db.execSQL(InitData.INSERT_TASKS);
        db.execSQL(InitData.INSERT_TASKS_TAGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE habits ADD COLUMN last_reset_date INTEGER DEFAULT 0;");
            } catch (Exception e) {
                // Column might already exist
            }
        }
        if (oldVersion < 5) {
            try {
                db.execSQL(CREATE_TABLE_WORK_SESSIONS);
            } catch (Exception e) {
                // Table might already exist
            }
        }
        if (oldVersion < 6) {
            try {
                // Rename old table
                db.execSQL("ALTER TABLE habits RENAME TO habits_old;");
                
                // Create new table with updated schema
                db.execSQL(CREATE_TABLE_HABITS);
                
                // Copy data from old table to new table
                db.execSQL("INSERT INTO habits (id, name, frequency_type, frequency_data, completed_count, user_id, is_completed, last_reset_date, start_date, target_days, all_day_goal) " +
                          "SELECT id, name, 'daily' as frequency_type, '1111111' as frequency_data, completed_count, user_id, is_completed, last_reset_date, " +
                          "COALESCE(last_reset_date, strftime('%s', 'now') * 1000) as start_date, NULL as target_days, 0 as all_day_goal FROM habits_old;");
                
                // Drop old table
                db.execSQL("DROP TABLE IF EXISTS habits_old;");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (oldVersion < 7) {
            try {
                // Rename old table
                db.execSQL("ALTER TABLE habits RENAME TO habits_old;");
                
                // Create new table with updated schema
                db.execSQL(CREATE_TABLE_HABITS);
                
                // Copy data from old table to new table
                db.execSQL("INSERT INTO habits (id, name, frequency_type, frequency_data, completed_count, user_id, is_completed, last_reset_date, start_date, target_days, all_day_goal) " +
                          "SELECT id, name, 'daily' as frequency_type, '1111111' as frequency_data, completed_count, user_id, is_completed, last_reset_date, " +
                          "COALESCE(last_reset_date, strftime('%s', 'now') * 1000) as start_date, NULL as target_days, 0 as all_day_goal FROM habits_old;");
                
                // Drop old table
                db.execSQL("DROP TABLE IF EXISTS habits_old;");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (oldVersion < 7) {
            try {
                db.execSQL("ALTER TABLE habits ADD COLUMN reminder_time TEXT;");
                db.execSQL("ALTER TABLE habits ADD COLUMN note TEXT;");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (oldVersion < 8) {
            try {
                db.execSQL("ALTER TABLE habits ADD COLUMN times_per_day INTEGER DEFAULT 1;");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

