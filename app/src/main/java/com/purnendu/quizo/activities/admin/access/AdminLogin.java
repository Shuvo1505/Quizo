package com.purnendu.quizo.activities.admin.access;

import static com.purnendu.quizo.utilities.HashAlgo.hashPassword;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.purnendu.quizo.R;
import com.purnendu.quizo.activities.admin.system.AdminActivity;
import com.purnendu.quizo.component.AlertBox;
import com.purnendu.quizo.databases.AdminDatabase;
import com.purnendu.quizo.dbclients.AdminDatabaseClient;
import com.purnendu.quizo.models.Admin;
import com.purnendu.quizo.utilities.Keyboard;
import com.purnendu.quizo.utilities.QuizoVibrator;
import com.purnendu.quizo.utilities.SharedPref;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This activity handles the login process for administrators in the Quizo application.
 * It takes admin credentials (email and password), hashes the password using {@link com.purnendu.quizo.utilities.HashAlgo},
 * and authenticates against the {@link com.purnendu.quizo.databases.AdminDatabase} via
 * {@link com.purnendu.quizo.dbclients.AdminDatabaseClient}.
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * Upon successful login, it navigates to {@link com.purnendu.quizo.activities.admin.system.AdminActivity}.
 * The activity uses various Android UI components like {@link android.widget.EditText}, {@link android.widget.Button},
 * {@link android.widget.ImageView}, and {@link android.widget.TextView}.
 * It also incorporates utility functions from {@link com.purnendu.quizo.utilities.Keyboard},
 * {@link com.purnendu.quizo.utilities.QuizoVibrator}, and {@link com.purnendu.quizo.utilities.SharedPref}
 * for UI management and session handling. User feedback is provided through {@link android.widget.Toast}
 * and custom dialogs using {@link com.purnendu.quizo.component.AlertBox}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
// Class for Admin Login
public class AdminLogin extends AppCompatActivity {

    // UI elements for admin login
    private EditText etUsername, etPassword;
    private TextView registerAdmin, t1; // t1 likely a label for registerAdmin
    private Button loginAdmin;
    private ImageView backBtn;

    @Nullable
    private static Admin getAdmin(String username, List<Admin> admins, String hashedPassword) {
        Admin matchedAdmin = null;
        // Iterate through the list of admins to find a match
        for (Admin admin : admins) {
            if (username.equals(admin.getUsername()) && hashedPassword.
                    equals(admin.getPassword())) {
                matchedAdmin = admin; // Found a matching admin
                break; // Exit loop once matched
            }
        }

        // Capture the matchedAdmin for the UI thread
        return matchedAdmin;
    }

    /**
     * Called after {@link #onCreate} or after {@link #onRestart} when the activity had
     * been stopped, but is now being displayed to the user.  It will be followed by
     * {@link #onResume}.
     * <p>
     * This method is overridden to check if an admin is already logged in via {@link SharedPref}.
     * If an admin session exists, it directly navigates to {@link AdminActivity} and finishes
     * the current activity, preventing re-login.
     */
    @Override
    protected void onStart() {
        super.onStart();
        SharedPref sharedPref = SharedPref.getInstance();
        // Check if there is an existing admin session
        if (sharedPref.getAdmin(this) != null) {
            startActivity(new Intent(this, AdminActivity.class)); // Go to AdminActivity
            finish(); // Close AdminLogin activity
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
        setContentView(R.layout.activity_admin_login); // Set the layout for this activity

        // Set the navigation bar color to black
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,
                R.color.black));

        // Initialize UI components
        etUsername = findViewById(R.id.usernameAdmin);
        etPassword = findViewById(R.id.passwordAdmin);
        registerAdmin = findViewById(R.id.registerAdmin);
        loginAdmin = findViewById(R.id.loginAdmin);
        t1 = findViewById(R.id.tvSignUp40); // Label for Register Admin
        backBtn = findViewById(R.id.imageViewad); // Back button

