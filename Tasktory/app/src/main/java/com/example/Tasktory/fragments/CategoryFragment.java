package com.example.Tasktory.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.Tasktory.R;
import com.example.Tasktory.activity.MainActivity;
import com.example.Tasktory.adapter.CategoryAdapter;
import com.example.Tasktory.dao.CategoryDAO;
import com.example.Tasktory.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CategoryFragment extends Fragment {
    private ListView listView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private CategoryDAO categoryDAO;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category, container, false);

        listView = view.findViewById(R.id.categoryListView);
        categoryDAO = new CategoryDAO(requireContext());
        categoryList = categoryDAO.getAllCategories();

        if (categoryList.isEmpty()) {
            addSampleCategories();
        }

        adapter = new CategoryAdapter(requireContext(), categoryList, categoryDAO);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Category category = categoryList.get(position);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("category_id", category.getCategoryId());
                intent.putExtra("category_name", category.getName());
                startActivity(intent);
            }
        });

        return view;
    }

    public void showAddCategoryDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_category);

        ImageView closeButton = dialog.findViewById(R.id.cancelButton);
        TextView createText = dialog.findViewById(R.id.createText);
        EditText editCategoryName = dialog.findViewById(R.id.editCategoryName);

        LinearLayout colorSelectionLayout = dialog.findViewById(R.id.colorSelectionLayout);
        ImageView color1 = dialog.findViewById(R.id.color1);
        ImageView color2 = dialog.findViewById(R.id.color2);
        ImageView color3 = dialog.findViewById(R.id.color3);
        ImageView color4 = dialog.findViewById(R.id.color4);
        ImageView color5 = dialog.findViewById(R.id.color5);

        LinearLayout iconSelectionLayout = dialog.findViewById(R.id.iconSelectionLayout);
        ImageView icon1 = dialog.findViewById(R.id.icon1);
        ImageView icon2 = dialog.findViewById(R.id.icon2);
        ImageView icon3 = dialog.findViewById(R.id.icon3);
        ImageView icon4 = dialog.findViewById(R.id.icon4);
        ImageView icon5 = dialog.findViewById(R.id.icon5);

        final String[] selectedColor = {"#8687E7"};
        final String[] selectedIcon = {"ic_question"};

        color1.setOnClickListener(v -> selectedColor[0] = "#FF0000"); // Màu đỏ
        color2.setOnClickListener(v -> selectedColor[0] = "#33B5E5"); // Màu xanh
        color3.setOnClickListener(v -> selectedColor[0] = "#99CC00"); // Màu xanh lá
        color4.setOnClickListener(v -> selectedColor[0] = "#FFBB33"); // Màu cam
        color5.setOnClickListener(v -> selectedColor[0] = "#AA66CC"); // Màu tím

        icon1.setOnClickListener(v -> selectedIcon[0] = "baseline_home_24");
        icon2.setOnClickListener(v -> selectedIcon[0] = "ic_work");
        icon3.setOnClickListener(v -> selectedIcon[0] = "ic_personal");
        icon4.setOnClickListener(v -> selectedIcon[0] = "ic_cart");
        icon5.setOnClickListener(v -> selectedIcon[0] = "ic_question");

        FloatingActionButton fabSaveCategory = dialog.findViewById(R.id.fabSave);
        fabSaveCategory.setOnClickListener(view -> {
            String categoryName = editCategoryName.getText().toString();
            addCategory(categoryName, selectedIcon[0], selectedColor[0], dialog);
        });

        closeButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void addCategory(String categoryName, String selectedIcon, String selectedColor, Dialog dialog) {
        if (categoryName.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            return;
        }

        Category category = new Category(0, categoryName, selectedIcon, selectedColor, 0);

        boolean isInserted = categoryDAO.insertCategory(category);

        if (isInserted) {
            Toast.makeText(requireContext(), "Danh mục đã được thêm", Toast.LENGTH_SHORT).show();
            categoryList.clear();
            categoryList.addAll(categoryDAO.getAllCategories());
            adapter.notifyDataSetChanged();  // Thông báo adapter cập nhật danh sách
        } else {
            Toast.makeText(requireContext(), "Lỗi khi thêm danh mục", Toast.LENGTH_SHORT).show();
        }

        dialog.dismiss();
    }

    private void addSampleCategories() {
        categoryDAO.insertCategory(new Category(0, "Công việc", "ic_work", "#8687E7", 1));
        categoryDAO.insertCategory(new Category(0, "Cá nhân", "ic_personal", "#8687E7", 2));
        categoryList.clear();
        categoryList.addAll(categoryDAO.getAllCategories());
    }
}
