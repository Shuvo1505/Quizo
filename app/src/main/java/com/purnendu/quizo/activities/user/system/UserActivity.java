package com.purnendu.quizo.activities.user.system;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.purnendu.quizo.R;
import com.purnendu.quizo.activities.info.AboutActivity;
import com.purnendu.quizo.activities.user.access.EditPasswordActivity;
import com.purnendu.quizo.activities.user.access.LoginActivity;
import com.purnendu.quizo.activities.web.WebActivity;
import com.purnendu.quizo.models.User;
import com.purnendu.quizo.utilities.QuizoVibrator;
import com.purnendu.quizo.utilities.SharedPref;

/**
 * This is the main activity for regular users in the Quizo application, serving as a central hub
 * for accessing various quiz-related features and user settings. It provides navigation options
 * to start quizzes, view past attempts, edit user passwords, view app information, and access external web resources.
 * It also handles secure logout and incorporates device security features like biometric authentication
 * to protect sensitive actions such as password changes.
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * <p>
 * The activity utilizes Android UI components such as {@link androidx.appcompat.widget.Toolbar},
 * {@link androidx.cardview.widget.CardView}, and {@link android.widget.TextView}.
 * It manages activity results using {@link androidx.activity.result.ActivityResultLauncher}
 * and {@link androidx.activity.result.contract.ActivityResultContracts}.
 * Key functionalities include navigating to {@link com.purnendu.quizo.activities.user.system.QuizOptionActivity},
 * {@link com.purnendu.quizo.activities.user.system.HistoryActivity},
 * {@link com.purnendu.quizo.activities.user.access.EditPasswordActivity},
 * {@link com.purnendu.quizo.activities.info.AboutActivity}, and {@link com.purnendu.quizo.activities.web.WebActivity}.
 * It also interacts with {@link com.purnendu.quizo.utilities.SharedPref} for session management
 * and {@link com.purnendu.quizo.utilities.QuizoVibrator} for haptic feedback.
 * Biometric authentication is handled via {@link android.app.KeyguardManager} and {@link android.provider.Settings}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for UserActivity
public class UserActivity extends AppCompatActivity {

    /**
     * Launcher for starting an activity for result, specifically used for device credential confirmation.
     * This allows the activity to receive a callback when the device's lock screen authentication completes.
     */
    private ActivityResultLauncher<Intent> confirmCredentialLauncher;

    /**
     * Singleton instance of {@link com.purnendu.quizo.utilities.SharedPref} for managing
     * application-wide shared preferences, particularly for user session data.
     */
    private SharedPref sharedPref;

    /**
     * Called when the activity is first created. This is where you should do all of your
     * normal static set up: create views, bind data to lists, etc.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @SuppressLint("SetTextI18n") // Suppresses lint warning for string concatenation in setText
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lock the screen orientation to prevent rotation issues
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_main); // Set the layout for this activity

        // Initialize and set up the Toolbar as the activity's action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hide the default title in the Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Register the ActivityResultLauncher for device credential confirmation.
        // This callback handles the result from the device's lock screen authentication.
        confirmCredentialLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Check if the user successfully authenticated
                    if (result.getResultCode() == RESULT_OK) {
                        // User successfully authenticated, show success toast
                        Toast.makeText(this, "Authentication successful!",
                                Toast.LENGTH_SHORT).show();
                        // Launch the EditPasswordActivity after successful authentication
                        startActivity(new Intent(UserActivity.this,
                                EditPasswordActivity.class));
                    } else {
                        // User cancelled or failed authentication, show failure toast
                        Toast.makeText(this,
                                "Authentication failed or cancelled!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Set the navigation bar color to black
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,
                R.color.black));

        // Add a callback to block the default back button behavior
        getOnBackPressedDispatcher().addCallback(this, new
                OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // Blocking the back button to keep the user in the main section
                    }
                });

        // Initialize UI components
        TextView tvUsername = findViewById(R.id.tvUsernameHome);
        TextView devGit = findViewById(R.id.devGitHub);
        CardView cvStartQuiz = findViewById(R.id.cvSetQuestions); // This ID might be misleading, assuming it's for "Start Quiz"
        CardView cvRule = findViewById(R.id.cvRule);
        CardView cvHistory = findViewById(R.id.cvHistory);
        CardView cvEditPassword = findViewById(R.id.cvEditPassword);
        CardView cvLogout = findViewById(R.id.cvLogout);

        // Get the SharedPref instance and retrieve current user data
        sharedPref = SharedPref.getInstance();
        User user = sharedPref.getUser(this);
        // Display a welcome message with the user's username
        tvUsername.setText("Hello, " + user.getUsername());

        // Set OnClickListener for the developer's GitHub link
        devGit.setOnClickListener(V -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            Intent webIntent = new Intent(UserActivity.this, WebActivity.class);
            webIntent.putExtra("accessURL", "https://github.com/Shuvo1505"); // URL to open
            webIntent.putExtra("webHeading", "GitHub Profile"); // Heading for the web activity
            startActivity(webIntent);
        });

        // Set OnClickListener for "Start Quiz" CardView
        cvStartQuiz.setOnClickListener(
                view -> {
                    QuizoVibrator.vibratePhone(this); // Provide haptic feedback
                    startActivity(new Intent(UserActivity.this,
                            QuizOptionActivity.class)); // Navigate to QuizOptionActivity
                }
        );

        // Set OnClickListener for "Rules" CardView
        cvRule.setOnClickListener(
                view -> {
                    QuizoVibrator.vibratePhone(this); // Provide haptic feedback
                    startActivity(new Intent(UserActivity.this,
                            RuleActivity.class)); // Navigate to RuleActivity
                }
        );

        // Set OnClickListener for "History" CardView
        cvHistory.setOnClickListener(
                view -> {
                    QuizoVibrator.vibratePhone(this); // Provide haptic feedback
                    startActivity(new Intent(UserActivity.this,
                            HistoryActivity.class)); // Navigate to HistoryActivity
                }
        );

        // Set OnClickListener for "Edit Password" CardView
        cvEditPassword.setOnClickListener(
                view -> {
                    QuizoVibrator.vibratePhone(this); // Provide haptic feedback
                    authenticateUser(); // Initiate device credential authentication
                }
        );

        // Set OnClickListener for "Logout" CardView
        cvLogout.setOnClickListener(view -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            showConfirmation(); // Show logout confirmation dialog
        });
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     * This is called only once, the first time the options menu is displayed.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu); // Inflate the menu layout
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        QuizoVibrator.vibratePhone(this); // Provide haptic feedback for menu item selection
        int id = item.getItemId();

        // Handle menu item clicks
        if (id == R.id.action_info) {
            // Navigate to AboutActivity
            Intent aboutIntent = new Intent(UserActivity.this,
                    AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        } else if (id == R.id.action_docs) {
            // Navigate to WebActivity for documentation
            Intent webIntent = new Intent(UserActivity.this,
                    WebActivity.class);
            webIntent.putExtra("accessURL", "https://quizo-app.tiiny.site"); // Placeholder for actual documentation URL
            webIntent.putExtra("webHeading", "Quizo Website"); // Heading for the web activity
            startActivity(webIntent);
            return true;
        } else if (id == R.id.action_leaderboard) {
            // Navigate to Leaderboard
            Intent leaderIntent = new Intent(UserActivity.this,
                    LeaderBoardActivity.class);
            startActivity(leaderIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item); // Let the superclass handle other menu items
        }
    }

    /**
     * Displays an {@link android.app.AlertDialog} to confirm the user's logout action.
     * If confirmed, it clears the user session from {@link com.purnendu.quizo.utilities.SharedPref}
     * and navigates back to the {@link com.purnendu.quizo.activities.user.access.LoginActivity},
     * clearing the activity stack.
     */
    private void showConfirmation() {
        new AlertDialog.Builder(UserActivity.this)
                .setTitle("Alert Message") // Dialog title
                .setMessage(R.string.logout_alert) // Dialog message from resources
                .setPositiveButton("Yes", (dialog, which) -> {
                    // On "Yes" click, clear user session
                    sharedPref.clearSharedPrefUser(UserActivity.this);
                    // Navigate to LoginActivity and clear activity stack
                    Intent intent = new Intent(UserActivity.this,
                            LoginActivity.class);
                    // Add flags to clear the activity stack and start a new task
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Finish the current activity
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // On "No" click, dismiss dialog
                .setCancelable(false) // Prevent dialog from being dismissed by touching outside
                .show();
    }

    /**
     * Initiates device credential (e.g., fingerprint, PIN, pattern) authentication.
     * This is used to secure sensitive actions like changing the user password.
     * If a secure lock screen is not set up, it prompts the user to navigate to security settings.
     */
    private void authenticateUser() {
        KeyguardManager keyguardManager = (KeyguardManager)
                getSystemService(Context.KEYGUARD_SERVICE);

        // Check if a secure lock screen is set up on the device
        if (keyguardManager.isKeyguardSecure()) {
            // Create an intent to confirm device credentials
            Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(
                    "Verify it's you", // Title for the authentication screen
                    "You requested to update your password" // Description for the authentication screen
            );
            if (intent != null) {
                // Launch the device credential confirmation screen
                confirmCredentialLauncher.launch(intent);
            }
        } else {
            // If no secure lock screen is found, inform the user and navigate to security settings
            Toast.makeText(this, "No secure lock screen setup was found",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS)); // Open device security settings
        }
    }
}
