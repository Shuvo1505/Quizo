package com.purnendu.quizo.activities.user.system;

import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.purnendu.quizo.R;
import com.purnendu.quizo.adapters.LeaderBoardAdapter;
import com.purnendu.quizo.models.LeaderBoard;
import com.purnendu.quizo.utilities.Constants;
import com.purnendu.quizo.utilities.NetworkListener;
import com.purnendu.quizo.utilities.QuizoVibrator;
import com.purnendu.quizo.utilities.SharedPref;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity displays the leaderboard for the Quizo application, showing users' total scores.
 * It fetches user data from Firebase Firestore, highlights the current user's score,
 * and lists other players' scores in descending order.
 * <p>
 * It utilizes {@link androidx.recyclerview.widget.RecyclerView} with {@link com.purnendu.quizo.adapters.LeaderBoardAdapter}
 * to present the leaderboard data, and a {@link android.widget.ProgressBar} to indicate loading status.
 * Data is retrieved from a Firebase Firestore instance.
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * <p>
 * The activity also incorporates network connectivity monitoring using a {@link com.purnendu.quizo.utilities.NetworkListener}
 * to handle online/offline states gracefully, ensuring a smooth user experience.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for LeaderBoard
public class LeaderBoardActivity extends AppCompatActivity {

    // UI elements
    private TextView tvCurrentUserScore;
    private RecyclerView rvLeaderboard;
    private ProgressBar progressBarLeaderboard;

    // Firebase Firestore instance
    private FirebaseFirestore db;
    // Adapter for the RecyclerView
    private LeaderBoardAdapter LeaderBoardAdapter;
    // List to hold leaderboard entries
    private List<LeaderBoard> allLeaderboardEntries;

    // Declare network listener
    private NetworkListener networkListener;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED); // Lock screen orientation
        setContentView(R.layout.activity_leaderboard); // Set the layout

        // Set navigation bar color
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

        // Initialize UI elements
        tvCurrentUserScore = findViewById(R.id.tvCurrentUserScore);
        rvLeaderboard = findViewById(R.id.rvLeaderboard);
        progressBarLeaderboard = findViewById(R.id.progressBarLeaderboard);

        // Configure RecyclerView
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        allLeaderboardEntries = new ArrayList<>();
        LeaderBoardAdapter = new LeaderBoardAdapter(new ArrayList<>()); // Initialize with empty list
        rvLeaderboard.setAdapter(LeaderBoardAdapter);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Handle back button click
        findViewById(R.id.imageViewLeaderboardBack).setOnClickListener(view -> {
            QuizoVibrator.vibratePhone(this); // Vibrate
            finish(); // Finish activity
        });

        networkListener = new NetworkListener(this, new NetworkListener.NetworkChangeListener() {
            @Override
            public void onNetworkConnected() {
                // When network is connected, attempt to fetch leaderboard data
                fetchLeaderboardData(); // Re-fetch data
            }

            @Override
            public void onNetworkDisconnected() {
                // When network is disconnected, hide content and show a message (dialog is handled by NetworkListener)
                rvLeaderboard.setVisibility(View.GONE);
                findViewById(R.id.tvOtherPlayersLabel).setVisibility(View.GONE);
            }
        });

        // Fetch and display leaderboard data
        fetchLeaderboardData();
    }

    /**
     * Registers the {@link NetworkListener} when the activity becomes visible.
     * This is crucial for receiving network broadcast updates.
     */
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkListener, intentFilter);
    }

    /**
     * Unregisters the {@link NetworkListener} when the activity is no longer visible.
     * This prevents memory leaks and ensures dialogs are dismissed properly.
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkListener);
        // Dismiss the network disconnected dialog if it's showing to prevent WindowManager$BadTokenException
        if (networkListener != null) {
            networkListener.dismissNetworkDisconnectedDialog();
        }
    }

    /**
     * Fetches all user data (name and totalPoints) from Firebase Firestore's "users" collection.
     * It then separates the current user's score, sorts the rest, and updates the UI.
     */
    private void fetchLeaderboardData() {
        findViewById(R.id.tvOtherPlayersLabel).setVisibility(View.GONE);
        findViewById(R.id.cardViewCurrentUser).setVisibility(View.GONE);
        progressBarLeaderboard.setVisibility(View.VISIBLE); // Show progress bar
        rvLeaderboard.setVisibility(View.GONE); // Hide RecyclerView initially
        // Get the current user's email from Shared Preferences
        String currentUserEmail = SharedPref.getInstance().getUser(this).getEmail();

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    findViewById(R.id.tvOtherPlayersLabel).setVisibility(View.VISIBLE);
                    findViewById(R.id.cardViewCurrentUser).setVisibility(View.VISIBLE);
                    progressBarLeaderboard.setVisibility(View.GONE); // Hide progress bar
                    if (task.isSuccessful()) {
                        allLeaderboardEntries.clear(); // Clear previous data
                        LeaderBoard currentUserEntry = null;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convert each document to a LeaderBoard object
                            LeaderBoard entry = document.toObject(LeaderBoard.class);

                            // Firestore document ID is the email, ensure it's set in the entry
                            if (entry.getEmail() == null || entry.getEmail().isEmpty()) {
                                entry.setEmail(document.getId());
                            }

                            // Identify current user's entry
                            if (entry.getEmail() != null && entry.getEmail().equals(currentUserEmail)) {
                                currentUserEntry = entry;
                            } else {
                                allLeaderboardEntries.add(entry); // Add other players to list
                            }
                        }

                        // Display current user's score prominently
                        if (currentUserEntry != null) {
                            tvCurrentUserScore.setText(Constants.formatScore(currentUserEntry.getTotalPoints()));
                        } else {
                            tvCurrentUserScore.setText(String.valueOf(0L));
                        }

                        // Sort other players by totalPoints in descending order
                        allLeaderboardEntries.sort((e1, e2) -> Long.compare(e2.getTotalPoints(), e1.getTotalPoints()));

                        //Check for availability of players
                        if (allLeaderboardEntries.isEmpty()) {
                            findViewById(R.id.textHolder).setVisibility(View.VISIBLE);
                            findViewById(R.id.tvOtherPlayersLabel).setVisibility(View.GONE);
                            findViewById(R.id.cardViewCurrentUser).setVisibility(View.GONE);
                        } else {
                            // Update RecyclerView adapter
                            LeaderBoardAdapter.updateData(allLeaderboardEntries);
                            rvLeaderboard.setVisibility(View.VISIBLE); // Show RecyclerView
                        }
                    } else {
                        Toast.makeText(LeaderBoardActivity.this,
                                getString(R.string.error_fetching_leaderboard), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
