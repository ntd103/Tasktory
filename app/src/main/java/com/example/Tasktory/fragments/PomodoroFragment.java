package com.example.Tasktory.fragments;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.Tasktory.R;
import com.example.Tasktory.dao.WorkSessionDAO;
import com.example.Tasktory.model.PomodoroSettings;
import com.example.Tasktory.model.TimerState;
import com.example.Tasktory.objects.UserSession;
import com.example.Tasktory.model.WorkSession;

import java.util.Locale;

public class PomodoroFragment extends Fragment implements PomodoroSettingsDialogFragment.OnSettingsChangedListener {
    private static final String CHANNEL_ID = "pomodoro_channel";
    private static final int NOTIFICATION_ID = 1;

    private TextView timerText;
    private TextView sessionText;
    private TextView sessionCountText;
    private ProgressBar timerProgress;
    private Button actionButton, skipButton;

    private PomodoroSettings settings;
    private CountDownTimer timer;
    private TimerState timerState;
    private WorkSessionDAO workSessionDAO;
    private WorkSession currentSession;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        timerState = TimerState.getInstance();
        settings = new PomodoroSettings(requireContext());
        workSessionDAO = new WorkSessionDAO(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        timerText = view.findViewById(R.id.timerText);
        timerProgress = view.findViewById(R.id.timerProgress);
        sessionText = view.findViewById(R.id.sessionText);
        actionButton = view.findViewById(R.id.actionButton);
        skipButton = view.findViewById(R.id.skipButton);

        // Notification channel
        createNotificationChannel();

        // Setup click listeners
        if (actionButton != null) actionButton.setOnClickListener(v -> toggleTimer());
        if (skipButton != null) skipButton.setOnClickListener(v -> skipSession());

        // Initial setup
        setupSession(true);
        updateSessionCount();

        return view;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Pomodoro Timer";
            String description = "Channel for Pomodoro Timer notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(NOTIFICATION_ID, builder.build());
    }

    private void toggleTimer() {
        if (timerState.isRunning()) {
            pauseTimer();
        } else {
            startTimer();
        }
    }

    private void showSettingsDialog() {
        PomodoroSettingsDialogFragment dialog = new PomodoroSettingsDialogFragment();
        dialog.setOnSettingsChangedListener(this);
        dialog.show(getChildFragmentManager(), "PomodoroSettingsDialog");
    }

    @Override
    public void onSettingsChanged() {
        setupSession(timerState.isWorkSession());
    }

    private void setupSession(boolean workSession) {
        if (settings == null) {
            settings = new PomodoroSettings(requireContext());
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        long duration = workSession ? 
            settings.getWorkDuration() * 60 * 1000L : 
            settings.getShortBreakDuration() * 60 * 1000L;

        timerState.resetTimer(duration);
        timerState.setWorkSession(workSession);
        updateTimerUI();
        updateSessionTypeUI();
        updateButtons();
    }

    private void updateSessionCount() {
        View view = getView();
        if (view == null) return;

        TextView sessionCountText = view.findViewById(R.id.sessionCountText);
        TextView progressText = view.findViewById(R.id.progressText);

        if (sessionCountText != null && progressText != null && workSessionDAO != null && settings != null) {
            try {
                int userId = UserSession.getInstance().getUserId();
                int completedSessions = workSessionDAO.getCompletedSessionsCount(userId);
                int targetSessions = settings.getTargetSessions();
                
                sessionCountText.setText("🎯 Completed sessions: " + completedSessions);
                
                if (targetSessions > 0) {
                    int percentage = (completedSessions * 100) / targetSessions;
                    progressText.setText(String.format(Locale.getDefault(),
                        "%d%% of daily target (%d/%d)", 
                        percentage, completedSessions, targetSessions));
                } else {
                    progressText.setText("Set a daily target in settings");
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error updating session count", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pomodoro_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            showSettingsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showClearSessionsDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Clear All Sessions")
            .setMessage("Are you sure you want to delete all completed sessions? This action cannot be undone.")
            .setPositiveButton("Clear", (dialog, which) -> {
                workSessionDAO.deleteAllSessions(UserSession.getInstance().getUserId());
                updateSessionCount();
                Toast.makeText(getContext(), "All sessions cleared", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void startTimer() {
        if (currentSession == null) {
            currentSession = new WorkSession();
            currentSession.setUserId(UserSession.getInstance().getUserId());
            currentSession.setStartTime(System.currentTimeMillis());
            currentSession.setSessionType(timerState.isWorkSession() ? "work" : "break");
        }

        timerState.startTimer();
        startTimerUpdates();
        updateButtons();
    }

    private void startTimerUpdates() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (timerState.isRunning()) {
                    updateTimerUI();
                    if (timerState.getTimeLeftInMillis() > 0) {
                        handler.postDelayed(this, 1000);
                    } else {
                        completeSession();
                    }
                }
            }
        });
    }

    private void pauseTimer() {
        timerState.pauseTimer();
        handler.removeCallbacksAndMessages(null);
        updateButtons();
    }

    private void skipSession() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        handler.removeCallbacksAndMessages(null);
        timerState.setRunning(false);
        completeSession();
        updateButtons();
    }

    private void completeSession() {
        if (timerState.isWorkSession()) {
            if (currentSession != null) {
                currentSession.setEndTime(System.currentTimeMillis());
                workSessionDAO.insert(currentSession);
                currentSession = null;
                updateSessionCount();
            }
            setupSession(false); // Switch to break
        } else {
            setupSession(true); // Switch to work
        }
    }

    private void updateTimerUI() {
        if (timerText != null && timerProgress != null) {
            long timeLeft = timerState.getTimeLeftInMillis();
            int minutes = (int) (timeLeft / 1000) / 60;
            int seconds = (int) (timeLeft / 1000) % 60;
            timerText.setText(String.format("%02d:%02d", minutes, seconds));

            long duration = timerState.isWorkSession() ? settings.getWorkDuration() * 60 * 1000L : settings.getShortBreakDuration() * 60 * 1000L;
            int progress = (int) ((timeLeft * 100) / duration);
            timerProgress.setProgress(progress);
        }
    }

    private void updateSessionTypeUI() {
        if (sessionText != null) {
            if (timerState.isWorkSession()) {
                sessionText.setText("Work Session");
            } else {
                int completedSessions = settings.getCompletedSessions();
                boolean isLongBreak = settings.getSessionsBeforeLongBreak() > 0 && 
                                     completedSessions % settings.getSessionsBeforeLongBreak() == 0;
                sessionText.setText(isLongBreak ? "Long Break" : "Short Break");
            }
        }
    }

    private void updateButtons() {
        if (actionButton != null) {
            actionButton.setText(timerState.isRunning() ? "Pause" : "Start");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