        // Set OnClickListener for the back button
        backBtn.setOnClickListener(V -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            finish(); // Close the current activity
        });

        // Set OnClickListener for the "Login" button
        loginAdmin.setOnClickListener(v -> {
            // Get trimmed text from username and password fields
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            Keyboard.hideKeyboard(this, v); // Hide the soft keyboard
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback

            // Validate user input; if validation fails, return
            if (!validateInputs(username, password)) return;

            // Disable buttons and hide other options during login process
            backBtn.setEnabled(false);
            loginAdmin.setEnabled(false);
            loginAdmin.setText(R.string.please_wait);
            t1.setVisibility(View.INVISIBLE);
            registerAdmin.setVisibility(View.INVISIBLE);

            // Attempt to log in the admin asynchronously
            loginAdmin(username, password);
        });

        // Set OnClickListener for the "Register Admin" TextView
        registerAdmin.setOnClickListener(
                v -> {
                    QuizoVibrator.vibratePhone(this); // Provide haptic feedback
                    startActivity(new Intent(AdminLogin.this,
                            AdminRegister.class)); // Navigate to AdminRegister activity
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
     * Validates the username and password inputs for admin login.
     * Checks for empty fields and length constraints.
     *
     * @param username The username entered by the admin.
     * @param password The password entered by the admin.
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
            // Show a custom alert dialog for password format issues
            AlertBox.showDialog(getString(R.string.password_format_error),
                    getString(R.string.password_warning), AdminLogin.this);
            return false;
        }
        return true; // All validations passed
    }

    /**
     * Authenticates the admin by comparing the provided credentials with those stored in the database.
     * This operation is performed on a background thread.
     * If authentication is successful, the admin session is saved, and the admin is navigated to {@link AdminActivity}.
     * Otherwise, an error message is displayed.
     *
     * @param username The username provided by the admin.
     * @param password The plaintext password provided by the admin.
     */
    private void loginAdmin(String username, String password) {
        // Create an Executor for background thread operations
        Executor executor = Executors.newSingleThreadExecutor();
        // Create a Handler to post results back to the main (UI) thread
        Handler handler = new Handler(Looper.getMainLooper());

        // Execute the login logic on a background thread
        executor.execute(() -> {
            // Get the AdminDatabase instance
            AdminDatabase db = AdminDatabaseClient.getInstance(getApplicationContext());
            // Retrieve all admins from the database (for demonstration; in a real app,
            // you'd query for a specific admin by username/email)
            List<Admin> admins = db.adminDao().observeAllAdmins();
            // Hash the provided password for comparison with stored hashed passwords
            String hashedPassword = hashPassword(password);

            Admin finalMatchedAdmin = getAdmin(username, admins, hashedPassword);
            // Post UI updates back to the main thread
            handler.post(() -> {
                if (finalMatchedAdmin != null) {
                    // If an admin is matched, update UI to indicate success/processing
                    backBtn.setEnabled(false);
                    loginAdmin.setEnabled(false);
                    loginAdmin.setText(R.string.please_wait);
                    t1.setVisibility(View.INVISIBLE);
                    registerAdmin.setVisibility(View.INVISIBLE);
                    // Block back button during transition
                    getOnBackPressedDispatcher().addCallback(this,
                            new OnBackPressedCallback(true) {
                                @Override
                                public void handleOnBackPressed() {
                                    // Blocking
                                }
                            });
                    // Save the authenticated admin to SharedPreferences
                    SharedPref.getInstance().setAdmin(AdminLogin.this,
                            finalMatchedAdmin);
                    // Navigate to AdminActivity
                    startActivity(new Intent(AdminLogin.this,
                            AdminActivity.class));
                    finish(); // Close AdminLogin activity
                } else {
                    // If no admin matched, revert UI and show error toast
                    backBtn.setEnabled(true);
                    loginAdmin.setEnabled(true);
                    loginAdmin.setText(R.string.login);
                    t1.setVisibility(View.VISIBLE);
                    registerAdmin.setVisibility(View.VISIBLE);
                    Toast.makeText(AdminLogin.this, "Wrong credentials!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
