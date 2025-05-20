package com.example.Tasktory.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Tasktory.R;
import com.example.Tasktory.adapter.TasksAdapter;
import com.example.Tasktory.dao.CategoryDAO;
import com.example.Tasktory.dao.TagDAO;
import com.example.Tasktory.dao.TaskDAO;
import com.example.Tasktory.dao.TaskTagsDAO;
import com.example.Tasktory.fragments.ChooseTagFragment;
import com.example.Tasktory.model.Tag;
import com.example.Tasktory.model.Task;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private TagDAO tagDAO;
    private TaskDAO taskDAO;

    private EditText searchBar;
    private TasksAdapter tasksAdapter;
    private LinearLayout tagsContainer;
    private TextView noResultsTextView;

    private final ArrayList<Integer> selectedTagIds = new ArrayList<>();
    private final List<Task> filteredTasks = new ArrayList<>();

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        taskDAO = new TaskDAO(this);
        tagDAO = new TagDAO(this);
        CategoryDAO categoryDAO = new CategoryDAO(this);
        TaskTagsDAO taskTagsDAO = new TaskTagsDAO(this);

        searchBar = findViewById(R.id.editTextSearch);
        tagsContainer = findViewById(R.id.linearLayoutSelectedTags);
        noResultsTextView = findViewById(R.id.textViewNoResults);
        ImageView backButton = findViewById(R.id.imageViewBack);
        Button btnSelectTag = findViewById(R.id.btnSelectTag);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tasksAdapter = new TasksAdapter(filteredTasks, taskDAO, categoryDAO, taskTagsDAO, null);
        recyclerView.setAdapter(tasksAdapter);

        backButton.setOnClickListener(v -> finish());
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hủy tìm kiếm trước đó
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Lập lịch tìm kiếm sau 300ms
                searchRunnable = () -> filterTasks();
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        getSupportFragmentManager().setFragmentResultListener("UPDATED_TAG", this, this::handleUpdatedTag);

        btnSelectTag.setOnClickListener(v -> {
            ChooseTagFragment dialogFragment = ChooseTagFragment.newInstance(selectedTagIds);
            dialogFragment.show(getSupportFragmentManager(), "ChooseTagFragment");
        });
    }

    private void handleUpdatedTag(String s, Bundle bundle) {
        int[] newTagIds = bundle.getIntArray("updatedTagIds");
        if (newTagIds != null) {
            selectedTagIds.clear();
            for (int tagId : newTagIds) {
                selectedTagIds.add(tagId);
            }
        }
        renderTags();
        filterTasks();
    }

    private void renderTags() {
        tagsContainer.removeAllViews();

        for (int tagId : selectedTagIds) {
            Tag tag = tagDAO.getTagById(tagId);
            if (tag == null) {
                continue;
            }
            TextView tagView = new TextView(this);
            tagView.setText(tag.getName());
            tagView.setPadding(16, 8, 16, 8);
            tagView.setBackground(ContextCompat.getDrawable(this, R.drawable.tn_tag_background));

            GradientDrawable backgroundDrawable = (GradientDrawable) tagView.getBackground();
            String color = tag.getColor();
            if (color != null && !color.isEmpty()) {
                int parsedColor = Color.parseColor(color);
                backgroundDrawable.setColor(parsedColor);
            } else {
                backgroundDrawable.setColor(ContextCompat.getColor(this, R.color.dark_gray));
            }

            tagView.setTextColor(Color.WHITE);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 8, 0);
            tagView.setLayoutParams(layoutParams);

            tagsContainer.addView(tagView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateTaskList(List<Task> tasks) {
        filteredTasks.clear();
        filteredTasks.addAll(tasks);
        tasksAdapter.notifyDataSetChanged();

        if (tasks.isEmpty()) {
            noResultsTextView.setVisibility(View.VISIBLE);
        } else {
            noResultsTextView.setVisibility(View.GONE);
        }
    }

    private void filterTasks() {
        String keyword = searchBar.getText().toString().toLowerCase();
        List<Task> allTasks = taskDAO.searchTask(keyword, selectedTagIds);

        updateTaskList(allTasks);
    }

}