package com.example.Tasktory.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.Tasktory.R;
import com.example.Tasktory.dao.HabitDAO;
import com.example.Tasktory.model.Habit;

import java.util.Calendar;
import java.util.List;

public class HabitAdapter extends ArrayAdapter<Habit> {
    private Context context;
    private List<Habit> habits;
    private HabitDAO habitDAO;
    private OnHabitClickListener habitClickListener;
    private OnHabitEditListener habitEditListener;

    public interface OnHabitClickListener {
        void onHabitClick(Habit habit);
    }
    
    public interface OnHabitEditListener {
        void onHabitEdit(Habit habit);
    }

    public HabitAdapter(@NonNull Context context, List<Habit> habits) {
        super(context, R.layout.item_habit, habits);
        this.context = context;
        this.habits = habits;
        this.habitDAO = new HabitDAO(context);
    }

    public void setOnHabitClickListener(OnHabitClickListener listener) {
        this.habitClickListener = listener;
    }
    
    public void setOnHabitEditListener(OnHabitEditListener listener) {
        this.habitEditListener = listener;
    }

    private void checkAndResetHabit(Habit habit) {
        if (habit == null) return;

        long currentTime = System.currentTimeMillis();
        long lastResetTime = habit.getLastResetDate();

        // If lastResetTime is 0 (never reset before), set it to current time
        if (lastResetTime == 0) {
            habit.setLastResetDate(currentTime);
            habitDAO.update(habit);
            return;
        }

        Calendar lastDate = Calendar.getInstance();
        lastDate.setTimeInMillis(lastResetTime);
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(currentTime);

        // Check if it's a new day (86400000 milliseconds = 24 hours)
        if (currentTime - lastResetTime >= 86400000) {
            // Reset daily completion
            habit.setCompletedCount(0);
            habit.setCompleted(false);

            // Kiểm tra reset tuần mới
            if (lastDate.get(Calendar.WEEK_OF_YEAR) != currentDate.get(Calendar.WEEK_OF_YEAR) ||
                    currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {

                // Reset weekly data
                switch (habit.getFrequencyType()) {
                    case "daily":
                        String days = habit.getFrequencyData();
                        if (days != null && days.length() == 7) {
                            // Reset lại trạng thái các ngày về ban đầu (1 = chọn, 0 = không chọn)
                            StringBuilder newDays = new StringBuilder(days);
                            for (int i = 0; i < 7; i++) {
                                if (newDays.charAt(i) == '2') { // 2 = đã hoàn thành
                                    newDays.setCharAt(i, '1'); // 1 = chọn
                                }
                            }
                            habit.setFrequencyData(newDays.toString());
                        }
                        break;

                    case "weekly":
                        // Reset số lần hoàn thành trong tuần
                        habit.setWeeklyCompletedCount(0);
                        break;
                }
            }

            habit.setLastResetDate(currentTime);
            habitDAO.update(habit);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false);
            holder = new ViewHolder();
            holder.nameText = convertView.findViewById(R.id.habitName);
            holder.descriptionText = convertView.findViewById(R.id.habitDescription);
            holder.completedDaysText = convertView.findViewById(R.id.completedDaysText);
            holder.remainingDaysText = convertView.findViewById(R.id.remainingDaysText);
            holder.weekDaysLayout = convertView.findViewById(R.id.weekDaysLayout);
            holder.progressBar = convertView.findViewById(R.id.progressBar);
            holder.progressText = convertView.findViewById(R.id.progressText);
            holder.checkBox = convertView.findViewById(R.id.checkBoxComplete);
            holder.deleteButton = convertView.findViewById(R.id.deleteButton);

            // Initialize day texts
            holder.dayTexts[0] = convertView.findViewById(R.id.mondayText);
            holder.dayTexts[1] = convertView.findViewById(R.id.tuesdayText);
            holder.dayTexts[2] = convertView.findViewById(R.id.wednesdayText);
            holder.dayTexts[3] = convertView.findViewById(R.id.thursdayText);
            holder.dayTexts[4] = convertView.findViewById(R.id.fridayText);
            holder.dayTexts[5] = convertView.findViewById(R.id.saturdayText);
            holder.dayTexts[6] = convertView.findViewById(R.id.sundayText);

            convertView.setTag(holder);

            // Set click listener for the whole item
            convertView.setOnClickListener(v -> {
                if (habitClickListener != null) {
                    habitClickListener.onHabitClick(getItem(position));
                }
            });
            
            // Set long click listener for editing
            convertView.setOnLongClickListener(v -> {
                if (habitEditListener != null) {
                    habitEditListener.onHabitEdit(getItem(position));
                    return true; // Long click handled
                }
                return false; // Long click not handled
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Habit habit = habits.get(position);
        checkAndResetHabit(habit);
        holder.nameText.setText(habit.getName());

        // Khởi tạo FrequencyData nếu chưa có
        if (habit.getFrequencyData() == null || habit.getFrequencyData().isEmpty()) {
            if ("weekly".equals(habit.getFrequencyType())) {
                // Mặc định tất cả các ngày đều không được chọn (0)
                habit.setFrequencyData("0000000");
                habitDAO.update(habit);
            } else if ("daily".equals(habit.getFrequencyType())) {
                // Mặc định không ngày nào được hoàn thành (0)
                habit.setFrequencyData("0000000");
                habitDAO.update(habit);
            }
        }

        // Set description based on frequency type
        String description = "";
        if ("daily".equals(habit.getFrequencyType())) {
            description = "Thực hiện mỗi ngày";
            // Ẩn weekDaysLayout, chỉ hiện checkbox
            holder.weekDaysLayout.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(habit.isCompleted());
            
            // Khởi tạo dữ liệu nếu chưa có - luôn chọn tất cả các ngày
            if (habit.getFrequencyData() == null || habit.getFrequencyData().isEmpty() || habit.getFrequencyData().length() != 7) {
                habit.setFrequencyData("1111111");
                habitDAO.update(habit);
            }
        } else if ("weekly".equals(habit.getFrequencyType())) {
            description = "Thực hiện theo lịch tuần";
            // Hiện weekDaysLayout và hiện thêm checkbox cho phép hoàn thành cả ngày
            holder.weekDaysLayout.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.VISIBLE);

            // Cập nhật trạng thái các ngày
            String days = habit.getFrequencyData();
            if (days != null && days.length() == 7) {
                for (int i = 0; i < 7; i++) {
                    TextView dayText = holder.dayTexts[i];
                    final int dayIndex = i;

                    // Set click listener cho từng ngày
                    dayText.setOnClickListener(v -> {
                        String currentDays = habit.getFrequencyData();
                        if (currentDays != null && currentDays.length() == 7) {
                            StringBuilder newDays = new StringBuilder(currentDays);
                            char currentStatus = currentDays.charAt(dayIndex);
                            // Toggle between not selected (0), selected (1), and completed (2)
                            char newStatus;
                            if (currentStatus == '0') newStatus = '1';
                            else if (currentStatus == '1') newStatus = '2';
                            else newStatus = '0';


                            newDays.setCharAt(dayIndex, newStatus);
                            habit.setFrequencyData(newDays.toString());
                            habitDAO.update(habit);
                            notifyDataSetChanged();
                        }
                    });

                    // Cập nhật giao diện
                    char status = days.charAt(i);
                    if (status == '2') {
                        dayText.setBackgroundResource(R.drawable.day_background_completed);
                        dayText.setTextColor(context.getResources().getColor(android.R.color.white));
                    } else if (status == '1') {
                        dayText.setBackgroundResource(R.drawable.day_background_selected);
                        dayText.setTextColor(context.getResources().getColor(android.R.color.white));
                    } else {
                        dayText.setBackgroundResource(R.drawable.day_background);
                        dayText.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                    }
                }
            }
        } else if ("interval".equals(habit.getFrequencyType())) {
            description = "Mỗi " + habit.getFrequencyData() + " ngày";
            holder.weekDaysLayout.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        holder.descriptionText.setText(description);

        // Hiển thị tiến trình
        if (habit.getFrequencyType() != null && habit.getFrequencyData() != null) {
            switch (habit.getFrequencyType()) {
                case "daily":
                    holder.completedDaysText.setText(habit.isCompleted() ? "Đã hoàn thành hôm nay" : "Chưa hoàn thành hôm nay");
                    break;

                case "weekly":
                    // Đếm số ngày đã hoàn thành trong tuần
                    int completedDays = 0;
                    for (char c : habit.getFrequencyData().toCharArray()) {
                        if (c == '2') completedDays++;
                    }
                    holder.completedDaysText.setText(String.format("%d ngày đã hoàn thành", completedDays));
                    break;

                case "interval":
                    int intervalDays = Integer.parseInt(habit.getFrequencyData());
                    holder.completedDaysText.setText(String.format("Đã hoàn thành %d lần trong %d ngày",
                            habit.getCompletedCount(), intervalDays));
                    break;
            }
        }

        // Hiển thị số ngày còn lại để hoàn thành mục tiêu
        Integer targetDays = habit.getTargetDays();
        if (targetDays != null && targetDays > 0) {
            long startTime = habit.getStartDate().getTime();
            long currentTime = System.currentTimeMillis();
            long daysPassed = (currentTime - startTime) / (24 * 60 * 60 * 1000);
            int daysRemaining = targetDays - (int)daysPassed;

            if (daysRemaining > 0) {
                holder.remainingDaysText.setText(String.format("%d days remaining", daysRemaining));
            } else {
                holder.remainingDaysText.setText("Goal completed!");
            }
        } else {
            holder.remainingDaysText.setText("No end date");
        }

        // Tính toán target count dựa trên loại tần suất
        int targetCount = getTargetCount(habit);

        int progress = (habit.getCompletedCount() * 100) / targetCount;
        holder.progressBar.setProgress(progress);
        holder.progressText.setText(habit.getCompletedCount() + "/" + targetCount);
        holder.checkBox.setChecked(habit.isCompleted());

        // Kiểm tra và cập nhật trạng thái checkbox
        holder.checkBox.setChecked(habit.isCompleted());

        holder.checkBox.setOnClickListener(v -> {
            // Kiểm tra reset ngày mới
            checkAndResetHabit(habit);

            // Nếu đã hoàn thành trong ngày, không cho bỏ tích
            if (habit.isCompleted()) {
                holder.checkBox.setChecked(true);
                return;
            }

            // Nếu chưa hoàn thành và đang tích
            if (holder.checkBox.isChecked()) {
                // Tăng số lần hoàn thành
                habit.setCompletedCount(habit.getCompletedCount() + 1);

                // Kiểm tra đã đủ số lần trong ngày chưa
                if (habit.getCompletedCount() >= habit.getTimesPerDay()) {
                    // Đã đủ số lần, đánh dấu hoàn thành cho ngày
                    habit.setCompleted(true);

                    // Cập nhật dữ liệu theo loại tần suất
                    switch (habit.getFrequencyType()) {
                        case "daily":
                            // Cập nhật frequencyData cho thói quen hàng ngày
                            String days = habit.getFrequencyData();
                            if (days != null && days.length() == 7) {
                                Calendar calendar = Calendar.getInstance();
                                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                                // Chuyển từ Calendar.DAY_OF_WEEK (1-7, CN là 1) sang index 0-6
                                int index = (dayOfWeek + 5) % 7;

                                // Đánh dấu ngày hiện tại là đã hoàn thành
                                StringBuilder newDays = new StringBuilder(days);
                                newDays.setCharAt(index, '2'); // 2 = hoàn thành, 1 = chọn, 0 = không chọn
                                habit.setFrequencyData(newDays.toString());
                            }
                            break;


                        case "weekly":
                            // Tăng số lần hoàn thành trong tuần
                            habit.setWeeklyCompletedCount(habit.getWeeklyCompletedCount() + 1);
                            
                            // Đánh dấu ngày hiện tại là đã hoàn thành trong tần suất hàng tuần
                            String weeklyDays = habit.getFrequencyData();
                            if (weeklyDays != null && weeklyDays.length() == 7) {
                                Calendar calendar = Calendar.getInstance();
                                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                                // Chuyển từ Calendar.DAY_OF_WEEK (1-7, CN là 1) sang index 0-6
                                int index = (dayOfWeek + 5) % 7;
                                
                                // Chỉ đánh dấu hoàn thành nếu ngày được chọn (giá trị 1)
                                StringBuilder newDays = new StringBuilder(weeklyDays);
                                if (newDays.charAt(index) == '1') {
                                    newDays.setCharAt(index, '2'); // Đánh dấu là đã hoàn thành
                                    habit.setFrequencyData(newDays.toString());
                                    // Thông báo cho người dùng
                                    if (context != null) {
                                        android.widget.Toast.makeText(context, "Đã đánh dấu hoàn thành cho ngày này", android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            break;
                    }
                }

                habitDAO.update(habit);
                notifyDataSetChanged();
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Habit")
                    .setMessage("Are you sure you want to delete \"" + habit.getName() + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        habitDAO.delete(habit.getId());
                        habits.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return convertView;
    }

    private int getTargetCount(Habit habit) {
        if (habit == null) {
            return 1; // Default value
        }

        // Lấy số lần cần hoàn thành trong ngày
        return habit.getTimesPerDay() > 0 ? habit.getTimesPerDay() : 1;
    }

    private static class ViewHolder {
        TextView nameText;
        TextView descriptionText;
        TextView completedDaysText;
        TextView remainingDaysText;
        ProgressBar progressBar;
        TextView progressText;
        CheckBox checkBox;
        ImageButton deleteButton;
        LinearLayout weekDaysLayout;
        TextView[] dayTexts = new TextView[7];
    }
}
