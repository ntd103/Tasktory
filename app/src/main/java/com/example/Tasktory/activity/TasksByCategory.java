package com.example.Tasktory.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Tasktory.R;
import com.example.Tasktory.adapter.TaskGroupAdapter;
import com.example.Tasktory.dao.CategoryDAO;
import com.example.Tasktory.dao.TaskDAO;
import com.example.Tasktory.dao.TaskTagsDAO;
import com.example.Tasktory.model.Task;
import com.example.Tasktory.model.TaskGroup;
import com.example.Tasktory.utils.Utils;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class TasksByCategory extends AppCompatActivity {

    private TextView textViewCategory;
    private ImageView imageViewBack;
    private ImageView imageViewSearch;
    private ImageView imageViewCalendar;
    private ImageView imageViewCateColor;

    private TaskDAO taskDAO;
    private CategoryDAO categoryDAO;
    private TaskTagsDAO taskTagsDAO;

    private TaskGroupAdapter taskGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_by_category);

        taskDAO = new TaskDAO(this);
        categoryDAO = new CategoryDAO(this);
        taskTagsDAO = new TaskTagsDAO(this);

        textViewCategory = findViewById(R.id.textViewCategory);
        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewSearch = findViewById(R.id.imageViewSearch);
        imageViewCalendar = findViewById(R.id.imageViewCalendar);
        imageViewCateColor = findViewById(R.id.imageViewCateColor);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        int categoryId = intent.getIntExtra("category_id", -1);
        String categoryName = intent.getStringExtra("category_name");
        String categoryColor = intent.getStringExtra("category_color");

        if (categoryId != -1) {
            textViewCategory.setText(categoryName);
            if (categoryColor != null) {
                try {
                    imageViewCateColor.setColorFilter(Color.parseColor(categoryColor));
                } catch (IllegalArgumentException e) {
                }
            }

            List<TaskGroup> taskGroups = getTaskGroups(categoryId);
            taskGroupAdapter = new TaskGroupAdapter(taskGroups, taskDAO, categoryDAO, taskTagsDAO, null);
            recyclerView.setAdapter(taskGroupAdapter);

        } else {
            Toast.makeText(this, "Danh mục không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imageViewBack.setOnClickListener(view -> finish());

        imageViewSearch.setOnClickListener(view -> {
            Intent intentSearch = new Intent(TasksByCategory.this, SearchActivity.class);
            startActivity(intentSearch);
        });

        imageViewCalendar.setOnClickListener(view -> {
            Intent intentCalendar = new Intent(TasksByCategory.this, MainActivity.class);
            intentCalendar.putExtra("fragment", "calendar");
            startActivity(intentCalendar);
        });
    }

    private List<TaskGroup> getTaskGroups(int categoryId) {
        List<TaskGroup> parentList = new ArrayList<>();
        List<Task> allTasks = taskDAO.findAllByCategoryId(categoryId);

        List<Task> todayTasks = new ArrayList<>();
        List<Task> tomorrowTasks = new ArrayList<>();
        List<Task> thisWeekTasks = new ArrayList<>();
        List<Task> thisMonthTasks = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate weekStart = today.with(ChronoField.DAY_OF_WEEK, 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());

        for (Task task : allTasks) {
            LocalDate taskDueDate = Utils.convertToLocalDate(task.getDueDate());
            if (taskDueDate.isEqual(today)) {
                todayTasks.add(task);
            } else if (taskDueDate.isEqual(tomorrow)) {
                tomorrowTasks.add(task);
            } else if (!taskDueDate.isBefore(today) && taskDueDate.isBefore(weekEnd.plusDays(1))) {
                thisWeekTasks.add(task);
            } else if (!taskDueDate.isBefore(weekEnd.plusDays(1)) && taskDueDate.isBefore(monthEnd.plusDays(1))) {
                thisMonthTasks.add(task);
            }
        }

        parentList.add(createTaskGroup("Hôm nay", R.color.today_color, R.color.white, todayTasks));
        parentList.add(createTaskGroup("Ngày mai", R.color.tomorrow_color, R.color.white, tomorrowTasks));
        parentList.add(createTaskGroup("Trong suốt tuần", R.color.this_week_color, R.color.white, thisWeekTasks));
        parentList.add(createTaskGroup("Tháng này", R.color.this_month_color, R.color.white, thisMonthTasks));
        parentList.add(createTaskGroup("Tất cả", R.color.all_tasks_color, R.color.white, allTasks));

        return parentList;
    }

    private TaskGroup createTaskGroup(String title, int bgColorRes, int textColorRes, List<Task> tasks) {
        return new TaskGroup(
                title,
                ContextCompat.getColor(this, bgColorRes),
                ContextCompat.getColor(this, textColorRes),
                tasks
        );
    }

}