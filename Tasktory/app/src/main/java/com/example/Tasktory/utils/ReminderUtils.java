package com.example.Tasktory.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.example.Tasktory.receiver.ReminderReceiver;

public class ReminderUtils {

    // Đặt nhắc nhở
    public static void setReminder(Context context, long reminderTimeMillis, int taskId, String title) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("taskId", taskId);
        intent.putExtra("isCancel", false);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = ContextCompat.getSystemService(context, AlarmManager.class);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTimeMillis, pendingIntent);
        }
    }

    // Hủy nhắc nhở
    public static void cancelReminder(Context context, int taskId) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", "");
        intent.putExtra("taskId", taskId);
        intent.putExtra("isCancel", true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = ContextCompat.getSystemService(context, AlarmManager.class);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
