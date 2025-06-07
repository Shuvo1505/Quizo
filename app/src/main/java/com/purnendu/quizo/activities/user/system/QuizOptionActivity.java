package com.purnendu.quizo.activities.user.system;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.purnendu.quizo.R;
import com.purnendu.quizo.activities.subjects.ComputerQuizActivity;
import com.purnendu.quizo.activities.subjects.GeographyQuizActivity;
import com.purnendu.quizo.activities.subjects.LiteratureQuizActivity;
import com.purnendu.quizo.activities.subjects.MathQuizActivity;
import com.purnendu.quizo.utilities.Constants;
import com.purnendu.quizo.utilities.QuizoVibrator;

/**
 * This activity presents users with various quiz topics in the Quizo application,
 * allowing them to select a subject to begin a quiz.
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * It provides navigation to specific quiz activities based on the selected topic,
 * such as {@link com.purnendu.quizo.activities.subjects.ComputerQuizActivity},
 * {@link com.purnendu.quizo.activities.subjects.GeographyQuizActivity},
 * {@link com.purnendu.quizo.activities.subjects.LiteratureQuizActivity}, and
 * {@link com.purnendu.quizo.activities.subjects.MathQuizActivity}.
 * <p>
 * The activity utilizes Android UI components like {@link androidx.cardview.widget.CardView}
 * for topic selection. It passes the selected subject using {@link android.content.Intent}
 * and {@link com.purnendu.quizo.utilities.Constants}.
 * Haptic feedback is provided by {@link com.purnendu.quizo.utilities.QuizoVibrator}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for QuizOptionActivity
public class QuizOptionActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_quiz_option); // Set the layout for this activity

        // Set the navigation bar color to black
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,
                R.color.black));

        // Initialize CardViews for each quiz subject
        CardView cvMath = findViewById(R.id.cvMath);
        CardView cvGeography = findViewById(R.id.cvGeography);
        CardView cvLiterature = findViewById(R.id.cvLiterature);
        CardView cvComputer = findViewById(R.id.cvComputer);

        // Set OnClickListener for the back ImageView
        findViewById(R.id.imageViewQuizOption).setOnClickListener(V -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            finish(); // Close the current activity
        });

        // Set OnClickListener for the "Math" quiz CardView
        cvMath.setOnClickListener(view -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            Intent intent = new Intent(QuizOptionActivity.this,
                    MathQuizActivity.class); // Create intent for MathQuizActivity
            intent.putExtra(Constants.SUBJECT, getString(R.string.math)); // Pass subject as extra
            startActivity(intent); // Start the quiz activity
        });

        // Set OnClickListener for the "Geography" quiz CardView
        cvGeography.setOnClickListener(view -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            Intent intent = new Intent(QuizOptionActivity.this,
                    GeographyQuizActivity.class); // Create intent for GeographyQuizActivity
            intent.putExtra(Constants.SUBJECT, getString(R.string.geography)); // Pass subject as extra
            startActivity(intent); // Start the quiz activity
        });

        // Set OnClickListener for the "Literature" quiz CardView
        cvLiterature.setOnClickListener(view -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            Intent intent = new Intent(QuizOptionActivity.this,
                    LiteratureQuizActivity.class); // Create intent for LiteratureQuizActivity
            intent.putExtra(Constants.SUBJECT, getString(R.string.literature)); // Pass subject as extra
            startActivity(intent); // Start the quiz activity
        });

        // Set OnClickListener for the "Computer" quiz CardView
        cvComputer.setOnClickListener(view -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            Intent intent = new Intent(QuizOptionActivity.this,
                    ComputerQuizActivity.class); // Create intent for ComputerQuizActivity
            intent.putExtra(Constants.SUBJECT, getString(R.string.computer)); // Pass subject as extra
            startActivity(intent); // Start the quiz activity
        });
    }
}
