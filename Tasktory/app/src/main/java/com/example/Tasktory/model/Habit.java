package com.example.Tasktory.model;

import java.util.Date;

public class Habit {
    private int id;
    private String name;
    private String frequencyType; // daily, weekly, interval
    private String frequencyData; // For daily: "1010101", For weekly/interval: number of days
    private int completedCount;
    private int userId;
    private boolean isCompleted;
    private long lastResetDate;
    private Date startDate;
    private Integer targetDays; // null means forever
    private boolean allDayGoal;
    private String reminderTime; // Format: "HH:mm" or null if no reminder
    private String note;
    private int timesPerDay; // Number of times per day to complete the habit
    private int weeklyCompletedCount; // Number of times completed in the current week

    public Habit() {}

    public Habit(int id, String name, String frequencyType, String frequencyData, 
                int completedCount, int userId, boolean isCompleted, 
                long lastResetDate, Date startDate, Integer targetDays, boolean allDayGoal,
                String reminderTime, String note, int timesPerDay) {
        this.id = id;
        this.name = name;
        this.frequencyType = frequencyType;
        this.frequencyData = frequencyData;
        this.completedCount = completedCount;
        this.userId = userId;
        this.isCompleted = isCompleted;
        this.lastResetDate = lastResetDate;
        this.startDate = startDate;
        this.targetDays = targetDays;
        this.allDayGoal = allDayGoal;
        this.reminderTime = reminderTime;
        this.note = note;
        this.timesPerDay = timesPerDay;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getFrequencyType() { return frequencyType; }
    public void setFrequencyType(String frequencyType) { this.frequencyType = frequencyType; }
    
    public String getFrequencyData() { return frequencyData; }
    public void setFrequencyData(String frequencyData) { this.frequencyData = frequencyData; }
    
    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    
    public long getLastResetDate() { return lastResetDate; }
    public void setLastResetDate(long lastResetDate) { this.lastResetDate = lastResetDate; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Integer getTargetDays() { return targetDays; }
    public void setTargetDays(Integer targetDays) { this.targetDays = targetDays; }
    
    public boolean isAllDayGoal() { return allDayGoal; }
    public void setAllDayGoal(boolean allDayGoal) { this.allDayGoal = allDayGoal; }
    
    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public int getTimesPerDay() { return timesPerDay; }
    public void setTimesPerDay(int timesPerDay) { this.timesPerDay = timesPerDay; }
    
    public int getWeeklyCompletedCount() { return weeklyCompletedCount; }
    public void setWeeklyCompletedCount(int weeklyCompletedCount) { this.weeklyCompletedCount = weeklyCompletedCount; }
}
