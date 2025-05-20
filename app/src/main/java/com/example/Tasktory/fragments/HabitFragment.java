package com.example.Tasktory.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.Tasktory.R;
import com.example.Tasktory.adapter.HabitAdapter;
import com.example.Tasktory.dao.HabitDAO;
import com.example.Tasktory.model.Habit;
import com.example.Tasktory.objects.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class HabitFragment extends Fragment implements AddHabitDialogFragment.OnHabitAddedListener,
        HabitAdapter.OnHabitEditListener {
    private ListView listView;
    private HabitDAO habitDAO;
    private FloatingActionButton fabAdd;
    private TextView emptyView;
    private HabitAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habit, container, false);

        // Initialize views
        listView = view.findViewById(R.id.listViewHabits);
        fabAdd = view.findViewById(R.id.fabAddHabit);
        emptyView = view.findViewById(R.id.emptyView);

        // Initialize DAO
        habitDAO = new HabitDAO(getContext());

        // Set empty view for listView
        listView.setEmptyView(emptyView);

        // Load habits
        loadHabits();
        
        // Set click handlers for habits
        configureHabitInteractions();

        // Set up long click listener for deleting habits

        // Add habit button click listener
        fabAdd.setOnClickListener(v -> {
            AddHabitDialogFragment dialog = new AddHabitDialogFragment();
            dialog.setOnHabitAddedListener(this);
            dialog.show(getChildFragmentManager(), "AddHabitDialog");
        });

        return view;
    }

    private void loadHabits() {
        List<Habit> habits = habitDAO.getAll(UserSession.getInstance().getUserId());
        adapter = new HabitAdapter(getContext(), habits);
        listView.setAdapter(adapter);
        
        // Set up click and edit handlers after loading
        configureHabitInteractions();
    }
    
    /**
     * Thiết lập xử lý sự kiện cho các thói quen
     */
    private void configureHabitInteractions() {
        if (adapter != null) {
            // Gắn sự kiện click để xem chi tiết thói quen
            adapter.setOnHabitClickListener(habit -> {
                // Hiển thị thông báo về thói quen được nhấn vào
                if (getContext() != null) {
                    android.widget.Toast.makeText(
                        getContext(),
                        "Nhấn giữ để chỉnh sửa thói quen",
                        android.widget.Toast.LENGTH_SHORT
                    ).show();
                }
            });
            
            // Gắn sự kiện nhấn giữ để chỉnh sửa thói quen
            adapter.setOnHabitEditListener(this);
        }
    }

    @Override
    public void onHabitAdded() {
        loadHabits();
    }
    
    @Override
    public void onHabitUpdated() {
        loadHabits();
    }
    
    @Override
    public void onHabitEdit(Habit habit) {
        AddHabitDialogFragment dialog = AddHabitDialogFragment.newInstanceForEdit(habit);
        dialog.setOnHabitAddedListener(this);
        dialog.show(getChildFragmentManager(), "EditHabitDialog");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHabits();
    }


}
