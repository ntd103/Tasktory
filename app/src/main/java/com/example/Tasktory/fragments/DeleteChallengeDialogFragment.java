package com.example.Tasktory.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.Tasktory.R;
import com.example.Tasktory.model.Challenge;

public class DeleteChallengeDialogFragment extends DialogFragment {
    private Challenge challenge;
    private OnChallengeDeletedListener listener;

    public interface OnChallengeDeletedListener {
        void onChallengeDeleted(Challenge challenge);
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public void setOnChallengeDeletedListener(OnChallengeDeletedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete_challenge, null);

        TextView messageText = view.findViewById(R.id.messageText);
        messageText.setText("Are you sure you want to delete challenge \"" + challenge.getName() + "\"?");

        builder.setView(view)
                .setTitle("Delete Challenge")
                .setPositiveButton("Delete", (dialog, id) -> {
                    if (listener != null) {
                        listener.onChallengeDeleted(challenge);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        return builder.create();
    }
}
