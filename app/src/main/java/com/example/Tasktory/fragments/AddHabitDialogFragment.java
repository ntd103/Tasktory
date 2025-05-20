package com.example.Tasktory.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.widget.Switch;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.Tasktory.R;
import com.example.Tasktory.dao.HabitDAO;
import com.example.Tasktory.model.Habit;
import com.example.Tasktory.objects.UserSession;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddHabitDialogFragment extends DialogFragment {
    private EditText editTextName;
    private TabLayout tabLayout;
    private LinearLayout dailyLayout;
    private LinearLayout weeklyLayout;
    private LinearLayout intervalLayout;
    private ToggleButton[] dayButtons;
    private NumberPicker intervalDaysPicker;
    private CheckBox checkBoxAllDay;
    private EditText editTextStartDate;
    private TextView textViewTargetDays;
    private TextView textViewReminder;
    private NumberPicker numberPickerTimesPerDay;
    private int selectedTargetDays = -1; // -1 means forever
    private String selectedReminderTime = null;
    private HabitDAO habitDAO;
    private OnHabitAddedListener listener;
    private Calendar calendar;
    
    // For edit mode
    private boolean isEditMode = false;
    private Habit habitToEdit = null;

    public interface OnHabitAddedListener {
        void onHabitAdded();
        void onHabitUpdated();
    }

    public void setOnHabitAddedListener(OnHabitAddedListener listener) {
        this.listener = listener;
    }
    
    // Static method to create instance for editing
    public static AddHabitDialogFragment newInstanceForEdit(Habit habit) {
        AddHabitDialogFragment fragment = new AddHabitDialogFragment();
        fragment.isEditMode = true;
        fragment.habitToEdit = habit;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            if (getActivity() == null) {
                return super.onCreateDialog(savedInstanceState);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_add_habit, null);

            if (view == null) {
                throw new IllegalStateException("Failed to inflate dialog layout");
            }

            try {
                initializeViews(view);
                setupTabLayout();
                setupDatePicker();
                setupNumberPickers();
            } catch (Exception e) {
                showError("Lỗi khởi tạo giao diện: " + e.getMessage());
                return super.onCreateDialog(savedInstanceState);
            }

            try {
                habitDAO = new HabitDAO(getContext());
            } catch (Exception e) {
                showError("Lỗi kết nối database: " + e.getMessage());
                return super.onCreateDialog(savedInstanceState);
            }

            // Set title and button text based on mode
            String title = isEditMode ? "Chỉnh sửa thói quen" : "Thêm thói quen mới";
            String positiveButtonText = isEditMode ? "Lưu" : "Thêm";
            
            builder.setView(view)
                   .setTitle(title)
                   .setPositiveButton(positiveButtonText, null)
                   .setNegativeButton("Hủy", (dialog, id) -> dismiss());

            AlertDialog dialog = builder.create();

            dialog.setOnShowListener(dialogInterface -> {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (button != null) {
                    button.setOnClickListener(v -> {
                        try {
                            if (isEditMode) {
                                if (updateHabit()) {
                                    dialog.dismiss();
                                }
                            } else {
                                if (addHabit()) {
                                    dialog.dismiss();
                                }
                            }
                        } catch (Exception e) {
                            String action = isEditMode ? "cập nhật" : "thêm";
                            showError("Lỗi khi " + action + " thói quen: " + e.getMessage());
                        }
                    });
                }
            });

            return dialog;
        } catch (Exception e) {
            showError("Lỗi khởi tạo dialog: " + e.getMessage());
            return super.onCreateDialog(savedInstanceState);
        }
    }

    private void initializeViews(View view) {
        if (view == null) return;

        editTextName = view.findViewById(R.id.editTextHabitName);
        tabLayout = view.findViewById(R.id.tabLayout);
        dailyLayout = view.findViewById(R.id.dailyLayout);
        weeklyLayout = view.findViewById(R.id.weeklyLayout);
        intervalLayout = view.findViewById(R.id.intervalLayout);
        
        // Initialize dayButtons array
        dayButtons = new ToggleButton[7];
        int[] buttonIds = {
            R.id.toggleMonday,
            R.id.toggleTuesday,
            R.id.toggleWednesday,
            R.id.toggleThursday,
            R.id.toggleFriday,
            R.id.toggleSaturday,
            R.id.toggleSunday
        };
        
        // Safely initialize each button
        for (int i = 0; i < buttonIds.length; i++) {
            dayButtons[i] = view.findViewById(buttonIds[i]);
            if (dayButtons[i] == null) {
                // If any button is null, create a new one to prevent NPE
                dayButtons[i] = new ToggleButton(requireContext());
            }
        }

        // daysPerWeekPicker không còn được sử dụng cho weekly layout
        intervalDaysPicker = view.findViewById(R.id.intervalDaysPicker);
        checkBoxAllDay = view.findViewById(R.id.checkBoxAllDay);
        editTextStartDate = view.findViewById(R.id.editTextStartDate);
        textViewTargetDays = view.findViewById(R.id.textViewTargetDays);
        textViewTargetDays.setOnClickListener(v -> showTargetDaysDialog());

        textViewReminder = view.findViewById(R.id.textViewReminder);
        textViewReminder.setOnClickListener(v -> showReminderDialog());

        numberPickerTimesPerDay = view.findViewById(R.id.numberPickerTimesPerDay);
        numberPickerTimesPerDay.setMinValue(1);
        numberPickerTimesPerDay.setMaxValue(10);
        calendar = Calendar.getInstance();
        
        // Nếu đang ở chế độ chỉnh sửa, tải dữ liệu habit lên giao diện
        if (isEditMode && habitToEdit != null) {
            loadHabitData();
        }
    }

    private void setupTabLayout() {
        if (tabLayout == null) return;

        try {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab != null) {
                        updateLayoutVisibility(tab.getPosition());
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
            
            // Nếu đang ở chế độ chỉnh sửa, chọn tab phù hợp với loại thói quen
            if (isEditMode && habitToEdit != null) {
                String frequencyType = habitToEdit.getFrequencyType();
                if ("daily".equals(frequencyType)) {
                    tabLayout.getTabAt(0).select();
                } else if ("weekly".equals(frequencyType)) {
                    tabLayout.getTabAt(1).select();
                } else if ("interval".equals(frequencyType)) {
                    tabLayout.getTabAt(2).select();
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi thiết lập tab: " + e.getMessage());
        }
    }

    private void updateLayoutVisibility(int position) {
        try {
            if (dailyLayout != null) {
                dailyLayout.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            }
            if (weeklyLayout != null) {
                weeklyLayout.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            }
            if (intervalLayout != null) {
                intervalLayout.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            showError("Lỗi khi cập nhật giao diện: " + e.getMessage());
        }
    }

    private void setupDatePicker() {
        if (editTextStartDate == null || calendar == null) return;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            
            // Nếu đang ở chế độ chỉnh sửa và có ngày bắt đầu
            if (isEditMode && habitToEdit != null && habitToEdit.getStartDate() != null) {
                calendar.setTime(habitToEdit.getStartDate());
            }
            
            editTextStartDate.setText(dateFormat.format(calendar.getTime()));

            editTextStartDate.setOnClickListener(v -> {
                try {
                    Context context = getContext();
                    if (context == null) return;

                    DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
                        try {
                            calendar.set(year, month, dayOfMonth);
                            editTextStartDate.setText(dateFormat.format(calendar.getTime()));
                        } catch (Exception e) {
                            showError("Lỗi khi cập nhật ngày: " + e.getMessage());
                        }
                    };

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context,
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.show();
                } catch (Exception e) {
                    showError("Lỗi khi mở bộ chọn ngày: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            showError("Lỗi khi thiết lập ngày: " + e.getMessage());
        }
    }

    private void setupNumberPickers() {
        try {
            // daysPerWeekPicker không còn được sử dụng
            
            if (intervalDaysPicker != null) {
                intervalDaysPicker.setMinValue(1);
                intervalDaysPicker.setMaxValue(365);
                
                // Nếu đang ở chế độ chỉnh sửa và là thói quen khoảng thời gian
                if (isEditMode && habitToEdit != null && "interval".equals(habitToEdit.getFrequencyType())) {
                    try {
                        int interval = Integer.parseInt(habitToEdit.getFrequencyData());
                        intervalDaysPicker.setValue(interval);
                    } catch (Exception e) {
                        // Sử dụng giá trị mặc định nếu có lỗi
                    }
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi cài đặt bộ chọn số: " + e.getMessage());
        }
    }

    private boolean addHabit() {
        try {
            // Kiểm tra null cho các view cần thiết
            if (editTextName == null || tabLayout == null) {
                showError("Lỗi khởi tạo giao diện");
                return false;
            }

            String name = editTextName.getText().toString().trim();
            if (name.isEmpty()) {
                editTextName.setError("Vui lòng nhập tên");
                Toast.makeText(getContext(), "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Kiểm tra UserSession
            UserSession userSession = UserSession.getInstance();
            if (userSession == null || userSession.getUserId() <= 0) {
                showError("Vui lòng đăng nhập lại");
                return false;
            }

            Habit habit = new Habit();
            habit.setName(name);
            habit.setStartDate(calendar.getTime());
            habit.setTargetDays(selectedTargetDays == -1 ? null : selectedTargetDays);
            habit.setAllDayGoal(checkBoxAllDay.isChecked());
            habit.setReminderTime(selectedReminderTime);
            habit.setTimesPerDay(numberPickerTimesPerDay.getValue());
            habit.setLastResetDate(System.currentTimeMillis());
            
            // Xử lý targetDays
            if (selectedTargetDays > 0) {
                habit.setTargetDays(selectedTargetDays);
            }

            // Lưu tần suất dựa trên tab được chọn
            int selectedTab = tabLayout.getSelectedTabPosition();
            switch (selectedTab) {
                case 0: // Hàng ngày
                    // Đối với thói quen hàng ngày, luôn chọn tất cả các ngày
                    habit.setFrequencyData("1111111"); // Tất cả các ngày đều được chọn (1)
                    habit.setFrequencyType("daily");
                    break;

                case 1: // Hàng tuần
                    if (dayButtons == null || dayButtons.length != 7) {
                        showError("Lỗi khởi tạo nút chọn ngày");
                        return false;
                    }

                    StringBuilder selectedDays = new StringBuilder();
                    boolean hasSelectedDay = false;
                    for (ToggleButton button : dayButtons) {
                        if (button == null) continue;
                        boolean isChecked = button.isChecked();
                        selectedDays.append(isChecked ? "1" : "0");
                        if (isChecked) hasSelectedDay = true;
                    }
                    if (!hasSelectedDay) {
                        showError("Vui lòng chọn ít nhất một ngày trong tuần");
                        return false;
                    }
                    habit.setFrequencyData(selectedDays.toString());
                    habit.setFrequencyType("weekly");
                    break;

                case 2: // Khoảng thời gian
                    if (intervalDaysPicker == null) {
                        showError("Lỗi khởi tạo bộ chọn khoảng thời gian");
                        return false;
                    }
                    habit.setFrequencyData(String.valueOf(intervalDaysPicker.getValue()));
                    habit.setFrequencyType("interval");
                    break;

                default:
                    showError("Vui lòng chọn loại tần suất");
                    return false;
            }

            habit.setUserId(userSession.getUserId());
            habit.setCompleted(false);
            habit.setCompletedCount(0);

            try {
                long result = habitDAO.insert(habit);
                if (result > 0) {
                    if (listener != null) {
                        listener.onHabitAdded();
                    }
                    return true;
                } else {
                    showError("Không thể thêm thói quen");
                }
            } catch (Exception e) {
                showError("Lỗi khi lưu thói quen: " + e.getMessage());
            }
        } catch (Exception e) {
            showError("Lỗi không xác định: " + e.getMessage());
        }
        return false;
    }

    private void showTargetDaysDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_target_days, null);
        builder.setView(dialogView);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupTargetDays);
        EditText editTextCustomDays = dialogView.findViewById(R.id.editTextCustomDays);
        LinearLayout customDaysLayout = dialogView.findViewById(R.id.customDaysLayout);
        customDaysLayout.setVisibility(View.GONE);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCustom) {
                customDaysLayout.setVisibility(View.VISIBLE);
            } else {
                customDaysLayout.setVisibility(View.GONE);
            }
        });

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.buttonCancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.buttonOk).setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            String targetDaysText = "Mãi mãi";
            
            if (checkedId == R.id.radioForever) {
                selectedTargetDays = -1;
            } else if (checkedId == R.id.radio7Days) {
                selectedTargetDays = 7;
                targetDaysText = "7 Days";
            } else if (checkedId == R.id.radio21Days) {
                selectedTargetDays = 21;
                targetDaysText = "21 Days";
            } else if (checkedId == R.id.radio30Days) {
                selectedTargetDays = 30;
                targetDaysText = "30 Days";
            } else if (checkedId == R.id.radio100Days) {
                selectedTargetDays = 100;
                targetDaysText = "100 Days";
            } else if (checkedId == R.id.radio365Days) {
                selectedTargetDays = 365;
                targetDaysText = "365 Days";
            } else if (checkedId == R.id.radioCustom) {
                String customDays = editTextCustomDays.getText().toString();
                if (customDays.isEmpty()) {
                    editTextCustomDays.setError("Vui lòng nhập số ngày");
                    return;
                }
                try {
                    int days = Integer.parseInt(customDays);
                    if (days < 1 || days > 999) {
                        editTextCustomDays.setError("Số ngày phải từ 1 đến 999");
                        return;
                    }
                    selectedTargetDays = days;
                    targetDaysText = days + " Days";
                } catch (NumberFormatException e) {
                    editTextCustomDays.setError("Số ngày không hợp lệ");
                    return;
                }
            }

            textViewTargetDays.setText(targetDaysText);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reminder, null);
        builder.setView(dialogView);

        Switch switchReminder = dialogView.findViewById(R.id.switchReminder);
        LinearLayout timePickerLayout = dialogView.findViewById(R.id.timePickerLayout);
        TextView textViewTime = dialogView.findViewById(R.id.textViewTime);

        // Khởi tạo trạng thái
        if (selectedReminderTime != null) {
            switchReminder.setChecked(true);
            textViewTime.setText(selectedReminderTime);
            timePickerLayout.setVisibility(View.VISIBLE);
        } else {
            switchReminder.setChecked(false);
            timePickerLayout.setVisibility(View.GONE);
        }

        // Xử lý sự kiện bật/tắt nhắc nhở
        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timePickerLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                selectedReminderTime = null;
                textViewReminder.setText("Không nhắc nhở");
            }
        });

        // Xử lý chọn thời gian
        textViewTime.setOnClickListener(v -> {
            Calendar currentTime = Calendar.getInstance();
            int hour = 9;
            int minute = 0;
            
            if (selectedReminderTime != null) {
                String[] timeParts = selectedReminderTime.split(":");
                hour = Integer.parseInt(timeParts[0]);
                minute = Integer.parseInt(timeParts[1]);
            }

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute1) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                    textViewTime.setText(time);
                    selectedReminderTime = time;
                    textViewReminder.setText(time);
                },
                hour,
                minute,
                true
            );
            timePickerDialog.show();
        });

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.buttonCancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.buttonOk).setOnClickListener(v -> {
            if (!switchReminder.isChecked()) {
                selectedReminderTime = null;
                textViewReminder.setText("Không nhắc nhở");
            }
            dialog.dismiss();
        });
        
        dialog.show();
    }
    
    private void showError(String message) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton("Đóng", null)
                .show();
    }
    
    /**
     * Tải dữ liệu từ habit đang chỉnh sửa lên giao diện
     */
    private void loadHabitData() {
        try {
            if (habitToEdit == null) return;
            
            // Tải tên thói quen
            if (editTextName != null) {
                editTextName.setText(habitToEdit.getName());
            }
            
            // Tải trạng thái đạt được tất cả
            if (checkBoxAllDay != null) {
                checkBoxAllDay.setChecked(habitToEdit.isAllDayGoal());
            }
            
            // Tải số lần mỗi ngày
            if (numberPickerTimesPerDay != null) {
                numberPickerTimesPerDay.setValue(habitToEdit.getTimesPerDay() > 0 ? habitToEdit.getTimesPerDay() : 1);
            }
            
            // Tải mục tiêu số ngày
            if (textViewTargetDays != null) {
                Integer targetDays = habitToEdit.getTargetDays();
                selectedTargetDays = targetDays != null ? targetDays : -1;
                updateTargetDaysText();
            }
            
            // Tải thời gian nhắc nhở
            if (textViewReminder != null) {
                selectedReminderTime = habitToEdit.getReminderTime();
                updateReminderText();
            }
            
            // Cập nhật trạng thái các nút ngày trong tuần
            String frequencyType = habitToEdit.getFrequencyType();
            if ("weekly".equals(frequencyType) && dayButtons != null) {
                String frequencyData = habitToEdit.getFrequencyData();
                if (frequencyData != null && frequencyData.length() == 7) {
                    for (int i = 0; i < 7 && i < dayButtons.length; i++) {
                        if (dayButtons[i] != null) {
                            char status = frequencyData.charAt(i);
                            dayButtons[i].setChecked(status == '1' || status == '2');
                        }
                    }
                }
            }
        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu thói quen: " + e.getMessage());
        }
    }
    
    /**
     * Cập nhật text hiển thị mục tiêu số ngày
     */
    private void updateTargetDaysText() {
        if (textViewTargetDays == null) return;
        
        String text = "Mãi mãi";
        if (selectedTargetDays > 0) {
            text = selectedTargetDays + " ngày";
        }
        textViewTargetDays.setText(text);
    }
    
    /**
     * Cập nhật text hiển thị thời gian nhắc nhở
     */
    private void updateReminderText() {
        if (textViewReminder == null) return;
        
        String text = "Không nhắc nhở";
        if (selectedReminderTime != null && !selectedReminderTime.isEmpty()) {
            text = selectedReminderTime;
        }
        textViewReminder.setText(text);
    }
    
    /**
     * Cập nhật thói quen hiện tại
     */
    private boolean updateHabit() {
        try {
            // Kiểm tra null cho các view cần thiết
            if (editTextName == null || tabLayout == null || habitToEdit == null) {
                showError("Lỗi khởi tạo giao diện");
                return false;
            }

            String name = editTextName.getText().toString().trim();
            if (name.isEmpty()) {
                editTextName.setError("Vui lòng nhập tên");
                return false;
            }

            // Kiểm tra UserSession
            UserSession userSession = UserSession.getInstance();
            if (userSession == null || userSession.getUserId() <= 0) {
                showError("Vui lòng đăng nhập lại");
                return false;
            }

            // Cập nhật các thuộc tính cơ bản
            habitToEdit.setName(name);
            habitToEdit.setStartDate(calendar.getTime());
            habitToEdit.setTargetDays(selectedTargetDays == -1 ? null : selectedTargetDays);
            habitToEdit.setAllDayGoal(checkBoxAllDay.isChecked());
            habitToEdit.setReminderTime(selectedReminderTime);
            habitToEdit.setTimesPerDay(numberPickerTimesPerDay.getValue());
            
            // Cập nhật tần suất dựa trên tab được chọn
            int selectedTab = tabLayout.getSelectedTabPosition();
            switch (selectedTab) {
                case 0: // Hàng ngày
                    // Đối với thói quen hàng ngày, luôn chọn tất cả các ngày
                    habitToEdit.setFrequencyData("1111111"); // Tất cả các ngày đều được chọn (1)
                    habitToEdit.setFrequencyType("daily");
                    break;

                case 1: // Hàng tuần
                    if (dayButtons == null || dayButtons.length != 7) {
                        showError("Lỗi khởi tạo nút chọn ngày");
                        return false;
                    }

                    StringBuilder selectedDays = new StringBuilder();
                    boolean hasSelectedDay = false;
                    for (ToggleButton button : dayButtons) {
                        if (button == null) continue;
                        boolean isChecked = button.isChecked();
                        selectedDays.append(isChecked ? "1" : "0");
                        if (isChecked) hasSelectedDay = true;
                    }
                    if (!hasSelectedDay) {
                        showError("Vui lòng chọn ít nhất một ngày trong tuần");
                        return false;
                    }
                    habitToEdit.setFrequencyData(selectedDays.toString());
                    habitToEdit.setFrequencyType("weekly");
                    break;

                case 2: // Khoảng thời gian
                    if (intervalDaysPicker == null) {
                        showError("Lỗi khởi tạo bộ chọn khoảng thời gian");
                        return false;
                    }
                    habitToEdit.setFrequencyData(String.valueOf(intervalDaysPicker.getValue()));
                    habitToEdit.setFrequencyType("interval");
                    break;

                default:
                    showError("Vui lòng chọn loại tần suất");
                    return false;
            }

            // Cập nhật habit vào database
            try {
                int result = habitDAO.update(habitToEdit);
                if (result > 0) {
                    if (listener != null) {
                        listener.onHabitUpdated();
                    }
                    return true;
                } else {
                    showError("Không thể cập nhật thói quen");
                }
            } catch (Exception e) {
                showError("Lỗi khi lưu thói quen: " + e.getMessage());
            }
        } catch (Exception e) {
            showError("Lỗi không xác định: " + e.getMessage());
        }
        return false;
    }
}
