package com.example.Tasktory.fragments;

import static com.example.Tasktory.utils.CalendarUtils.daysInWeekArray;
import static com.example.Tasktory.utils.CalendarUtils.monthYearFromDate;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Tasktory.R;
import com.example.Tasktory.adapter.CalendarAdapter;
import com.example.Tasktory.adapter.TaskArrayAdapter;
import com.example.Tasktory.dao.TaskDAO;
import com.example.Tasktory.model.Task;
import com.example.Tasktory.utils.CalendarUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private Button btnNext, btnPrev;
    private ListView lvTask;
    private TaskDAO taskDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        initWidgets(view);
        CalendarUtils.selectedDate = LocalDate.now();
        Date selectedDate = java.sql.Date.valueOf(CalendarUtils.selectedDate.toString());
        setTasksForSelectedDate(selectedDate);
        setWeekView();
        return view;
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        btnNext = view.findViewById(R.id.btnNextWeek);
        btnPrev = view.findViewById(R.id.btnPrevWeek);
        taskDAO = new TaskDAO(view.getContext());
        lvTask = view.findViewById(R.id.lvTask);

        monthYearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousWeekAction(view);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWeekAction(view);
            }
        });
    }

    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
        Date selectedDate = java.sql.Date.valueOf(CalendarUtils.selectedDate.toString());
        setTasksForSelectedDate(selectedDate);
    }

    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
        Date selectedDate = java.sql.Date.valueOf(CalendarUtils.selectedDate.toString());
        setTasksForSelectedDate(selectedDate);
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
        Date selectedDate = java.sql.Date.valueOf(date.toString());
        setTasksForSelectedDate(selectedDate);
    }

    private void setTasksForSelectedDate(Date selectedDate) {
        List<Task> tasks = taskDAO.findAll().stream()
                .filter(task -> {
                    Date taskDueDate = task.getDueDate();
                    if (taskDueDate != null) {
                        Calendar taskCalendar = Calendar.getInstance();
                        taskCalendar.setTime(taskDueDate);
                        taskCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        taskCalendar.set(Calendar.MINUTE, 0);
                        taskCalendar.set(Calendar.SECOND, 0);
                        taskCalendar.set(Calendar.MILLISECOND, 0);

                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.setTime(selectedDate);
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        selectedCalendar.set(Calendar.MINUTE, 0);
                        selectedCalendar.set(Calendar.SECOND, 0);
                        selectedCalendar.set(Calendar.MILLISECOND, 0);

                        return taskCalendar.getTime().equals(selectedCalendar.getTime());
                    }
                    return false;
                })
                .collect(Collectors.toList());

        TaskArrayAdapter arrayAdapter = new TaskArrayAdapter(getActivity(), R.layout.item_task, tasks);
        lvTask.setAdapter(arrayAdapter);
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
                    CalendarUtils.selectedDate = LocalDate.of(year1, month1 + 1, dayOfMonth);
                    setWeekView();
                    setTasksForSelectedDate(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }
}
