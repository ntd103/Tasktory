package com.example.Tasktory.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Tasktory.R;
import com.example.Tasktory.adapter.ChooseCategoryAdapter;
import com.example.Tasktory.dao.CategoryDAO;
import com.example.Tasktory.model.Category;

import java.util.List;

public class ChooseCategoryFragment extends DialogFragment {

    private static final String ARG_CATEGORY_ID = "category_id";
    private int categoryId;
    private CategoryDAO categoryDAO;
    private Category selectedCategory;
    private ChooseCategoryAdapter adapter;

    public ChooseCategoryFragment() {
    }

    public static ChooseCategoryFragment newInstance(int categoryId) {
        ChooseCategoryFragment fragment = new ChooseCategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int width = (int) (displayMetrics.widthPixels * 0.9);
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
        }

        Context context = requireContext();
        categoryDAO = new CategoryDAO(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_category, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerCategoryList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        List<Category> categories = categoryDAO.getAllCategories();
        adapter = new ChooseCategoryAdapter(categories, this::onCategorySelected);
        recyclerView.setAdapter(adapter);

        int index = categories.indexOf(new Category(categoryId));
        if (index != -1) {
            selectedCategory = categories.get(index);
            adapter.setSelectedCategory(selectedCategory);
        }

        Button cancelButton = view.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(v -> dismiss());

        Button saveButton = view.findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(v -> onSaveClicked());

        return view;
    }

    private void onSaveClicked() {
        Bundle result = new Bundle();
        if (selectedCategory != null) {
            result.putInt("updatedCategoryId", selectedCategory.getCategoryId());
        } else {
            result.putInt("updatedCategoryId", -1);
        }
        getParentFragmentManager().setFragmentResult("UPDATED_CATEGORY", result);

        dismiss();
    }

    private void onCategorySelected(Category category) {
        if (selectedCategory != null && selectedCategory.equals(category)) {
            selectedCategory = null;
            adapter.setSelectedCategory(null);
        } else {
            selectedCategory = category;
            adapter.setSelectedCategory(category);
        }
    }

}