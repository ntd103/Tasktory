package com.example.Tasktory.model;

public class TimerState {
    private static TimerState instance;
    private boolean isRunning;
    private long timeLeftInMillis;
    private long endTime;
    private boolean isWorkSession;

    private TimerState() {
        isRunning = false;
        timeLeftInMillis = 0;
        endTime = 0;
        isWorkSession = true;
    }

    public static TimerState getInstance() {
        if (instance == null) {
            instance = new TimerState();
        }
        return instance;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public long getTimeLeftInMillis() {
        if (isRunning) {
            long currentTime = System.currentTimeMillis();
            timeLeftInMillis = Math.max(0, endTime - currentTime);
        }
        return timeLeftInMillis;
    }

    public void setTimeLeftInMillis(long timeLeftInMillis) {
        this.timeLeftInMillis = timeLeftInMillis;
        if (isRunning) {
            endTime = System.currentTimeMillis() + timeLeftInMillis;
        }
    }

    public boolean isWorkSession() {
        return isWorkSession;
    }

    public void setWorkSession(boolean workSession) {
        isWorkSession = workSession;
    }

    public void startTimer() {
        isRunning = true;
        endTime = System.currentTimeMillis() + timeLeftInMillis;
    }

    public void pauseTimer() {
        isRunning = false;
        timeLeftInMillis = Math.max(0, endTime - System.currentTimeMillis());
    }

    public void resetTimer(long duration) {
        isRunning = false;
        timeLeftInMillis = duration;
        endTime = 0;
    }
}
