package com.purnendu.quizo.activities.user.access;

import static com.purnendu.quizo.utilities.HashAlgo.hashPassword;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.purnendu.quizo.R;
import com.purnendu.quizo.activities.admin.access.AdminLogin;
import com.purnendu.quizo.activities.user.system.UserActivity;
import com.purnendu.quizo.databases.UserDatabase;
import com.purnendu.quizo.dbclients.UserDatabaseClient;
import com.purnendu.quizo.models.User;
import com.purnendu.quizo.utilities.Keyboard;
import com.purnendu.quizo.utilities.QuizoVibrator;
import com.purnendu.quizo.utilities.SharedPref;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This activity handles the login process for regular users in the Quizo application.
 * It takes user credentials (email and password), hashes the password using {@link com.purnendu.quizo.utilities.HashAlgo},
 * and authenticates against the {@link com.purnendu.quizo.databases.UserDatabase} via
 * {@link com.purnendu.quizo.dbclients.UserDatabaseClient}.
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * Upon successful login, it navigates to {@link UserActivity}.
 * It also provides an option to navigate to the {@link com.purnendu.quizo.activities.admin.access.AdminLogin} screen.
 * <p>
 * The activity uses various Android UI components like {@link android.widget.EditText}, {@link android.widget.Button},
 * and {@link android.widget.TextView}.
 * It incorporates utility functions from {@link com.purnendu.quizo.utilities.Keyboard},
 * {@link com.purnendu.quizo.utilities.QuizoVibrator}, and {@link com.purnendu.quizo.utilities.SharedPref}
 * for UI management and session handling. User feedback is provided through {@link android.widget.Toast}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for LoginActivity
public class LoginActivity extends AppCompatActivity {

    // UI elements for user input and actions
    private EditText etUsername, etPassword;
    private TextView tvSignUp, btnLoginAdmin, t1, t2; // t1 and t2 seem to be labels for tvSignUp and btnLoginAdmin
    private Button btnLogin;

    @Nullable
    private static User getUser(String username, List<User> users, String hashedPassword) {
        User matchedUser = null;
        // Iterate through the list of users to find a match
        for (User user : users) {
            if (username.equals(user.getUsername()) &&
                    hashedPassword.equals(user.getPassword())) {
                matchedUser = user; // Found a matching user
                break; // Exit loop once matched
            }
        }

        // Capture the matchedUser for the UI thread
        return matchedUser;
    }

    /**
     * Called after {@link #onCreate} or after {@link #onRestart} when the activity had
     * been stopped, but is now being displayed to the user.  It will be followed by
     * {@link #onResume}.
     * <p>
     * This method is overridden to check if a user is already logged in via {@link SharedPref}.
     * If a user session exists, it directly navigates to {@link UserActivity} and finishes
     * the current activity, preventing re-login.
     */
    @Override
    protected void onStart() {
        super.onStart();
        SharedPref sharedPref = SharedPref.getInstance();
        // Check if there is an existing user session
        if (sharedPref.getUser(this) != null) {
            startActivity(new Intent(this, UserActivity.class)); // Go to UserActivity
            finish(); // Close LoginActivity
        }
    }

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
        setContentView(R.layout.activity_login); // Set the layout for this activity

