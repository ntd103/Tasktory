package com.example.Tasktory.model;

import java.util.Date;

public class Task {
    private int taskId;
    private String title;
    private String note;
    private Date dueDate;
    private Date reminderTime;
    private int userId;
    private int categoryId;
    private boolean isCompleted;

    public Task() {
    }

    public Task(int taskId, String title, String note, Date dueDate, Date reminderTime, int userId, int categoryId, boolean isCompleted) {
        this.taskId = taskId;
        this.title = title;
        this.note = note;
        this.dueDate = dueDate;
        this.reminderTime = reminderTime;
        this.userId = userId;
        this.categoryId = categoryId;
        this.isCompleted = isCompleted;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Date reminderTime) {
        this.reminderTime = reminderTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

}
