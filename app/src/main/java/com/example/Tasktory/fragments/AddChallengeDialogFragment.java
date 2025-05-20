package com.example.Tasktory.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.Tasktory.R;
import com.example.Tasktory.dao.ChallengeDAO;
import com.example.Tasktory.model.Challenge;
import com.example.Tasktory.objects.UserSession;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddChallengeDialogFragment extends DialogFragment {
    private EditText editTextName;
    private EditText editTextDescription;
    private TextView textViewStartDate;
    private TextView textViewEndDate;
    private ChallengeDAO challengeDAO;
    private OnChallengeAddedListener listener;
    private SimpleDateFormat dateFormat;
    private Calendar startCalendar, endCalendar;

    public interface OnChallengeAddedListener {
        void onChallengeAdded();
    }

    public void setOnChallengeAddedListener(OnChallengeAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_challenge, null);

        editTextName = view.findViewById(R.id.editTextChallengeName);
        editTextDescription = view.findViewById(R.id.editTextChallengeDescription);
        textViewStartDate = view.findViewById(R.id.textViewStartDate);
        textViewEndDate = view.findViewById(R.id.textViewEndDate);
        Button buttonAdd = view.findViewById(R.id.buttonAddChallenge);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        endCalendar.add(Calendar.DAY_OF_MONTH, 7); // Default end date is 7 days from now

        challengeDAO = new ChallengeDAO(getContext());

        // Set initial dates
        updateStartDateText();
        updateEndDateText();

        // Set up date pickers
        textViewStartDate.setOnClickListener(v -> showDatePicker(true));
        textViewEndDate.setOnClickListener(v -> showDatePicker(false));

        buttonAdd.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (name.isEmpty()) {
                editTextName.setError("Please enter a name");
                return;
            }

            Challenge challenge = new Challenge();
            challenge.setName(name);
            challenge.setDescription(description);
            challenge.setStartDate(dateFormat.format(startCalendar.getTime()));
            challenge.setEndDate(dateFormat.format(endCalendar.getTime()));
            challenge.setUserId(UserSession.getInstance().getUserId());
            challenge.setCompleted(false);

            long result = challengeDAO.insert(challenge);
            if (result > 0) {
                if (listener != null) {
                    listener.onChallengeAdded();
                }
                dismiss();
            }
        });

        builder.setView(view)
                .setTitle("Add New Challenge");

        return builder.create();
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    if (isStartDate) {
                        startCalendar.set(year, month, dayOfMonth);
                        updateStartDateText();
                        // Ensure end date is not before start date
                        if (endCalendar.before(startCalendar)) {
                            endCalendar.setTime(startCalendar.getTime());
                            endCalendar.add(Calendar.DAY_OF_MONTH, 1);
                            updateEndDateText();
                        }
                    } else {
                        endCalendar.set(year, month, dayOfMonth);
                        updateEndDateText();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        if (isStartDate) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        } else {
            datePickerDialog.getDatePicker().setMinDate(startCalendar.getTimeInMillis());
        }

        datePickerDialog.show();
    }

    private void updateStartDateText() {
        textViewStartDate.setText("Start Date: " + dateFormat.format(startCalendar.getTime()));
    }

    private void updateEndDateText() {
        textViewEndDate.setText("End Date: " + dateFormat.format(endCalendar.getTime()));
    }
}
