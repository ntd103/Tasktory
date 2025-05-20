package com.example.Tasktory.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.Tasktory.R;
import com.example.Tasktory.dao.TaskDAO;
import com.example.Tasktory.model.Task;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PersonalFragment extends Fragment {

    TextView textViewSelectedDate;
    ArrayList<String> filterList;
    Spinner spinnerFilter;
    ArrayAdapter<String> adapter;
    PieChart chart;
    TaskDAO taskDAO;
    List<Task> tasks;
    TextView txtComp, txtInComp;

    public void getWidget(View view) {
        taskDAO = new TaskDAO(requireContext());
        txtComp = view.findViewById(R.id.txtCompleted);
        txtInComp = view.findViewById(R.id.txtInCompleted);
        textViewSelectedDate = view.findViewById(R.id.textViewSelectedDate);
        spinnerFilter = view.findViewById(R.id.spinnerFilter);
        chart = view.findViewById(R.id.pieChart);
        filterList = new ArrayList<>();
        filterList.add("Chọn ngày");
        filterList.add("Theo ngày");
        //       filterList.add("Tuần qua");
        filterList.add("Theo tháng");

        tasks = taskDAO.findAll();

        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, filterList);
        spinnerFilter.setAdapter(adapter);
        getPieChart(50, 50);
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if (selectedOption.equals("Theo ngày")) {
                    showDatePicker();
                } else if (selectedOption.equals("Tuần qua")) {
                    textViewSelectedDate.setText("Thống kê trong tuần qua. ");

                } else if (selectedOption.equals("Theo tháng")) {
                    showMonthYearPicker();
                    textViewSelectedDate.setText("Thống kê trong tháng: ");
                } else {
                    textViewSelectedDate.setText("Thống kê trong:");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        getWidget(view);

        return view;
    }

    public void getPieChart(int doneValue, int unfinishValue) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(doneValue, "Hoàn thành"));
        entries.add(new PieEntry(unfinishValue, "Chưa hoàn thành"));

        // Create and customize the pie data set
        PieDataSet pieDataSet = new PieDataSet(entries, null);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(16f);
        pieDataSet.setValueTextColor(android.graphics.Color.WHITE);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);

        // Create and set the pie data
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(chart));
        pieData.setValueTextSize(12f);

        // Customize the pie chart
        chart.clear();
        chart.setData(pieData);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(android.graphics.Color.TRANSPARENT);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setCenterTextSize(18f);
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // Disable the legend
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        chart.animateY(1400, Easing.EaseInOutQuad);
        chart.notifyDataSetChanged();

        chart.invalidate();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    Calendar selectedDateCalendar = Calendar.getInstance();
                    selectedDateCalendar.set(year1, month1, dayOfMonth);
                    Date selectedDate = selectedDateCalendar.getTime();

                    String selectedDateString = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    textViewSelectedDate.setText("Thống kê trong: " + selectedDateString);
                    getStatisticByDay(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showMonthYearPicker() {
        final Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_month_year_picker, null);
        final NumberPicker monthPicker = dialogView.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = dialogView.findViewById(R.id.picker_year);

        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setDisplayedValues(new String[]{"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"});
        monthPicker.setValue(currentMonth);

        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(2100);
        yearPicker.setValue(currentYear);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chọn tháng và năm")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    int selectedMonth = monthPicker.getValue();
                    int selectedYear = yearPicker.getValue();
                    String selectedDate = (selectedMonth + 1) + "/" + selectedYear;
                    textViewSelectedDate.setText("Thống kê trong: " + selectedDate);

                    // Create a Date object for the first day of the selected month and year
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(Calendar.YEAR, selectedYear);
                    selectedCalendar.set(Calendar.MONTH, selectedMonth);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, 1);
                    Date filterDate = selectedCalendar.getTime();

                    // Call getStatisticByMonth with the selected date
                    getStatisticByMonth(filterDate);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public void getStatisticByDay(Date filterDate) {
        int inComp = 1, comp = 1;

        Calendar filterCalendar = Calendar.getInstance();
        filterCalendar.setTime(filterDate);

        for (Task task : tasks) {
            Calendar taskCalendar = Calendar.getInstance();
            taskCalendar.setTime(task.getDueDate());

            boolean sameDay = taskCalendar.get(Calendar.YEAR) == filterCalendar.get(Calendar.YEAR) &&
                    taskCalendar.get(Calendar.DAY_OF_YEAR) == filterCalendar.get(Calendar.DAY_OF_YEAR);

            if (sameDay) {
                if (task.isCompleted()) {
                    comp++;
                } else {
                    inComp++;
                }
            }
        }

        txtComp.setText(String.valueOf(comp - 1));
        txtInComp.setText(String.valueOf(inComp - 1));
        if (inComp == 1 && comp == 1) {
            getPieChart(comp, inComp);
        } else {
            getPieChart(comp - 1, inComp - 1);
        }

    }

    public void getStatisticByMonth(Date filterDate) {
        int inComp = 0, comp = 0;

        Calendar filterCalendar = Calendar.getInstance();
        filterCalendar.setTime(filterDate);

        for (Task task : tasks) {
            Calendar taskCalendar = Calendar.getInstance();
            taskCalendar.setTime(task.getDueDate());

            boolean sameMonth = taskCalendar.get(Calendar.YEAR) == filterCalendar.get(Calendar.YEAR) &&
                    taskCalendar.get(Calendar.MONTH) == filterCalendar.get(Calendar.MONTH);

            if (sameMonth) {
                if (task.isCompleted()) {
                    comp++;
                } else {
                    inComp++;
                }
            }
        }

        txtComp.setText(String.valueOf(comp));
        txtInComp.setText(String.valueOf(inComp));
        getPieChart(comp, inComp);
    }

}
