package com.purnendu.quizo.activities.admin.access;

import static com.purnendu.quizo.utilities.HashAlgo.hashPassword;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.purnendu.quizo.R;
import com.purnendu.quizo.component.AlertBox;
import com.purnendu.quizo.databases.AdminDatabase;
import com.purnendu.quizo.dbclients.AdminDatabaseClient;
import com.purnendu.quizo.models.Admin;
import com.purnendu.quizo.utilities.Keyboard;
import com.purnendu.quizo.utilities.QuizoVibrator;
import com.purnendu.quizo.utilities.SharedPref;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This activity allows administrators to change their password within the Quizo application.
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * It handles user input for new passwords, performs hashing using {@link com.purnendu.quizo.utilities.HashAlgo},
 * and updates the admin data in the {@link com.purnendu.quizo.databases.AdminDatabase} via
 * {@link com.purnendu.quizo.dbclients.AdminDatabaseClient}.
 * The activity utilizes Android UI components such as {@link android.widget.EditText}, {@link android.widget.Button},
 * and {@link android.widget.ImageView}, along with utility classes like {@link com.purnendu.quizo.utilities.Keyboard},
 * {@link com.purnendu.quizo.utilities.QuizoVibrator}, and {@link com.purnendu.quizo.utilities.SharedPref}.
 * It also provides user feedback through {@link android.widget.Toast} and custom dialogs via {@link com.purnendu.quizo.component.AlertBox}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for Admin Edit Password
public class AdminEditPassword extends AppCompatActivity {

    // UI elements for password input and actions
    private EditText etOldPassword, etNewPassword, etConfirmNewPassword;
    private Button btnSavePassword;
    private ImageView btnBack;

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
        setContentView(R.layout.activity_admin_edit_password); // Set the layout for this activity

        // Set the navigation bar color to black
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,
                R.color.black));

        // Initialize UI components
        etOldPassword = findViewById(R.id.tietPassword);
        etNewPassword = findViewById(R.id.tietPasswordNewPass);
        etConfirmNewPassword = findViewById(R.id.tietPasswordConfirmNewPass);
        btnSavePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.imageViewEditPassword);

        // Set OnClickListener for the back button
        btnBack.setOnClickListener(V -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            finish(); // Close the current activity
        });

        // Set OnClickListener for the "Save Password" button
        btnSavePassword.setOnClickListener(view -> {
            // Get current text from input fields
            String oldPassword = etOldPassword.getText().toString();
            String newPassword = etNewPassword.getText().toString();
            String confirmNewPassword = etConfirmNewPassword.getText().toString();

            Keyboard.hideKeyboard(this, view); // Hide the soft keyboard
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback

            // Validate user input; if validation fails, return
            if (!validateInput(oldPassword, newPassword, confirmNewPassword)) return;

            // If input is valid, proceed to change the password
            changePassword(oldPassword, newPassword);
        });
    }

    /**
     * Attempts to change the admin's password.
     * It retrieves the current admin from SharedPreferences, hashes the old and new passwords,
     * validates the old password, and then updates the admin data in the database
     * and SharedPreferences asynchronously.
     *
     * @param oldPassword The old password entered by the admin.
     * @param newPassword The new password entered by the admin.
     */
    private void changePassword(String oldPassword, String newPassword) {
        // Retrieve the current admin object from SharedPreferences
        Admin admin = SharedPref.getInstance().getAdmin(AdminEditPassword.this);
        // Hash the old password entered by the admin for comparison
        String oldHash = hashPassword(oldPassword);

        // Check if the entered old password matches the stored hashed password
        if (!admin.getPassword().equals(oldHash)) {
            Toast.makeText(this, R.string.please_enter_the_right_password,
                    Toast.LENGTH_SHORT).show();
            return; // Exit if old password is incorrect
        }

        // Hash the new password
        String newHash = hashPassword(newPassword);
        // Set the new hashed password to the admin object
        admin.setPassword(newHash);

        // Create an Executor for background thread operations
        Executor executor = Executors.newSingleThreadExecutor();
        // Create a Handler to post results back to the main (UI) thread
        Handler handler = new Handler(Looper.getMainLooper());

        // Execute database update on a background thread
        executor.execute(() -> {
            // Get the AdminDatabaseClient instance and access the AdminDao
            AdminDatabase adminDatabaseClient = AdminDatabaseClient.
                    getInstance(getApplicationContext());
            // Update the admin record in the database
            adminDatabaseClient.adminDao().updateAdmin(admin);

            // Post UI updates back to the main thread
            handler.post(() -> {
                // Disable buttons and change text to indicate processing
                btnBack.setEnabled(false);
                btnSavePassword.setEnabled(false);
                btnSavePassword.setText(R.string.please_wait);
                // Temporarily block the back button during the operation
                getOnBackPressedDispatcher().addCallback(this,
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                // Blocking
                            }
                        });
                // Show success message
                Toast.makeText(AdminEditPassword.this,
                        R.string.password_updated_successfully, Toast.LENGTH_SHORT).show();
                // Update the admin object in SharedPreferences with the new password
                SharedPref.getInstance().setAdmin(AdminEditPassword.this, admin);
                finish(); // Close the activity
            });
        });
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
     * Validates the admin's input for old, new, and confirmed new passwords.
     * Checks for empty fields, matching new and confirm passwords,
     * new password being different from old, and new password format.
     *
     * @param oldPassword        The old password entered by the admin.
     * @param newPassword        The new password entered by the admin.
     * @param confirmNewPassword The confirmed new password entered by the admin.
     * @return {@code true} if all inputs are valid, {@code false} otherwise.
     */
    private boolean validateInput(String oldPassword, String newPassword,
                                  String confirmNewPassword) {
        if (oldPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.old_password_cannot_be_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.new_password_cannot_be_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (confirmNewPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.confirm_password_cannot_be_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!confirmNewPassword.equals(newPassword)) {
            Toast.makeText(this, getString(R.string.password_must_be_same),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (oldPassword.equals(newPassword)) {
            Toast.makeText(this, getString(R.string.new_password_must_be_different),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!checkParametersPass(newPassword)) {
            // Show a custom alert dialog for password format issues
            AlertBox.showDialog(getString(R.string.password_format_error),
                    getString(R.string.password_warning),
                    AdminEditPassword.this);
            return false;
        }

        return true; // All validations passed
    }
}
