package com.example.Tasktory.objects;

public class UserSession {
    private static UserSession instance;
    private int userId;

    private static class SingletonHelper {
        private static final UserSession INSTANCE = new UserSession();
    }

    public static UserSession getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void clearSession() {
        userId = 0;
    }

}
