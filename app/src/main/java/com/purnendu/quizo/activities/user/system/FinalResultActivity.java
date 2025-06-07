package com.purnendu.quizo.activities.user.system;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.purnendu.quizo.R;
import com.purnendu.quizo.databases.UserDatabase;
import com.purnendu.quizo.dbclients.UserDatabaseClient;
import com.purnendu.quizo.models.Attempt;
import com.purnendu.quizo.utilities.Constants;
import com.purnendu.quizo.utilities.DateParser;
import com.purnendu.quizo.utilities.NetworkListener;
import com.purnendu.quizo.utilities.QuizoVibrator;
import com.purnendu.quizo.utilities.SharedPref;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This activity displays the final results of a quiz attempt to the user in the Quizo application.
 * It receives quiz performance data (correct/incorrect answers, subject, points earned) via an {@link android.content.Intent}.
 * The activity calculates and displays the overall score, and saves the quiz attempt details
 * as an {@link com.purnendu.quizo.models.Attempt} into the {@link com.purnendu.quizo.databases.UserDatabase}
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * via {@link com.purnendu.quizo.dbclients.UserDatabaseClient}.
 * <p>
 * It utilizes Android UI components such as {@link android.widget.TextView} to present the results.
 * Utility classes like {@link com.purnendu.quizo.utilities.Constants} for result keys,
 * {@link com.purnendu.quizo.utilities.DateParser} for formatting timestamps,
 * {@link com.purnendu.quizo.utilities.QuizoVibrator} for haptic feedback,
 * and {@link com.purnendu.quizo.utilities.SharedPref} for user session details are integrated.
 * Asynchronous database operations are handled using {@link java.util.concurrent.Executor} and {@link android.os.Handler}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for Final Result
public class FinalResultActivity extends AppCompatActivity {

    // Firebase Firestore instance
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // UI elements to display quiz results
    private TextView tvSubject, tvCorrect, tvIncorrect, tvEarned, tvDate;
    private Button buttonFinish;

    // Declare network listener
    private NetworkListener networkListener;

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
        setContentView(R.layout.activity_final_result); // Set the layout for this activity

