package com.example.Tasktory.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.Tasktory.R;
import com.example.Tasktory.activity.MainActivity;
import com.example.Tasktory.dao.TaskDAO;
import com.example.Tasktory.model.Task;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        int taskId = intent.getIntExtra("taskId", 1);
        boolean isCancel = intent.getBooleanExtra("isCancel", false);

        if (isCancel) {
            cancelNotification(context, taskId);
        } else {
            TaskDAO taskDAO = new TaskDAO(context);
            Task task = taskDAO.findById(taskId);
            if (task == null || task.isCompleted()) {
                return;
            }

            if (title == null) title = "Nhắc nhở nhiệm vụ";
            String message = task.getTitle();

            showNotification(context, taskId, title, message);
        }
    }

    private void showNotification(Context context, int taskId, String title, String message) {
        // Tạo Intent để mở MainActivity khi người dùng nhấp vào thông báo
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Tạo PendingIntent để hoạt động khi nhấp vào thông báo
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "REMINDER_CHANNEL")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Hiển thị thông báo
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(taskId, builder.build());
    }

    private void cancelNotification(Context context, int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }
}
