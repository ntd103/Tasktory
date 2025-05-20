package com.example.Tasktory.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Tasktory.R;
import com.example.Tasktory.model.Tag;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {
    private final List<Tag> tags;
    private final OnTagClickListener onTagClickListener;

    // Interface để xử lý sự kiện click
    public interface OnTagClickListener {
        void onTagClick(Tag tag);
    }

    // Constructor
    public TagAdapter(List<Tag> tags, OnTagClickListener onTagClickListener) {
        this.tags = tags;
        this.onTagClickListener = onTagClickListener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag_1, parent, false); // Sử dụng layout `item_tag.xml`
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tags.get(position);
        holder.bind(tag);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    // ViewHolder cho từng item trong RecyclerView
    class TagViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTagName;
        private final View colorIndicator;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTagName = itemView.findViewById(R.id.tvTagName);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }

        public void bind(Tag tag) {
            tvTagName.setText(tag.getName());
            colorIndicator.setBackgroundColor(Color.parseColor(tag.getColor())); // Set màu cho tag
            itemView.setOnClickListener(v -> onTagClickListener.onTagClick(tag)); // Xử lý sự kiện click
        }
    }
}
