package com.example.Tasktory.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.Tasktory.R;
import com.example.Tasktory.dao.CategoryDAO;
import com.example.Tasktory.dao.TaskDAO;
import com.example.Tasktory.dao.TaskTagsDAO;
import com.example.Tasktory.fragments.ChooseCategoryFragment;
import com.example.Tasktory.fragments.ChooseTagFragment;
import com.example.Tasktory.fragments.DeleteTaskFragment;
import com.example.Tasktory.fragments.EditTaskTitleFragment;
import com.example.Tasktory.model.Category;
import com.example.Tasktory.model.Task;
import com.example.Tasktory.utils.ReminderUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity {

    private CheckBox checkBox;
    private ImageView iconCate;
    private TextView textViewTitle;
    private TextView textViewNote;
    private TextView textViewTagCount;
    private TextView textViewNameCate;
    private TextView textViewTime;
    private TextView textViewReminder;

    private TaskTagsDAO taskTagsDAO;
    private TaskDAO taskDAO;
    private CategoryDAO categoryDAO;
    private Task currentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        taskTagsDAO = new TaskTagsDAO(this);
        taskDAO = new TaskDAO(this);
        categoryDAO = new CategoryDAO(this);
        int taskId = getIntent().getIntExtra("task_id", -1);

        ImageView imageViewEditTitleTask = findViewById(R.id.edit_icon);
        LinearLayout layoutCategoryButton = findViewById(R.id.layoutCategoryButton);
        LinearLayout layoutTagButton = findViewById(R.id.layoutTagButton);
        ImageView buttonClose = findViewById(R.id.buttonClose);
        TextView textViewDelete = findViewById(R.id.text_delete);

        checkBox = findViewById(R.id.checkbox);
        iconCate = findViewById(R.id.iconCate);
        textViewTitle = findViewById(R.id.task_title);
        textViewNote = findViewById(R.id.task_subtitle);
        textViewTagCount = findViewById(R.id.textTags);
        textViewNameCate = findViewById(R.id.nameCate);
        textViewTime = findViewById(R.id.text_task_time_value);
        textViewReminder = findViewById(R.id.text_reminder_value);

        loadTaskDetails(taskId);

        getSupportFragmentManager().setFragmentResultListener("UPDATED_TITLE", this, this::handleUpdatedTitle);
        getSupportFragmentManager().setFragmentResultListener("UPDATED_CATEGORY", this, this::handleUpdatedCategory);
        getSupportFragmentManager().setFragmentResultListener("UPDATED_TAG", this, this::handleUpdatedTag);
        getSupportFragmentManager().setFragmentResultListener("DELETED_TASK", this, this::handleDeletedTask);

        imageViewEditTitleTask.setOnClickListener(view -> {
            EditTaskTitleFragment dialogFragment = EditTaskTitleFragment.newInstance(currentTask.getTitle(), currentTask.getNote());
            dialogFragment.show(getSupportFragmentManager(), "EditTaskDialogFragment");
        });

        layoutCategoryButton.setOnClickListener(view -> {
            ChooseCategoryFragment dialogFragment = ChooseCategoryFragment.newInstance(currentTask.getCategoryId());
            dialogFragment.show(getSupportFragmentManager(), "ChooseCategoryFragment");
        });

        layoutTagButton.setOnClickListener(view -> {
            ChooseTagFragment dialogFragment = ChooseTagFragment.newInstance(currentTask.getTaskId());
            dialogFragment.show(getSupportFragmentManager(), "ChooseTagFragment");
        });

        textViewDelete.setOnClickListener(view -> {
            DeleteTaskFragment dialogFragment = DeleteTaskFragment.newInstance(currentTask.getTitle());
            dialogFragment.show(getSupportFragmentManager(), "DeleteTaskFragment");
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            taskDAO.updateTaskStatus(currentTask.getTaskId(), isChecked);

            currentTask.setCompleted(isChecked);
        });

        updateDueDate();
        updateReminderTime();
        textViewTime.setOnLongClickListener(view -> {
            long isUpdated = taskDAO.updateTaskDueDate(currentTask.getTaskId(), null);
            if (isUpdated > 0) {
                textViewTime.setText("Chưa có");
                currentTask.setDueDate(null);
            }
            return false;
        });
        textViewReminder.setOnLongClickListener(view -> {
            long isUpdated = taskDAO.updateTaskReminderTime(currentTask.getTaskId(), null);
            if (isUpdated > 0) {
                textViewReminder.setText("Chưa có");
                currentTask.setReminderTime(null);

                ReminderUtils.cancelReminder(this, currentTask.getTaskId());
            }
            return false;
        });

        buttonClose.setOnClickListener(view -> finish());
    }

    private void updateDueDate() {
        final Calendar calendar = Calendar.getInstance();
        textViewTime.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                    (view1, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                                (view2, hourOfDay, minute) -> {
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute);

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                    String formattedDateTime = sdf.format(calendar.getTime());
                                    textViewTime.setText(formattedDateTime);

                                    long isUpdated = taskDAO.updateTaskDueDate(currentTask.getTaskId(), calendar.getTime());
                                    if (isUpdated > 0) {
                                        currentTask.setDueDate(calendar.getTime());
                                    }
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        );
                        timePickerDialog.show();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void updateReminderTime() {
        final Calendar calendar = Calendar.getInstance();
        textViewReminder.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                    (view1, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                                (view2, hourOfDay, minute) -> {
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute);

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                    String formattedDateTime = sdf.format(calendar.getTime());
                                    textViewReminder.setText(formattedDateTime);

                                    long isUpdated = taskDAO.updateTaskReminderTime(currentTask.getTaskId(), calendar.getTime());
                                    if (isUpdated > 0) {
                                        currentTask.setReminderTime(calendar.getTime());

                                        //Cập nhật lại thông báo
                                        ReminderUtils.cancelReminder(this, currentTask.getTaskId());
                                        ReminderUtils.setReminder(this, currentTask.getReminderTime().getTime(), currentTask.getTaskId(), null);
                                    }
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        );
                        timePickerDialog.show();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void handleUpdatedTitle(String s, Bundle bundle) {
        String newTitle = bundle.getString("updatedTitle");
        String newNote = bundle.getString("updatedNote");

        long isUpdate = taskDAO.updateTaskTitleAndNote(currentTask.getTaskId(), newTitle, newNote);
        if (isUpdate > 0) {
            currentTask.setTitle(newTitle);
            currentTask.setNote(newNote);

            updateViewTitle();
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleUpdatedCategory(String s, Bundle bundle) {
        int newCategoryId = bundle.getInt("updatedCategoryId");

        long isUpdate = taskDAO.updateTaskCategory(currentTask.getTaskId(), newCategoryId);
        if (isUpdate > 0) {
            currentTask.setCategoryId(newCategoryId);

            updateViewCategory();

            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleUpdatedTag(String s, Bundle bundle) {
        int[] newTagIds = bundle.getIntArray("updatedTagIds");

        taskTagsDAO.deleteByTaskId(currentTask.getTaskId());
        if (newTagIds != null && newTagIds.length > 0) {
            taskTagsDAO.create(currentTask.getTaskId(), newTagIds);
        }

        updateViewTags();
    }

    private void handleDeletedTask(String s, Bundle bundle) {
        long isDeleted = taskDAO.delete(currentTask.getTaskId());
        if (isDeleted > 0) {
            Toast.makeText(this, "Đã xóa nhiệm vụ", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi xóa nhiệm vụ", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTaskDetails(int taskId) {
        if (taskId != -1) {
            currentTask = taskDAO.findById(taskId);
            if (currentTask != null) {
                updateViewTaskStatus();
                updateViewTitle();
                updateViewTime();
                updateViewReminderTime();
                updateViewCategory();
                updateViewTags();
            } else {
                Toast.makeText(this, "Task not found", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void updateViewTaskStatus() {
        checkBox.setChecked(currentTask.isCompleted());
    }

    private void updateViewTitle() {
        textViewTitle.setText(currentTask.getTitle());
        textViewNote.setText(currentTask.getNote());
    }

    private void updateViewTime() {
        Date date = currentTask.getDueDate();
        String formattedTime;
        if (date == null) {
            formattedTime = "Chưa có";
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            formattedTime = String.format("%02d:%02d, %02d/%02d/%d", hourOfDay, minute, dayOfMonth, month + 1, year);
        }
        textViewTime.setText(formattedTime);
    }

    private void updateViewReminderTime() {
        Date date = currentTask.getReminderTime();
        String formattedTime;
        if (date == null) {
            formattedTime = "Chưa có";
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            formattedTime = String.format("%02d:%02d, %02d/%02d/%d", hourOfDay, minute, dayOfMonth, month + 1, year);
        }
        textViewReminder.setText(formattedTime);
    }

    private void updateViewTags() {
        int tagCount = taskTagsDAO.getTagCountByTaskId(currentTask.getTaskId());
        textViewTagCount.setText(String.valueOf(tagCount));
    }

    private void updateViewCategory() {
        Category category = categoryDAO.getCategoryById(currentTask.getCategoryId());
        if (category != null) {
            textViewNameCate.setText(category.getName());
            if (category.getIcon() != null && !category.getIcon().isEmpty()) {
                int iconResId = this.getResources().getIdentifier(
                        category.getIcon(), "drawable", this.getPackageName()
                );

                if (iconResId != 0) {
                    iconCate.setImageResource(iconResId);
                } else {
                    iconCate.setImageResource(R.drawable.ic_block);
                }
            } else {
                iconCate.setImageResource(R.drawable.ic_block);
            }
        } else {
            textViewNameCate.setText("Chưa có");
            iconCate.setImageResource(R.drawable.ic_block);
        }
    }
}