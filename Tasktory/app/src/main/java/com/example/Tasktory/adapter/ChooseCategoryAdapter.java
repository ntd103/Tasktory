package com.example.Tasktory.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Tasktory.R;
import com.example.Tasktory.model.Category;

import java.util.List;

public class ChooseCategoryAdapter extends RecyclerView.Adapter<ChooseCategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener onCategoryClickListener;
    private Category selectedCategory;

    public ChooseCategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.onCategoryClickListener = listener;
    }

    @NonNull
    @Override
    public ChooseCategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_2, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        //Làm nổi danh mục được chọn
        if (category.equals(selectedCategory)) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        if (category.getIcon() != null && !category.getIcon().isEmpty()) {
            int iconResId = holder.itemView.getContext().getResources().getIdentifier(
                    category.getIcon(), "drawable", holder.itemView.getContext().getPackageName()
            );

            if (iconResId != 0) {
                holder.imageViewIcon.setImageResource(iconResId);
            } else {
                holder.imageViewIcon.setImageResource(R.drawable.ic_block);
            }
        } else {
            holder.imageViewIcon.setImageResource(R.drawable.ic_block);
        }

        if (category.getColor() != null && !category.getColor().isEmpty()) {
            try {
                int color = Color.parseColor(category.getColor());
                holder.imageViewIcon.getBackground().mutate().setTint(color);
            } catch (Exception e) {
            }
        }
        holder.textViewName.setText(category.getName());

        holder.itemView.setOnClickListener(v -> onCategoryClickListener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setSelectedCategory(Category category) {
        this.selectedCategory = category;
        notifyDataSetChanged();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewIcon = itemView.findViewById(R.id.iconCategory);
            textViewName = itemView.findViewById(R.id.textCategory);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

}