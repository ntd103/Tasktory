package com.example.Tasktory.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Tasktory.R;
import com.example.Tasktory.adapter.ChooseTagAdapter;
import com.example.Tasktory.dao.TagDAO;
import com.example.Tasktory.dao.TaskTagsDAO;
import com.example.Tasktory.model.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseTagFragment extends DialogFragment {
    private static final String ARG_TASK_ID = "task_id";
    private static final String ARG_TAG_IDS = "tag_ids";
    private Integer taskId;
    private ArrayList<Integer> tagIds = new ArrayList<>();
    private TagDAO tagDAO;
    private TaskTagsDAO taskTagsDAO;
    private List<Tag> selectedTags;
    private ChooseTagAdapter adapter;

    public ChooseTagFragment() {
    }

    public static ChooseTagFragment newInstance(int taskId) {
        ChooseTagFragment fragment = new ChooseTagFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TASK_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    public static ChooseTagFragment newInstance(ArrayList<Integer> tagIds) {
        ChooseTagFragment fragment = new ChooseTagFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList(ARG_TAG_IDS, tagIds);
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
            if (getArguments().containsKey(ARG_TASK_ID)) {
                taskId = getArguments().getInt(ARG_TASK_ID);
            } else if (getArguments().containsKey(ARG_TAG_IDS)) {
                tagIds = getArguments().getIntegerArrayList(ARG_TAG_IDS);
            }
        }

        Context context = requireContext();
        tagDAO = new TagDAO(context);
        taskTagsDAO = new TaskTagsDAO(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_tag, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerTagList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        List<Tag> tags = tagDAO.getAllTags();
        adapter = new ChooseTagAdapter(tags, this::onTagSelected);
        recyclerView.setAdapter(adapter);

        if (taskId != null) {
            selectedTags = taskTagsDAO.getAllByTaskId(taskId);
        } else {
            selectedTags = tags.stream()
                    .filter(tag -> tagIds.contains(tag.getTagId()))
                    .collect(Collectors.toList());
        }
        adapter.setSelectedTags(selectedTags);

        Button cancelButton = view.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(v -> dismiss());

        Button saveButton = view.findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(v -> onSaveClicked());

        return view;
    }

    private void onSaveClicked() {
        Bundle result = new Bundle();
        int[] updatedTagIds = selectedTags.stream().mapToInt(Tag::getTagId).toArray();
        result.putIntArray("updatedTagIds", updatedTagIds);
        getParentFragmentManager().setFragmentResult("UPDATED_TAG", result);

        dismiss();
    }

    private void onTagSelected(Tag tag) {
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag);
        } else {
            selectedTags.add(tag);
        }
        adapter.setSelectedTags(selectedTags);
    }
}