package com.purnendu.quizo.activities.info;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.purnendu.quizo.R;
import com.purnendu.quizo.activities.web.WebActivity;
import com.purnendu.quizo.utilities.Credits;
import com.purnendu.quizo.utilities.QuizoVibrator;

/**
 * This activity displays information about the Quizo application, including its version,
 * and provides links to external resources or credits.
 * It allows users to navigate to a {@link com.purnendu.quizo.activities.web.WebActivity}
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * to view dynamically generated links or other web content using {@link com.purnendu.quizo.utilities.Credits}.
 * <p>
 * The activity utilizes basic Android UI components such as {@link android.widget.TextView}
 * and {@link android.widget.ImageView}.
 * It provides haptic feedback via {@link com.purnendu.quizo.utilities.QuizoVibrator}
 * and handles standard Android activity lifecycle methods.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for About Activity
public class AboutActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_about); // Set the layout for this activity

        // Initialize UI components
        TextView openCredits = findViewById(R.id.openCredits);
        ImageView imageViewBack = findViewById(R.id.imageViewBack);

        // Set OnClickListener for the "Open Credits" TextView
        openCredits.setOnClickListener(v -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            Intent webIntent = new Intent(AboutActivity.this, WebActivity.class);
            // Pass a randomly generated URL from the Credits utility
            webIntent.putExtra("accessURL", Credits.generateRandomLink());
            webIntent.putExtra("webHeading", "Icon Creator"); // Set the heading for the web activity
            startActivity(webIntent); // Start the WebActivity
        });

        // Set OnClickListener for the back ImageView
        imageViewBack.setOnClickListener(v -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            finish(); // Close the current activity
        });
    }
}