        // Set the navigation bar color to black
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,
                R.color.black));

        // Initialize UI components
        etUsername = findViewById(R.id.tietUsername);
        etPassword = findViewById(R.id.tiePassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginAdmin = findViewById(R.id.tvSignAdmin);

        t1 = findViewById(R.id.tvSignUp4); // Label for SignUp
        t2 = findViewById(R.id.tvadmin); // Label for Admin Login

        // Set OnClickListener for the "Login" button
        btnLogin.setOnClickListener(v -> {
            // Get trimmed text from username and password fields
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            Keyboard.hideKeyboard(this, v); // Hide the soft keyboard
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback

            // Validate user input; if validation fails, return
            if (!validateInputs(username, password)) return;

            // Disable login button and hide other options during login process
            btnLogin.setEnabled(false);
            btnLogin.setText(R.string.please_wait);
            tvSignUp.setVisibility(View.GONE);
            btnLoginAdmin.setVisibility(View.GONE);
            t1.setVisibility(View.GONE);
            t2.setVisibility(View.GONE);

            // Attempt to log in the user asynchronously
            loginUser(username, password);
        });

        // Set OnClickListener for the "Sign Up" TextView
        tvSignUp.setOnClickListener(
                v -> {
                    QuizoVibrator.vibratePhone(this); // Provide haptic feedback
                    startActivity(new Intent(LoginActivity.this,
                            RegisterActivity.class)); // Navigate to RegisterActivity
                }
        );

        // Set OnClickListener for the "Login as Admin" TextView
        btnLoginAdmin.setOnClickListener(
                v -> {
                    QuizoVibrator.vibratePhone(this); // Provide haptic feedback
                    startActivity(new Intent(LoginActivity.this,
                            AdminLogin.class)); // Navigate to AdminLogin activity
                }
        );
    }

    /**
     * Checks if the username length is within the allowed limit (<= 20 characters).
     *
     * @param uname The username string to check.
     * @return {@code true} if the username length is valid, {@code false} otherwise.
     */
    private boolean checkLengthUname(String uname) {
        return uname.length() <= 20;
    }

    /**
     * Checks if the password length is within the allowed limit (<= 16 characters).
     *
     * @param pass The password string to check.
     * @return {@code true} if the password length is valid, {@code false} otherwise.
     */
    private boolean checkParametersPass(String pass) {
        if (pass == null) return false;
        return pass.length() <= 16;
    }

    /**
     * Validates the username and password inputs.
     * Checks for empty fields and length constraints.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     * @return {@code true} if inputs are valid, {@code false} otherwise.
     */
    private boolean validateInputs(String username, String password) {
        if (username.isEmpty()) {
            Toast.makeText(this, getString(R.string.username_cannot_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.password_cannot_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!checkLengthUname(username)) {
            Toast.makeText(this, getString(R.string.username_too_long),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!checkParametersPass(password)) {
            Toast.makeText(this, getString(R.string.password_too_long),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true; // All validations passed
    }

    /**
     * Authenticates the user by comparing the provided credentials with those stored in the database.
     * This operation is performed on a background thread.
     * If authentication is successful, the user session is saved, and the user is navigated to {@link UserActivity}.
     * Otherwise, an error message is displayed.
     *
     * @param username The username provided by the user.
     * @param password The plaintext password provided by the user.
     */
    private void loginUser(String username, String password) {
        // Create an Executor for background thread operations
        Executor executor = Executors.newSingleThreadExecutor();
        // Create a Handler to post results back to the main (UI) thread
        Handler handler = new Handler(Looper.getMainLooper());

        // Execute the login logic on a background thread
        executor.execute(() -> {
            // Get the UserDatabase instance
            UserDatabase db = UserDatabaseClient.getInstance(getApplicationContext());
            // Retrieve all users from the database (for demonstration; in a real app,
            // you'd query for a specific user by username/email)
            List<User> users = db.userDao().observeAllUser();
            // Hash the provided password for comparison with stored hashed passwords
            String hashedPassword = hashPassword(password);

            User finalMatchedUser = getUser(username, users, hashedPassword);
            // Post UI updates back to the main thread
            handler.post(() -> {
                if (finalMatchedUser != null) {
                    // If a user is matched, update UI to indicate success/processing
                    btnLogin.setEnabled(false);
                    btnLogin.setText(R.string.please_wait);
                    tvSignUp.setVisibility(View.GONE);
                    btnLoginAdmin.setVisibility(View.GONE);
                    t1.setVisibility(View.GONE);
                    t2.setVisibility(View.GONE);

                    // Block back button during transition
                    getOnBackPressedDispatcher().addCallback(this,
                            new OnBackPressedCallback(true) {
                                @Override
                                public void handleOnBackPressed() {
                                    // Blocking
                                }
                            });
                    // Save the authenticated user to SharedPreferences
                    SharedPref.getInstance().setUser(LoginActivity.this,
                            finalMatchedUser);
                    // Navigate to UserActivity
                    startActivity(new Intent(LoginActivity.this,
                            UserActivity.class));
                    finish(); // Close LoginActivity
                } else {
                    // If no user matched, revert UI and show error toast
                    btnLogin.setEnabled(true);
                    btnLogin.setText(R.string.login);
                    tvSignUp.setVisibility(View.VISIBLE);
                    btnLoginAdmin.setVisibility(View.VISIBLE);
                    t1.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Wrong credentials!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
