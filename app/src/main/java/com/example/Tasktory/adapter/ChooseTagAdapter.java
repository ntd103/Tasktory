package com.example.Tasktory.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Tasktory.R;
import com.example.Tasktory.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class ChooseTagAdapter extends RecyclerView.Adapter<ChooseTagAdapter.TagViewHolder> {

    private final List<Tag> tagList;
    private final OnTagClickListener onTagClickListener;
    private final List<Tag> selectedTags = new ArrayList<>();

    public ChooseTagAdapter(List<Tag> tagList, OnTagClickListener listener) {
        this.tagList = tagList;
        this.onTagClickListener = listener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag_2, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tagList.get(position);

        //Làm nổi nhãn được chọn
        if (selectedTags.contains(tag)) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        if (tag.getColor() != null && !tag.getColor().isEmpty()) {
            try {
                int color = Color.parseColor(tag.getColor());
                holder.imageViewIcon.getBackground().mutate().setTint(color);
            } catch (Exception ignored) {
            }
        }
        holder.textViewName.setText(tag.getName());

        holder.itemView.setOnClickListener(v -> onTagClickListener.onTagClick(tag));
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedTags(List<Tag> tag) {
        this.selectedTags.clear();
        this.selectedTags.addAll(tag);
        notifyDataSetChanged();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewName;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewIcon = itemView.findViewById(R.id.iconTag);
            textViewName = itemView.findViewById(R.id.textTag);
        }
    }

    public interface OnTagClickListener {
        void onTagClick(Tag tag);
    }

}