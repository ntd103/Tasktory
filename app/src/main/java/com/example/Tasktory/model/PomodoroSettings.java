package com.example.Tasktory.model;

import android.content.Context;
import android.content.SharedPreferences;

public class PomodoroSettings {
    private static final String PREF_NAME = "pomodoro_settings";
    private static final String KEY_WORK_DURATION = "work_duration";
    private static final String KEY_SHORT_BREAK_DURATION = "short_break_duration";
    private static final String KEY_LONG_BREAK_DURATION = "long_break_duration";
    private static final String KEY_SESSIONS_BEFORE_LONG_BREAK = "sessions_before_long_break";
    private static final String KEY_COMPLETED_SESSIONS = "completed_sessions";
    private static final String KEY_AUTO_START_BREAKS = "auto_start_breaks";
    private static final String KEY_AUTO_START_WORK = "auto_start_work";
    private static final String KEY_TARGET_SESSIONS = "target_sessions";

    private SharedPreferences preferences;

    public PomodoroSettings(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setWorkDuration(int minutes) {
        preferences.edit().putInt(KEY_WORK_DURATION, minutes).apply();
    }

    public int getWorkDuration() {
        return preferences.getInt(KEY_WORK_DURATION, 25); // Default 25 minutes
    }

    public void setShortBreakDuration(int minutes) {
        preferences.edit().putInt(KEY_SHORT_BREAK_DURATION, minutes).apply();
    }

    public int getShortBreakDuration() {
        return preferences.getInt(KEY_SHORT_BREAK_DURATION, 5); // Default 5 minutes
    }

    public void setLongBreakDuration(int minutes) {
        preferences.edit().putInt(KEY_LONG_BREAK_DURATION, minutes).apply();
    }

    public int getLongBreakDuration() {
        return preferences.getInt(KEY_LONG_BREAK_DURATION, 15); // Default 15 minutes
    }

    public void setSessionsBeforeLongBreak(int sessions) {
        preferences.edit().putInt(KEY_SESSIONS_BEFORE_LONG_BREAK, sessions).apply();
    }

    public int getSessionsBeforeLongBreak() {
        return preferences.getInt(KEY_SESSIONS_BEFORE_LONG_BREAK, 4); // Default 4 sessions
    }

    public void setCompletedSessions(int sessions) {
        preferences.edit().putInt(KEY_COMPLETED_SESSIONS, sessions).apply();
    }

    public int getCompletedSessions() {
        return preferences.getInt(KEY_COMPLETED_SESSIONS, 0);
    }

    public void incrementCompletedSessions() {
        int current = getCompletedSessions();
        setCompletedSessions(current + 1);
    }

    public void setAutoStartBreaks(boolean autoStart) {
        preferences.edit().putBoolean(KEY_AUTO_START_BREAKS, autoStart).apply();
    }

    public boolean getAutoStartBreaks() {
        return preferences.getBoolean(KEY_AUTO_START_BREAKS, true);
    }

    public void setAutoStartWork(boolean autoStart) {
        preferences.edit().putBoolean(KEY_AUTO_START_WORK, autoStart).apply();
    }

    public boolean getAutoStartWork() {
        return preferences.getBoolean(KEY_AUTO_START_WORK, false);
    }

    public void setTargetSessions(int target) {
        preferences.edit().putInt(KEY_TARGET_SESSIONS, target).apply();
    }

    public int getTargetSessions() {
        return preferences.getInt(KEY_TARGET_SESSIONS, 8); // Default 8 sessions per day
    }
}
