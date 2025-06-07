package com.purnendu.quizo.activities.user.system;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.purnendu.quizo.R;
import com.purnendu.quizo.adapters.HistoryAdapter;
import com.purnendu.quizo.databases.UserDatabase;
import com.purnendu.quizo.dbclients.UserDatabaseClient;
import com.purnendu.quizo.models.Attempt;
import com.purnendu.quizo.utilities.QuizoVibrator;
import com.purnendu.quizo.utilities.SharedPref;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This activity allows users to view their quiz progress and past attempts in the Quizo application.
 * It retrieves a list of {@link com.purnendu.quizo.models.Attempt} records for the current user
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * from the {@link com.purnendu.quizo.databases.UserDatabase} via {@link com.purnendu.quizo.dbclients.UserDatabaseClient}.
 * The attempts are displayed in a {@link androidx.recyclerview.widget.RecyclerView} using a
 * {@link com.purnendu.quizo.adapters.HistoryAdapter}.
 * <p>
 * The activity utilizes Android UI components such as {@link android.widget.TextView} and
 * {@link androidx.recyclerview.widget.RecyclerView} with {@link androidx.recyclerview.widget.LinearLayoutManager}.
 * It integrates {@link com.purnendu.quizo.utilities.SharedPref} for retrieving user session data
 * and {@link com.purnendu.quizo.utilities.QuizoVibrator} for haptic feedback.
 * Asynchronous database operations are handled using {@link java.util.concurrent.Executor} and {@link android.os.Handler}.
 * It also includes sorting functionality for the attempts using {@link java.util.Comparator}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for History
public class HistoryActivity extends AppCompatActivity {

    // UI elements
    private RecyclerView rvHistory;
    private TextView tvTotalPoints, tvTotalAttempts, textAttempt;
    private List<Attempt> attempts;

    /**
     * Called when the activity is first created. This is where you should do all of your
     * normal static set up: create views, bind data to lists, etc.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lock the screen orientation to prevent rotation issues
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_history); // Set the layout for this activity

        // Set the navigation bar color to black
        getWindow().setNavigationBarColor(ContextCompat.
                getColor(this, R.color.black));

        // Initialize RecyclerView and set its layout manager
        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        // Initialize TextViews for displaying history summary
        tvTotalAttempts = findViewById(R.id.tvtotal_attemptsHistory);
        textAttempt = findViewById(R.id.total_attempts); // This TextView likely displays "Total Attempts" or "Total Attempt"
        tvTotalPoints = findViewById(R.id.tvOverAllPointsHistory);

        // Set OnClickListener for the back ImageView
        findViewById(R.id.imageViewHistory).setOnClickListener(V -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            finish(); // Close the current activity
        });

        // Load user's quiz attempts from the database
        loadUserAttempts();
    }

    /**
     * Loads all quiz attempts for the current logged-in user from the database asynchronously.
     * It calculates total points and displays the attempts in a RecyclerView,
     * along with a summary of total attempts and overall points.
     */
    private void loadUserAttempts() {

        // Get the email of the current logged-in user from Shared Preferences
        String email = SharedPref.getInstance().getUser(this).getEmail();
        // Create an Executor for background thread operations
        Executor executor = Executors.newSingleThreadExecutor();
        // Create a Handler to post results back to the main (UI) thread
        Handler handler = new Handler(Looper.getMainLooper());

        // Execute the database operation on a background thread
        executor.execute(() -> {
            // Get the UserDatabase instance
            UserDatabase userDatabase = UserDatabaseClient.
                    getInstance(getApplicationContext());
            // Retrieve all attempts associated with the user's email
            List<Attempt> fetchedAttempts = userDatabase.userDao().
                    getUserAndAttemptsWithSameEmail(email);

            // Post UI updates back to the main thread
            handler.post(() -> {
                // Ensure 'attempts' is updated with fetched data; handle null case for robustness
                attempts = (fetchedAttempts != null) ? fetchedAttempts : new ArrayList<>();

                if (attempts.size() > 1) {
                    textAttempt.setText(R.string.total_attempts);
                } else {
                    textAttempt.setText(R.string.total_attempt);
                }

                if (attempts.isEmpty()) {
                    // Show "No Data" message and hide all other relevant UI
                    findViewById(R.id.textDataHolder).setVisibility(View.VISIBLE);
                    findViewById(R.id.overallpoints).setVisibility(View.INVISIBLE);
                    findViewById(R.id.total_attempts).setVisibility(View.INVISIBLE);
                    findViewById(R.id.tvOverAllPointsHistory).setVisibility(View.INVISIBLE);
                    findViewById(R.id.tvtotal_attemptsHistory).setVisibility(View.INVISIBLE);

                } else {
                    // Hide "No Data" message and hide all other relevant UI
                    findViewById(R.id.textDataHolder).setVisibility(View.GONE);
                    findViewById(R.id.overallpoints).setVisibility(View.VISIBLE);
                    findViewById(R.id.total_attempts).setVisibility(View.VISIBLE);
                    findViewById(R.id.tvOverAllPointsHistory).setVisibility(View.VISIBLE);
                    findViewById(R.id.tvtotal_attemptsHistory).setVisibility(View.VISIBLE);

                    long totalPoints = attempts.stream().mapToLong(Attempt::getEarned).sum();

                    attempts.sort(Comparator.comparingLong(Attempt::getCreatedTime).reversed());

                    handler.post(() -> {
                        tvTotalAttempts.setText(String.valueOf(attempts.size()));
                        tvTotalPoints.setText(String.valueOf(totalPoints));

                        HistoryAdapter adapter = new HistoryAdapter(attempts);
                        rvHistory.setAdapter(adapter);
                    });
                }
            });
        });
    }
}
