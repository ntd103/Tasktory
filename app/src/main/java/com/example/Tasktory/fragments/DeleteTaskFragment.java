package com.example.Tasktory.fragments;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.Tasktory.R;

public class DeleteTaskFragment extends DialogFragment {

    private static final String ARG_TASK_TITLE = "task_title";
    private String taskTitle;

    public DeleteTaskFragment() {
    }

    public static DeleteTaskFragment newInstance(String title) {
        DeleteTaskFragment fragment = new DeleteTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TASK_TITLE, title);
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
            taskTitle = getArguments().getString(ARG_TASK_TITLE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_task, container, false);

        TextView textViewTitle = view.findViewById(R.id.task_title);
        textViewTitle.setText(String.format("Tiêu đề: %s", taskTitle));

        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> dismiss());

        Button buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(v -> deleteTask());

        return view;
    }

    private void deleteTask() {
        Bundle result = new Bundle();
        getParentFragmentManager().setFragmentResult("DELETED_TASK", result);

        dismiss();
    }
}