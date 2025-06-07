package com.purnendu.quizo.activities.admin.system;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.purnendu.quizo.R;
import com.purnendu.quizo.dao.QuestionDao;
import com.purnendu.quizo.dbclients.QuestionDatabaseClient;
import com.purnendu.quizo.models.Question;
import com.purnendu.quizo.utilities.Keyboard;
import com.purnendu.quizo.utilities.QuizoVibrator;

import java.util.concurrent.Executors;

/**
 * This activity allows administrators to add quiz questions within the Quizo application.
 * It provides UI elements for inputting question details such as topic (via {@link android.widget.Spinner}),
 * question text, options, correct answer, and a boolean switch for question status.
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * Data is managed through the {@link com.purnendu.quizo.dbclients.QuestionDatabaseClient} and its
 * {@link com.purnendu.quizo.dao.QuestionDao}.
 * <p>
 * The activity uses various Android UI components including {@link android.widget.EditText},
 * {@link android.widget.Button}, {@link android.widget.ImageView}, {@link android.widget.Switch},
 * and {@link android.widget.TextView}.
 * It provides user feedback through {@link android.widget.Toast} and integrates utility functions
 * from {@link com.purnendu.quizo.utilities.Keyboard} for soft keyboard management and
 * {@link com.purnendu.quizo.utilities.QuizoVibrator} for haptic feedback.
 * Asynchronous database operations are handled using {@link java.util.concurrent.Executors}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
@SuppressLint("UseSwitchCompatOrMaterialCode")
// Suppresses lint warning for using Switch instead of MaterialSwitch
//Class for QuestionActivity
public class QuestionActivity extends AppCompatActivity {

    // UI elements for question input
    private EditText etQuestion, etAnswer1, etAnswer2, etAnswer3, etAnswer4;
    // UI elements for marking correct answers
    private Switch switchCorrect1, switchCorrect2, switchCorrect3, switchCorrect4;
    // Spinner for selecting the question's subject/topic
    private Spinner spinnerSubject;
    // Button to add the question to the database
    private Button btnAddQuestion;
    // TextView to display the current count of questions for the selected subject
    private TextView tvQuestionCountStatus;
    // ImageView acting as a back button
    private ImageView imageViewBack;

    // Data Access Object for interacting with the question database
    private QuestionDao questionDao;
    // Stores the currently selected subject from the spinner
    private String currentSelectedSubject;

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
        setContentView(R.layout.activity_admin_question);

        // Initialize the QuestionDao using the singleton database client
        questionDao = QuestionDatabaseClient.getInstance(this).questionDao();

        // Initialize all UI components
        initUI();

        // Retrieve the array of subjects from resources
        String[] subjectsArray = getResources().getStringArray(R.array.subjects_array);

        // Create an ArrayAdapter for the spinner using a custom layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
                R.layout.quizo_spinner, // Custom spinner item layout
                android.R.id.text1, // ID of the TextView within the custom layout
                subjectsArray
        );

        // Set the dropdown view resource for the spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Assign the adapter to the subject spinner
        spinnerSubject.setAdapter(adapter);

        // Set an item selected listener for the spinner
        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                // Update the currently selected subject
                currentSelectedSubject = parent.getItemAtPosition(position).toString();
                QuizoVibrator.vibratePhone(QuestionActivity.this); // Provide haptic feedback
                // Update the question count status for the new subject
                updateQuestionCountStatus(currentSelectedSubject);
                clearFields(); // Clear input fields when subject changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed when nothing is selected
            }
        });

        // Set OnClickListener for the "Add Question" button
        btnAddQuestion.setOnClickListener(v -> {
            QuizoVibrator.vibratePhone(QuestionActivity.this); // Provide haptic feedback
            Keyboard.hideKeyboard(this, v); // Hide the keyboard
            addQuestionToDatabase(); // Attempt to add the question to the database
        });

        // Set OnClickListener for the back ImageView
        imageViewBack.setOnClickListener(v -> {
            QuizoVibrator.vibratePhone(QuestionActivity.this); // Provide haptic feedback
            finish(); // Close the current activity
        });

        // Initial setup: set the current selected subject based on the spinner's default selection
        currentSelectedSubject = spinnerSubject.getSelectedItem().toString();
        // Update the question count status for the initial subject
        updateQuestionCountStatus(currentSelectedSubject);
    }

    /**
     * Initializes all UI components by finding their respective IDs from the layout file.
     */
    private void initUI() {
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer1 = findViewById(R.id.etAnswer1);
        etAnswer2 = findViewById(R.id.etAnswer2);
        etAnswer3 = findViewById(R.id.etAnswer3);
        etAnswer4 = findViewById(R.id.etAnswer4);
        switchCorrect1 = findViewById(R.id.switchCorrect1);
        switchCorrect2 = findViewById(R.id.switchCorrect2);
        switchCorrect3 = findViewById(R.id.switchCorrect3);
        switchCorrect4 = findViewById(R.id.switchCorrect4);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        tvQuestionCountStatus = findViewById(R.id.tvQuestionCountStatus);
        imageViewBack = findViewById(R.id.imageViewBack);
    }

    /**
     * Updates the TextView displaying the current number of questions for a given subject.
     * This operation is performed on a background thread to avoid blocking the UI,
     * and the result is posted back to the main thread.
     *
     * @param subject The subject for which to retrieve the question count.
     */
    @SuppressLint("SetTextI18n") // Suppresses lint warning for string concatenation
    private void updateQuestionCountStatus(String subject) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Get the question count for the specified topic from the database
            int count = questionDao.getQuestionCountByTopic(subject);
            // Post the UI update back to the main thread
            runOnUiThread(() -> {
                // Set the text view with the updated count and subject
                tvQuestionCountStatus.setText(getString(R.string.currently) + count +
                        getString(R.string.questions_for) + subject +
                        getString(R.string.dot));
                // Enable the add question button (it might have been disabled if no subject was selected initially)
                btnAddQuestion.setEnabled(true);
            });
        });
    }

    /**
     * Gathers input from the UI fields, validates it, and attempts to add a new question
     * to the database. It ensures that all fields are filled and exactly one option
     * is marked as correct. Database insertion is performed on a background thread.
     */
    private void addQuestionToDatabase() {
        // Retrieve trimmed text from all input fields
        final String questionText = etQuestion.getText().toString().trim();
        final String optionA = etAnswer1.getText().toString().trim();
        final String optionB = etAnswer2.getText().toString().trim();
        final String optionC = etAnswer3.getText().toString().trim();
        final String optionD = etAnswer4.getText().toString().trim();

        // Check the state of each correct answer switch
        boolean correct1 = switchCorrect1.isChecked();
        boolean correct2 = switchCorrect2.isChecked();
        boolean correct3 = switchCorrect3.isChecked();
        boolean correct4 = switchCorrect4.isChecked();

        // Validate that all question and answer fields are not empty
        if (questionText.isEmpty() || optionA.isEmpty() || optionB.isEmpty() ||
                optionC.isEmpty() || optionD.isEmpty()) {
            Toast.makeText(this, R.string.all_fields_are_required,
                    Toast.LENGTH_SHORT).show();
            return; // Exit if any field is empty
        }

        // Count how many options are marked as correct
        int correctCount = (correct1 ? 1 : 0) + (correct2 ? 1 : 0) + (correct3 ? 1 : 0)
                + (correct4 ? 1 : 0);

        // Validate that exactly one option is marked as correct
        if (correctCount != 1) {
            Toast.makeText(this,
                    R.string.exactly_one_option_must_be_marked_as_correct
                    , Toast.LENGTH_SHORT).show();
            return; // Exit if not exactly one correct option
        }

        // Determine the text of the correct answer based on which switch is checked
        String correctAnswerText;
        if (correct1) correctAnswerText = optionA;
        else if (correct2) correctAnswerText = optionB;
        else if (correct3) correctAnswerText = optionC;
        else correctAnswerText = optionD;

        // Create a new Question object with the collected data
        Question question = new Question(currentSelectedSubject, questionText,
                optionA, optionB, optionC, optionD, correctAnswerText);

        // Execute database insertion on a single background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            // Insert the question into the database and get the row ID
            long rowId = questionDao.insertQuestion(question);

            // Post UI updates back to the main thread
            runOnUiThread(() -> {
                if (rowId > 0) {
                    // Show success message, clear fields, and update question count
                    Toast.makeText(QuestionActivity.this,
                            R.string.question_added_successfully,
                            Toast.LENGTH_SHORT).show();
                    clearFields();
                    updateQuestionCountStatus(currentSelectedSubject);
                } else {
                    // Show failure message if insertion failed
                    Toast.makeText(QuestionActivity.this,
                            R.string.failed_to_add_question,
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    /**
     * Clears all input fields and resets the correct answer switches to unchecked.
     */
    private void clearFields() {
        etQuestion.setText("");
        etAnswer1.setText("");
        etAnswer2.setText("");
        etAnswer3.setText("");
        etAnswer4.setText("");
        switchCorrect1.setChecked(false);
        switchCorrect2.setChecked(false);
        switchCorrect3.setChecked(false);
        switchCorrect4.setChecked(false);
    }
}
