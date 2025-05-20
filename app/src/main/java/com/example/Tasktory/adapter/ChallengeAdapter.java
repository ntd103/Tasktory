package com.example.Tasktory.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.Tasktory.R;
import com.example.Tasktory.dao.ChallengeDAO;
import com.example.Tasktory.model.Challenge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChallengeAdapter extends ArrayAdapter<Challenge> {
    private Context context;
    private List<Challenge> challenges;
    private ChallengeDAO challengeDAO;
    private SimpleDateFormat dateFormat;

    public ChallengeAdapter(@NonNull Context context, List<Challenge> challenges) {
        super(context, R.layout.item_challenge, challenges);
        this.context = context;
        this.challenges = challenges;
        this.challengeDAO = new ChallengeDAO(context);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_challenge, parent, false);
            holder = new ViewHolder();
            holder.nameText = convertView.findViewById(R.id.challengeName);
            holder.descriptionText = convertView.findViewById(R.id.challengeDescription);
            holder.progressText = convertView.findViewById(R.id.challengeProgress);
            holder.deleteButton = convertView.findViewById(R.id.deleteButton);
            holder.checkBox = convertView.findViewById(R.id.checkBoxComplete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Challenge challenge = challenges.get(position);
        holder.nameText.setText(challenge.getName());
        holder.descriptionText.setText(challenge.getDescription());
        
        try {
            Date startDate = dateFormat.parse(challenge.getStartDate());
            Date endDate = dateFormat.parse(challenge.getEndDate());
            Date currentDate = new Date();
            
            String status;
            if (currentDate.before(startDate)) {
                status = "Not started yet";
            } else if (currentDate.after(endDate)) {
                status = "Ended";
            } else {
                status = "In progress";
            }
            
            holder.progressText.setText(String.format("%s - %s (%s)", 
                challenge.getStartDate(), challenge.getEndDate(), status));
        } catch (ParseException e) {
            holder.progressText.setText(String.format("%s - %s", 
                challenge.getStartDate(), challenge.getEndDate()));
        }

        holder.checkBox.setChecked(challenge.isCompleted());
        holder.checkBox.setOnClickListener(v -> {
            challenge.setCompleted(holder.checkBox.isChecked());
            challengeDAO.update(challenge);
            notifyDataSetChanged();
        });

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                .setTitle("Delete Challenge")
                .setMessage("Are you sure you want to delete \"" + challenge.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    ChallengeDAO challengeDAO = new ChallengeDAO(context);
                    challengeDAO.delete(challenge.getId());
                    remove(challenge);
                    notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView nameText;
        TextView descriptionText;
        TextView progressText;
        ImageButton deleteButton;
        CheckBox checkBox;
    }
}
