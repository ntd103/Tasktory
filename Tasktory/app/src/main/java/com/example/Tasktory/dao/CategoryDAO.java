package com.example.Tasktory.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.Tasktory.database.DbHelper;
import com.example.Tasktory.model.Category;
import com.example.Tasktory.objects.UserSession;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final SQLiteDatabase db;

    public int getUserId() {
        UserSession userSession = UserSession.getInstance();
        return userSession.getUserId();
    }

    public CategoryDAO(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();

    }

    // Thêm danh mục mới
    public boolean insertCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("icon", category.getIcon());
        values.put("color", category.getColor());
        values.put("user_id", getUserId()); // Thay "userId" thành "user_id"

        long result = db.insert("categories", null, values); // Thay "Category" thành "categories"
        return result != -1;
    }

    // Cập nhật danh mục
    public int updateCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("icon", category.getIcon());
        values.put("color", category.getColor());

        return db.update("categories", values, "category_id = ? AND user_id = ?", // Thay "Category" thành "categories"
                new String[]{String.valueOf(category.getCategoryId()), String.valueOf(getUserId())}); // Thay "categoryId" và "userId" thành "category_id" và "user_id"
    }

    // Xóa danh mục
    public int deleteCategory(int categoryId) {
        return db.delete("categories", "category_id = ? AND user_id = ?", // Thay "Category" thành "categories"
                new String[]{String.valueOf(categoryId), String.valueOf(getUserId())}); // Thay "categoryId" và "userId" thành "category_id" và "user_id"
    }

    // Lấy tất cả danh mục của người dùng
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String selectQuery = "SELECT * FROM categories WHERE user_id = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(getUserId())});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))); // Thay "categoryId" thành "category_id"
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                category.setIcon(cursor.getString(cursor.getColumnIndexOrThrow("icon")));
                category.setColor(cursor.getString(cursor.getColumnIndexOrThrow("color")));
                category.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))); // Thay "userId" thành "user_id"
                categories.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return categories;
    }

    public Category getCategoryById(int id) {
        Category category = null;
        Cursor cursor = db.query("categories", null, "category_id = ? AND user_id = ?",
                new String[]{String.valueOf(id), String.valueOf(getUserId())}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            category = new Category();
            category.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
            category.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            category.setIcon(cursor.getString(cursor.getColumnIndexOrThrow("icon")));
            category.setColor(cursor.getString(cursor.getColumnIndexOrThrow("color")));
            category.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
            cursor.close();
        }

        return category;
    }

    public Category getCategoryByTaskId(int taskId) {
        Category category = null;

        Cursor cursor = db.rawQuery(
                "SELECT c.category_id, c.name, c.icon, c.color, c.user_id " +
                        "FROM categories c " +
                        "JOIN tasks t ON c.category_id = t.category_id " +
                        "WHERE t.task_id = ? AND c.user_id = ?",
                new String[]{String.valueOf(taskId), String.valueOf(getUserId())}
        );

        if (cursor != null && cursor.moveToFirst()) {
            category = new Category();
            category.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
            category.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            category.setIcon(cursor.getString(cursor.getColumnIndexOrThrow("icon")));
            category.setColor(cursor.getString(cursor.getColumnIndexOrThrow("color")));
            category.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_id")));
            cursor.close();
        }

        return category;
    }
}
