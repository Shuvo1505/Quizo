package com.purnendu.quizo.activities.user.access;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.purnendu.quizo.R;
import com.purnendu.quizo.activities.user.system.UserActivity;
import com.purnendu.quizo.component.AlertBox;
import com.purnendu.quizo.databases.UserDatabase;
import com.purnendu.quizo.dbclients.UserDatabaseClient;
import com.purnendu.quizo.models.User;
import com.purnendu.quizo.utilities.HashAlgo;
import com.purnendu.quizo.utilities.Keyboard;
import com.purnendu.quizo.utilities.QuizoVibrator;
import com.purnendu.quizo.utilities.SharedPref;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This activity facilitates the registration of new users for the Quizo application.
 * It collects user details such as username, email, and password, performs input validation
 * (e.g., email pattern check using {@link android.util.Patterns} and empty field checks using {@link android.text.TextUtils}),
 * and hashes the password using {@link com.purnendu.quizo.utilities.HashAlgo}.
 * The new user data is then inserted into the {@link com.purnendu.quizo.databases.UserDatabase}
 * via {@link com.purnendu.quizo.dbclients.UserDatabaseClient}. It handles potential
 * {@link android.database.sqlite.SQLiteConstraintException} for duplicate entries.
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * Upon successful registration, it navigates to {@link UserActivity}.
 * <p>
 * The activity uses Android UI components like {@link android.widget.EditText}, {@link android.widget.Button},
 * and {@link android.widget.ImageView}, and utility classes such as {@link com.purnendu.quizo.utilities.Keyboard},
 * {@link com.purnendu.quizo.utilities.QuizoVibrator}, and {@link com.purnendu.quizo.utilities.SharedPref}.
 * User feedback is provided through {@link android.widget.Toast} and custom dialogs using {@link com.purnendu.quizo.component.AlertBox}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for RegisterActivity
public class RegisterActivity extends AppCompatActivity {

    // UI elements for user input and actions
    private EditText etUsername, etEmail, etPassword;
    private ImageView btnBack;
    private Button btnRegister;

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
        setContentView(R.layout.activity_register); // Set the layout for this activity

        // Set the navigation bar color to black
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,
                R.color.black));

        // Initialize UI components
        btnBack = findViewById(R.id.imageView);
        etUsername = findViewById(R.id.tietUsername);
        etEmail = findViewById(R.id.tieEmail);
        etPassword = findViewById(R.id.tiePassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Set OnClickListener for the back button
        btnBack.setOnClickListener(V -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            finish(); // Close the current activity
        });

        // Set OnClickListener for the "Register" button
        btnRegister.setOnClickListener(v -> {
            // Get trimmed text from input fields
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            Keyboard.hideKeyboard(this, v); // Hide the soft keyboard

            QuizoVibrator.vibratePhone(this); // Provide haptic feedback

            // Validate user input; if validation fails, return
            if (!validateInputs(username, email, password)) return;

            // Disable register button and back button during registration process
            btnRegister.setEnabled(false);
            btnRegister.setText(R.string.please_wait);
            btnBack.setEnabled(false);

            // Attempt to register the user asynchronously
            registerUser(username, email, password);
        });
    }

    /**
     * Validates if the given character sequence is a valid email address.
     * Checks for non-empty, length <= 50, and matches a standard email pattern.
     *
     * @param target The character sequence to validate as an email.
     * @return {@code true} if the target is a valid email, {@code false} otherwise.
     */
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target)
                && target.length() <= 50
                && Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
     * Checks if the provided password string meets the specified format requirements.
     * The regex requires:
     * - At least one letter (uppercase or lowercase)
     * - At least one digit
     * - At least one special character (@#$%^&+=!)
     * - Length between 8 and 16 characters
     *
     * @param pass The password string to validate.
     * @return {@code true} if the password matches the regex, {@code false} otherwise.
     */
    private boolean checkParametersPass(String pass) {
        if (pass == null) return false;
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,16}$";
        return pass.matches(regex);
    }

    /**
     * Validates all user inputs (username, email, password) for registration.
     * Checks for empty fields, username length, email format, and password format.
     *
     * @param username The username entered by the user.
     * @param email    The email entered by the user.
     * @param password The password entered by the user.
     * @return {@code true} if all inputs are valid, {@code false} otherwise.
     */
    private boolean validateInputs(String username, String email, String password) {
        if (username.isEmpty()) {
            Toast.makeText(this, getString(R.string.username_cannot_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!checkLengthUname(username)) {
            Toast.makeText(this, getString(R.string.username_too_long),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, getString(R.string.email_cannot_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidEmail(email)) {
            Toast.makeText(this, getString(R.string.email_not_valid),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.password_cannot_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!checkParametersPass(password)) {
            // Show a custom alert dialog for password format issues
            AlertBox.showDialog(getString(R.string.password_format_error),
                    getString(R.string.password_warning), RegisterActivity.this);
            return false;
        }
        return true; // All validations passed
    }

    /**
     * Registers a new user by inserting their details into the database.
     * This operation is performed on a background thread.
     * Handles potential {@link SQLiteConstraintException} for duplicate email addresses.
     * Upon successful registration, the user is logged in and navigated to {@link UserActivity}.
     *
     * @param username The username for the new user.
     * @param email    The email for the new user (primary key).
     * @param password The plaintext password for the new user.
     */
    private void registerUser(String username, String email, String password) {
        // Create an Executor for background thread operations
        Executor executor = Executors.newSingleThreadExecutor();
        // Create a Handler to post results back to the main (UI) thread
        Handler handler = new Handler(Looper.getMainLooper());

        // Execute the registration logic on a background thread
        executor.execute(() -> {
            // Get the UserDatabase instance
            UserDatabase db = UserDatabaseClient.getInstance(getApplicationContext());
            // Hash the password before storing it
            String hashedPassword = HashAlgo.hashPassword(password);
            // Create a new User object
            User user = new User(username, email, hashedPassword);
            boolean isSuccess = true;

            try {
                // Attempt to insert the new user into the database
                db.userDao().insertUser(user);
            } catch (SQLiteConstraintException e) {
                // Catch constraint exception if email already exists
                isSuccess = false;
            }

            // Capture the success status for the UI thread
            boolean finalIsSuccess = isSuccess;
            // Post UI updates back to the main thread
            handler.post(() -> {
                if (finalIsSuccess) {
                    // If registration is successful, update UI to indicate success
                    btnRegister.setEnabled(false);
                    btnRegister.setText(R.string.please_wait);
                    btnBack.setEnabled(false);
                    // Block back button during transition
                    getOnBackPressedDispatcher().addCallback(this,
                            new OnBackPressedCallback(true) {
                                @Override
                                public void handleOnBackPressed() {
                                    // Blocking
                                }
                            });
                    // Save the newly registered user to SharedPreferences (auto-login)
                    SharedPref.getInstance().setUser(RegisterActivity.this, user);
                    // Prepare intent to navigate to UserActivity
                    Intent intent = new Intent(RegisterActivity.this,
                            UserActivity.class);
                    // Clear activity stack to prevent going back to registration
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(RegisterActivity.this, "User Created!",
                            Toast.LENGTH_SHORT).show();
                    startActivity(intent); // Start UserActivity
                    finish(); // Close RegisterActivity
                } else {
                    // If registration failed (e.g., duplicate email), revert UI and show error
                    btnRegister.setEnabled(true);
                    btnRegister.setText(R.string.sign_up);
                    btnBack.setEnabled(true);
                    Toast.makeText(RegisterActivity.this,
                            "This email is already used by someone else",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
