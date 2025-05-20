package com.example.Tasktory.model;

import java.util.Objects;

public class Tag {
    private int tagId;
    private String name;
    private String color;
    private int userId;

    public Tag() {
    }

    public Tag(int tagId, String name, String color, int userId) {
        this.tagId = tagId;
        this.name = name;
        this.color = color;
        this.userId = userId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return tagId == tag.tagId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tagId);
    }

}
