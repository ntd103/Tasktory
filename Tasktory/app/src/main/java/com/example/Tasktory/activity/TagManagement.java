package com.example.Tasktory.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Tasktory.R;
import com.example.Tasktory.adapter.TagAdapter;
import com.example.Tasktory.dao.TagDAO;
import com.example.Tasktory.model.Tag;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class TagManagement extends AppCompatActivity {
    private TagDAO tagDAO;
    private RecyclerView recyclerViewTags;
    private FloatingActionButton fabAddTag;
    private TagAdapter adapter;
    private List<Tag> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_management);

        tagDAO = new TagDAO(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setColorFilter(ContextCompat.getColor(this, R.color.black));
        btnBack.setOnClickListener(v -> finish());

        recyclerViewTags = findViewById(R.id.recyclerViewTags);
        fabAddTag = findViewById(R.id.fabAddTag);

        // Cấu hình RecyclerView
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(this));

        loadTags();

        fabAddTag.setOnClickListener(v -> showAddTagDialog());
    }

    private void loadTags() {
        tags = tagDAO.getAllTags();
        adapter = new TagAdapter(tags, this::showEditDeleteDialog);
        recyclerViewTags.setAdapter(adapter);
    }

    private void showAddTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm Tag");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 16);

        final EditText inputName = new EditText(this);
        inputName.setHint("Tên tag");
        layout.addView(inputName);

        final Button colorPickerButton = new Button(this);
        colorPickerButton.setText("Chọn màu");
        layout.addView(colorPickerButton);

        final int[] selectedColor = {0xFFFFFFFF}; // Màu mặc định là trắng
        colorPickerButton.setOnClickListener(v -> {
            AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, selectedColor[0], new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    selectedColor[0] = color;
                    colorPickerButton.setBackgroundColor(color);
                }

                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                    // Người dùng hủy chọn màu
                }
            });
            colorPicker.show();
        });

        builder.setView(layout);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String tagName = inputName.getText().toString();
            String tagColor = String.format("#%06X", (0xFFFFFF & selectedColor[0]));

            if (!tagName.isEmpty()) {
                tagDAO.addTag(tagName, tagColor);
                loadTags();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showEditDeleteDialog(Tag tag) {
        CharSequence[] options = {"Sửa", "Xóa"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tùy chọn");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Sửa
                showEditTagDialog(tag);
            } else if (which == 1) { // Xóa
                tagDAO.deleteTag(tag.getTagId());
                loadTags();
            }
        });
        builder.show();
    }

    private void showEditTagDialog(Tag tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Tag");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText editName = new EditText(this);
        editName.setText(tag.getName());
        layout.addView(editName);

        final Button colorPickerButton = new Button(this);
        colorPickerButton.setText("Chọn màu");
        colorPickerButton.setBackgroundColor(android.graphics.Color.parseColor(tag.getColor()));
        layout.addView(colorPickerButton);

        final int[] selectedColor = {android.graphics.Color.parseColor(tag.getColor())};
        colorPickerButton.setOnClickListener(v -> {
            AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, selectedColor[0], new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    selectedColor[0] = color;
                    colorPickerButton.setBackgroundColor(color);
                }

                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                    // Người dùng hủy chọn màu
                }
            });
            colorPicker.show();
        });

        builder.setView(layout);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newName = editName.getText().toString();
            String newColor = String.format("#%06X", (0xFFFFFF & selectedColor[0]));

            if (!newName.isEmpty()) {
                tagDAO.updateTag(tag.getTagId(), newName, newColor);
                loadTags();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
