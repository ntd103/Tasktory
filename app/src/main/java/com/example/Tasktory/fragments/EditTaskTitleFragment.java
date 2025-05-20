package com.example.Tasktory.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.Tasktory.R;

public class EditTaskTitleFragment extends DialogFragment {

    private static final String ARG_TASK_TITLE = "task_title";
    private static final String ARG_TASK_NOTE = "task_note";
    private String taskTitle;
    private String taskNote;
    private EditText editTaskTitle;
    private EditText editTaskNote;

    public static EditTaskTitleFragment newInstance(String currentTitle, String currentNote) {
        EditTaskTitleFragment fragment = new EditTaskTitleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TASK_TITLE, currentTitle);
        args.putString(ARG_TASK_NOTE, currentNote);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int width = (int) (displayMetrics.widthPixels * 0.9);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskTitle = getArguments().getString(ARG_TASK_TITLE, "");
            taskNote = getArguments().getString(ARG_TASK_NOTE, "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_task_title, container, false);

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        editTaskTitle = view.findViewById(R.id.editTaskTitle);
        TextView textViewNote = view.findViewById(R.id.textViewNote);
        editTaskNote = view.findViewById(R.id.editTaskNote);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        if (!taskTitle.isEmpty()) {
            textViewTitle.setText(taskTitle);
            editTaskTitle.setText(taskTitle);
        }
        if (!taskNote.isEmpty()) {
            textViewNote.setText(taskNote);
            editTaskNote.setText(taskNote);
        }

        textViewTitle.setOnClickListener(v -> {
            textViewTitle.setVisibility(View.GONE);
            editTaskTitle.setVisibility(View.VISIBLE);
            editTaskTitle.setText(textViewTitle.getText());

            textViewNote.setVisibility(View.VISIBLE);
            editTaskNote.setVisibility(View.GONE);
            textViewNote.setText(editTaskNote.getText());
        });

        textViewNote.setOnClickListener(v -> {
            textViewNote.setVisibility(View.GONE);
            editTaskNote.setVisibility(View.VISIBLE);
            editTaskNote.setText(textViewNote.getText());

            textViewTitle.setVisibility(View.VISIBLE);
            editTaskTitle.setVisibility(View.GONE);
            textViewTitle.setText(editTaskTitle.getText());
        });

        cancelButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> onSaveClicked());

        return view;
    }

    private void onSaveClicked() {
        Bundle result = new Bundle();
        result.putString("updatedTitle", editTaskTitle.getText().toString());
        result.putString("updatedNote", editTaskNote.getText().toString());
        getParentFragmentManager().setFragmentResult("UPDATED_TITLE", result);

        dismiss();
    }

}
