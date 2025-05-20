package com.example.Tasktory.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.Tasktory.R;
import com.example.Tasktory.model.PomodoroSettings;

public class PomodoroSettingsDialogFragment extends DialogFragment {
    private PomodoroSettings settings;
    private NumberPicker workDurationPicker;
    private NumberPicker shortBreakPicker;
    private NumberPicker longBreakPicker;
    private NumberPicker sessionsBeforeLongBreakPicker;
    private NumberPicker targetSessionsPicker;
    private Switch autoStartBreaksSwitch;
    private Switch autoStartWorkSwitch;
    private OnSettingsChangedListener listener;

    public interface OnSettingsChangedListener {
        void onSettingsChanged();
    }

    public void setOnSettingsChangedListener(OnSettingsChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pomodoro_settings, null);

        settings = new PomodoroSettings(requireContext());

        // Initialize pickers
        workDurationPicker = view.findViewById(R.id.workDurationPicker);
        shortBreakPicker = view.findViewById(R.id.shortBreakPicker);
        longBreakPicker = view.findViewById(R.id.longBreakPicker);
        sessionsBeforeLongBreakPicker = view.findViewById(R.id.sessionsBeforeLongBreakPicker);
        targetSessionsPicker = view.findViewById(R.id.targetSessionsPicker);
        autoStartBreaksSwitch = view.findViewById(R.id.autoStartBreaksSwitch);
        autoStartWorkSwitch = view.findViewById(R.id.autoStartWorkSwitch);

        // Set ranges and current values
        setupNumberPicker(workDurationPicker, 1, 60, settings.getWorkDuration());
        setupNumberPicker(shortBreakPicker, 1, 30, settings.getShortBreakDuration());
        setupNumberPicker(longBreakPicker, 1, 60, settings.getLongBreakDuration());
        setupNumberPicker(sessionsBeforeLongBreakPicker, 1, 10, settings.getSessionsBeforeLongBreak());
        setupNumberPicker(targetSessionsPicker, 1, 20, settings.getTargetSessions());

        // Set up switches
        autoStartBreaksSwitch.setChecked(settings.getAutoStartBreaks());
        autoStartWorkSwitch.setChecked(settings.getAutoStartWork());

        builder.setView(view)
                .setTitle("Pomodoro Settings")
                .setPositiveButton("Save", (dialog, id) -> {
                    // Save settings
                    settings.setWorkDuration(workDurationPicker.getValue());
                    settings.setShortBreakDuration(shortBreakPicker.getValue());
                    settings.setLongBreakDuration(longBreakPicker.getValue());
                    settings.setSessionsBeforeLongBreak(sessionsBeforeLongBreakPicker.getValue());
                    settings.setTargetSessions(targetSessionsPicker.getValue());
                    settings.setAutoStartBreaks(autoStartBreaksSwitch.isChecked());
                    settings.setAutoStartWork(autoStartWorkSwitch.isChecked());

                    if (listener != null) {
                        listener.onSettingsChanged();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog
                    dialog.cancel();
                });

        return builder.create();
    }

    private void setupNumberPicker(NumberPicker picker, int min, int max, int value) {
        if (picker != null) {
            picker.setMinValue(min);
            picker.setMaxValue(max);
            picker.setValue(value);
            picker.setWrapSelectorWheel(false);
        }
    }
}
