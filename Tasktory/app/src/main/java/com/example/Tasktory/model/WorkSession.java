package com.example.Tasktory.model;

public class WorkSession {
    private int id;
    private int userId;
    private long startTime;
    private long endTime;
    private String sessionType; // "work", "short_break", "long_break"

    public WorkSession() {}

    public WorkSession(int id, int userId, long startTime, long endTime, String sessionType) {
        this.id = id;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sessionType = sessionType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
}
