package com.example.Tasktory.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.Tasktory.R;
import com.example.Tasktory.adapter.ChallengeAdapter;
import com.example.Tasktory.dao.ChallengeDAO;
import com.example.Tasktory.model.Challenge;
import com.example.Tasktory.objects.UserSession;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ChallengeFragment extends Fragment implements AddChallengeDialogFragment.OnChallengeAddedListener, DeleteChallengeDialogFragment.OnChallengeDeletedListener {
    private ListView listView;
    private ChallengeDAO challengeDAO;
    private FloatingActionButton fabAdd;
    private TextView emptyView;
    private ChallengeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);

        // Initialize views
        listView = view.findViewById(R.id.listViewChallenges);
        fabAdd = view.findViewById(R.id.fabAddChallenge);
        emptyView = view.findViewById(R.id.emptyView);

        // Set up long click listener for deleting challenges
        listView.setOnItemLongClickListener((parent, view1, position, id) -> {
            Challenge challenge = adapter.getItem(position);
            if (challenge != null) {
                showDeleteChallengeDialog(challenge);
            }
            return true;
        });

        // Initialize DAO
        challengeDAO = new ChallengeDAO(getContext());

        // Set empty view for listView
        listView.setEmptyView(emptyView);

        // Load challenges
        loadChallenges();

        // Add challenge button click listener
        fabAdd.setOnClickListener(v -> {
            AddChallengeDialogFragment dialog = new AddChallengeDialogFragment();
            dialog.setOnChallengeAddedListener(this);
            dialog.show(getChildFragmentManager(), "AddChallengeDialog");
        });

        return view;
    }

    private void loadChallenges() {
        List<Challenge> challenges = challengeDAO.getAll(UserSession.getInstance().getUserId());
        adapter = new ChallengeAdapter(getContext(), challenges);
        listView.setAdapter(adapter);
    }

    @Override
    public void onChallengeAdded() {
        loadChallenges();
    }

    private void showDeleteChallengeDialog(Challenge challenge) {
        if (challenge == null) return;
        DeleteChallengeDialogFragment dialog = new DeleteChallengeDialogFragment();
        dialog.setChallenge(challenge);
        dialog.setOnChallengeDeletedListener(this);
        dialog.show(getChildFragmentManager(), "DeleteChallengeDialog");
    }

    @Override
    public void onChallengeDeleted(Challenge challenge) {
        if (challenge != null && challengeDAO != null) {
            int result = challengeDAO.delete(challenge.getId());
            if (result > 0) {
                loadChallenges();
            }
        }
    }


}

