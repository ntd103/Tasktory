package com.example.Tasktory.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Tasktory.R;
import com.example.Tasktory.activity.TaskDetailActivity;
import com.example.Tasktory.dao.CategoryDAO;
import com.example.Tasktory.dao.TaskDAO;
import com.example.Tasktory.dao.TaskTagsDAO;
import com.example.Tasktory.model.Category;
import com.example.Tasktory.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final TaskDAO taskDAO;
    private final CategoryDAO categoryDAO;
    private final TaskTagsDAO taskTagsDAO;
    private final OnTaskStatusChangedListener taskStatusChangedListener;

    public TasksAdapter(List<Task> taskList, TaskDAO taskDAO, CategoryDAO categoryDAO, TaskTagsDAO taskTagsDAO, OnTaskStatusChangedListener taskStatusChangedListener) {
        this.taskList = taskList;
        this.taskDAO = taskDAO;
        this.categoryDAO = categoryDAO;
        this.taskTagsDAO = taskTagsDAO;
        this.taskStatusChangedListener = taskStatusChangedListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, taskDAO, categoryDAO, taskTagsDAO, taskStatusChangedListener);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskTime;
        CheckBox taskCheckBox;
        ImageView imageViewCate;
        TextView textViewCate;
        TextView textViewTags;
        LinearLayout layoutCategoryButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskTime = itemView.findViewById(R.id.taskTime);
            taskCheckBox = itemView.findViewById(R.id.checkbox);
            imageViewCate = itemView.findViewById(R.id.iconCate);
            textViewCate = itemView.findViewById(R.id.nameCate);
            textViewTags = itemView.findViewById(R.id.textTags);
            layoutCategoryButton = itemView.findViewById(R.id.layoutCategoryButton);
        }

        public void bind(Task task, TaskDAO taskDAO, CategoryDAO categoryDAO, TaskTagsDAO taskTagsDAO, OnTaskStatusChangedListener taskStatusChangedListener) {
            taskTitle.setText(task.getTitle());

            Date dueDate = task.getDueDate();
            if (dueDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(dueDate);
                taskTime.setText(formattedDate);
            } else {
                taskTime.setText("Vô thời hạn");
            }

            Category category = categoryDAO.getCategoryById(task.getCategoryId());
            if (category != null) {

                //Xử lý icon
                textViewCate.setText(category.getName());
                if (category.getIcon() != null && !category.getIcon().isEmpty()) {
                    int iconResId = itemView.getContext().getResources().getIdentifier(
                            category.getIcon(), "drawable", itemView.getContext().getPackageName()
                    );

                    if (iconResId != 0) {
                        imageViewCate.setImageResource(iconResId);
                    } else {
                        imageViewCate.setImageResource(R.drawable.ic_block);
                    }
                } else {
                    imageViewCate.setImageResource(R.drawable.ic_block);
                }
            } else {
                textViewCate.setText("Chưa có");
                imageViewCate.setImageResource(R.drawable.ic_block);
            }

            // Xử lý màu
            Drawable originalDrawable = layoutCategoryButton.getBackground();
            GradientDrawable backgroundDrawable;
            if (originalDrawable instanceof GradientDrawable) {
                backgroundDrawable = (GradientDrawable) originalDrawable.mutate();
            } else {
                backgroundDrawable = new GradientDrawable();
            }
            if (category != null && category.getColor() != null && !category.getColor().isEmpty()) {
                int parsedColor = Color.parseColor(category.getColor());
                backgroundDrawable.setColor(parsedColor);
            } else {
                backgroundDrawable.setColor(ContextCompat.getColor(itemView.getContext(), R.color.lavender));
            }

            int tagCount = taskTagsDAO.getTagCountByTaskId(task.getTaskId());
            textViewTags.setText(String.valueOf(tagCount));

            taskCheckBox.setChecked(task.isCompleted());
            taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                long isUpdate = taskDAO.updateTaskStatus(task.getTaskId(), isChecked);
                if (isUpdate > 0) {
                    task.setCompleted(isChecked);

                    if (taskStatusChangedListener != null) {
                        taskStatusChangedListener.onTaskStatusChanged();
                    }
                }
            });

            itemView.setOnClickListener(view -> {
                int taskId = task.getTaskId();
                Intent intent = new Intent(view.getContext(), TaskDetailActivity.class);
                intent.putExtra("task_id", taskId);
                view.getContext().startActivity(intent);
            });
        }
    }

    public interface OnTaskStatusChangedListener {
        void onTaskStatusChanged();
    }

}