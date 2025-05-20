package com.example.Tasktory.model;

import java.util.List;

public class TaskGroup {
    private String title;
    private int color;
    private int textColor;
    private List<Task> taskList;

    public TaskGroup(String title, int color, int textColor, List<Task> taskList) {
        this.title = title;
        this.color = color;
        this.textColor = textColor;
        this.taskList = taskList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
}
