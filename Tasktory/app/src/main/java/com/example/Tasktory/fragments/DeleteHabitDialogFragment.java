package com.example.Tasktory.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.Tasktory.R;
import com.example.Tasktory.model.Habit;

public class DeleteHabitDialogFragment extends DialogFragment {
    private Habit habit;
    private OnHabitDeletedListener listener;

    public interface OnHabitDeletedListener {
        void onHabitDeleted(Habit habit);
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public void setOnHabitDeletedListener(OnHabitDeletedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_habit, null);

        TextView messageText = view.findViewById(R.id.messageText);
        messageText.setText("Are you sure you want to delete habit \"" + habit.getName() + "\"?");

        builder.setView(view)
                .setTitle("Delete Habit")
                .setPositiveButton("Delete", (dialog, id) -> {
                    if (listener != null) {
                        listener.onHabitDeleted(habit);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        return builder.create();
    }
}
