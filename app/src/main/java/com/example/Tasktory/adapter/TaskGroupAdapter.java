package com.example.Tasktory.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Tasktory.R;
import com.example.Tasktory.dao.CategoryDAO;
import com.example.Tasktory.dao.TaskDAO;
import com.example.Tasktory.dao.TaskTagsDAO;
import com.example.Tasktory.model.TaskGroup;

import java.util.List;

public class TaskGroupAdapter extends RecyclerView.Adapter<TaskGroupAdapter.ParentViewHolder> {

    private final List<TaskGroup> taskGroupList;
    private final TaskDAO taskDAO;
    private final CategoryDAO categoryDAO;
    private final TaskTagsDAO taskTagsDAO;
    private final TasksAdapter.OnTaskStatusChangedListener taskStatusChangedListener;

    public TaskGroupAdapter(List<TaskGroup> taskGroupList, TaskDAO taskDAO, CategoryDAO categoryDAO, TaskTagsDAO taskTagsDAO, TasksAdapter.OnTaskStatusChangedListener taskStatusChangedListener) {
        this.taskGroupList = taskGroupList;
        this.taskDAO = taskDAO;
        this.categoryDAO = categoryDAO;
        this.taskTagsDAO = taskTagsDAO;
        this.taskStatusChangedListener = taskStatusChangedListener;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_group_item, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParentViewHolder holder, int position) {
        TaskGroup taskGroup = taskGroupList.get(position);
        holder.bind(taskGroup, taskDAO, categoryDAO, taskTagsDAO, taskStatusChangedListener);
    }

    @Override
    public int getItemCount() {
        return taskGroupList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTaskGroups(List<TaskGroup> newTaskGroups) {
        this.taskGroupList.clear();
        this.taskGroupList.addAll(newTaskGroups);
        notifyDataSetChanged();
    }

    public static class ParentViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout header;
        private final TextView parentTitle;
        private final TextView itemCount;
        private final ImageView arrowIcon;
        private final RecyclerView childRecyclerView;
        private boolean isExpanded = false;

        public ParentViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
            parentTitle = itemView.findViewById(R.id.parentTitle);
            itemCount = itemView.findViewById(R.id.itemCount);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
            childRecyclerView = itemView.findViewById(R.id.childRecyclerView);

            itemView.setOnClickListener(v -> {
                isExpanded = !isExpanded;
                childRecyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                arrowIcon.setImageResource(isExpanded ? R.drawable.baseline_keyboard_arrow_down_24 : R.drawable.baseline_keyboard_arrow_right_24);
            });
        }

        public void bind(TaskGroup taskGroup, TaskDAO taskDAO, CategoryDAO categoryDAO, TaskTagsDAO taskTagsDAO, TasksAdapter.OnTaskStatusChangedListener taskStatusChangedListener) {
            header.setBackgroundColor(taskGroup.getColor());

            parentTitle.setText(taskGroup.getTitle());
            parentTitle.setTextColor(taskGroup.getTextColor());

            int childCount = taskGroup.getTaskList().size();
            itemCount.setText(String.valueOf(childCount));
            itemCount.setTextColor(taskGroup.getTextColor());

            arrowIcon.setImageResource(isExpanded ? R.drawable.baseline_keyboard_arrow_down_24 : R.drawable.baseline_keyboard_arrow_right_24);
            arrowIcon.setColorFilter(taskGroup.getTextColor());

            childRecyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            childRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            TasksAdapter childAdapter = new TasksAdapter(taskGroup.getTaskList(), taskDAO, categoryDAO, taskTagsDAO, taskStatusChangedListener);
            childRecyclerView.setAdapter(childAdapter);
        }
    }
}