        // Set the navigation bar color to black
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,
                R.color.black));

        // Initialize UI elements
        tvSubject = findViewById(R.id.textView16);
        tvCorrect = findViewById(R.id.textView19);
        tvIncorrect = findViewById(R.id.textView27);
        tvEarned = findViewById(R.id.textView28);
        tvDate = findViewById(R.id.textView30);
        buttonFinish = findViewById(R.id.btnFinishQuiz);

        // Get quiz results from the intent that started this activity
        Intent intent = getIntent();
        int correctAnswer = intent.getIntExtra(Constants.CORRECT, 0);
        // Get the total questions attempted from the quiz activity
        int totalQuestionsAttempted = intent.getIntExtra
                (Constants.TOTAL_QUESTIONS_ATTEMPTED, 0);
        String subject = intent.getStringExtra(Constants.SUBJECT);
        // Get the current user's email from SharedPreferences
        String email = SharedPref.getInstance().getUser(this).getEmail();

        // Calculate incorrect answers based on the actual number of questions attempted
        int incorrectAnswer = totalQuestionsAttempted - correctAnswer;

        // Calculate earned points: (correct answers * points per correct) - (incorrect answers * points per incorrect)
        int earnedPoints = (correctAnswer * Constants.CORRECT_POINT) -
                (incorrectAnswer * Constants.INCORRECT_POINT);

        // Set click listener for the back arrow ImageView
        findViewById(R.id.imageViewFinalResultQuiz).setOnClickListener(view -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            // Navigate back to UserActivity and clear the activity stack
            startActivity(new Intent(FinalResultActivity.this,
                    UserActivity.class));
            finish(); // Finish the current activity
        });

        // Set click listener for the "Start Again" button
        findViewById(R.id.btnFinishQuiz).setOnClickListener(view -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            // Navigate back to UserActivity and clear the activity stack
            startActivity(new Intent(FinalResultActivity.this,
                    UserActivity.class));
            finish(); // Finish the current activity
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to UserActivity and clear the activity stack
                startActivity(new Intent(FinalResultActivity.this,
                        UserActivity.class));
                finish(); // Finish the current activity
            }
        });

        // Create an Attempt object with details of the current quiz
        Attempt attempt = new Attempt(
                Calendar.getInstance().getTimeInMillis(), // Current timestamp as creation time
                subject, // Quiz subject
                correctAnswer, // Number of correct answers
                incorrectAnswer, // Number of incorrect answers
                earnedPoints, // Points earned from this attempt
                email // User's email
        );

        // This method will fetch overall points from DB, add current attempt's earned points,
        // set the new total to the attempt object, save the attempt, and then display data.
        getOverallPointsAndSaveAttempt(attempt);
    }

    /**
     * Fetches the user's previous overall points from the database, calculates the new total,
     * updates the {@link com.purnendu.quizo.models.Attempt} object with this new total,
     * saves the attempt to the database, and then calls {@link #displayData(Attempt)}
     * to update the UI. This entire process runs on a background thread.
     *
     * @param attempt The current {@link com.purnendu.quizo.models.Attempt} object to be saved.
     */
    private void getOverallPointsAndSaveAttempt(Attempt attempt) {
        // Create an Executor for background thread operations
        Executor executor = Executors.newSingleThreadExecutor();
        // Create a Handler to post results back to the main (UI) thread
        Handler handler = new Handler(Looper.getMainLooper());

        // Execute the database operations on a background thread
        executor.execute(() -> {
            // Get the UserDatabaseClient instance
            UserDatabase userDatabaseClient = UserDatabaseClient.
                    getInstance(getApplicationContext());
            // Fetch the *previous* overall points for this user from all past attempts
            long previousOverallPoints = userDatabaseClient.userDao().
                    getOverAllPoints(attempt.getEmail());

            // Calculate the new total overall points by adding current attempt's earned points
            long newOverallPoints = previousOverallPoints + attempt.getEarned();

            // Set this new total to the current attempt object
            attempt.setOverallPoints(newOverallPoints);

            // Insert the current attempt (which now contains the calculated new overall points)
            userDatabaseClient.userDao().insertAttempt(attempt);

            // Post the UI update back to the main thread with the updated attempt object
            handler.post(() -> {
                displayData(attempt); //Display quiz result

                networkListener = new NetworkListener(this, new NetworkListener.NetworkChangeListener() {
                    @Override
                    public void onNetworkConnected() {
                        // Re-Call the method to save user data to Firebase Firestore
                        pushDataToCloud(attempt.getEmail(),
                                SharedPref.getInstance().getUser(FinalResultActivity.this).getUsername(),
                                attempt.getOverallPoints()
                        );
                    }

                    @Override
                    public void onNetworkDisconnected() {
                    }
                });
                // Call the method to save user data to Firebase Firestore
                pushDataToCloud(attempt.getEmail(),
                        SharedPref.getInstance().getUser(FinalResultActivity.this).getUsername(),
                        attempt.getOverallPoints()
                );
            });
        });
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
     * Displays the quiz result data on the UI elements.
     *
     * @param attempt The {@link com.purnendu.quizo.models.Attempt} object containing the results to display.
     */
    private void displayData(Attempt attempt) {
        tvSubject.setText(attempt.getSubject()); // Set quiz subject
        tvCorrect.setText(String.valueOf(attempt.getCorrect())); // Set number of correct answers
        tvIncorrect.setText(String.valueOf(attempt.getIncorrect())); // Set number of incorrect answers
        tvEarned.setText(String.valueOf(attempt.getEarned())); // Set earned points
        // Format and set the date of the attempt
        tvDate.setText(DateParser.formatDate(attempt.getCreatedTime()));
    }

    /**
     * Saves or updates the user's name and total points in Firebase.
     * The user's email is used as the document ID for their data in the 'users' collection.
     * It uses SetOptions.merge() to update fields without overwriting the entire document.
     *
     * @param email       The email of the user, used as the document ID.
     * @param userName    The name of the user.
     * @param totalPoints The total accumulated points for the user.
     */
    private void pushDataToCloud(String email, String userName, long totalPoints) {
        // Initialize progress bar and blocking inputs
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.imageViewFinalResultQuiz).setEnabled(false);
        buttonFinish.setEnabled(false);
        buttonFinish.setText(R.string.please_wait);

        // Create a new map to store the user's data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", userName);
        userData.put("totalPoints", totalPoints);
        userData.put("lastUpdated", Calendar.getInstance().getTimeInMillis()); // Add a timestamp

        // Get a reference to the 'users' collection and create/get a document with the user's email as ID
        db.collection("users").document(email)
                .set(userData, SetOptions.merge()) // Use merge to update existing fields or create if not exists
                .addOnSuccessListener(aVoid -> {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    findViewById(R.id.imageViewFinalResultQuiz).setEnabled(true);
                    buttonFinish.setEnabled(true);
                    buttonFinish.setText(R.string.start_again);
                    Toast.makeText(FinalResultActivity.this,
                            "Data saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(FinalResultActivity.this,
                        "Something went wrong!", Toast.LENGTH_SHORT).show());
    }
}
