package com.purnendu.quizo.activities.subjects;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.purnendu.quizo.R;
import com.purnendu.quizo.activities.user.system.FinalResultActivity;
import com.purnendu.quizo.dao.QuestionDao;
import com.purnendu.quizo.dbclients.QuestionDatabaseClient;
import com.purnendu.quizo.models.Question;
import com.purnendu.quizo.utilities.Constants;
import com.purnendu.quizo.utilities.QuizoVibrator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * This activity facilitates the Mathematics quiz for users in the Quizo application.
 * It fetches questions related to "Math" from the database via
 * {@link com.purnendu.quizo.dbclients.QuestionDatabaseClient} and its {@link com.purnendu.quizo.dao.QuestionDao}.
 * Users answer multiple-choice questions, and their progress (correct/incorrect answers) is tracked.
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * Upon completion, results are passed to {@link com.purnendu.quizo.activities.user.system.FinalResultActivity}.
 * <p>
 * The activity utilizes various Android UI components including {@link android.widget.TextView},
 * {@link android.widget.RadioGroup}, {@link android.widget.RadioButton}, and {@link android.widget.Button}.
 * It manages quiz flow, question display, and answer validation.
 * Utility classes like {@link com.purnendu.quizo.utilities.Constants} for fixed values and
 * {@link com.purnendu.quizo.utilities.QuizoVibrator} for haptic feedback are integrated.
 * User feedback is provided through {@link android.widget.Toast} and {@link android.app.AlertDialog} for exit confirmation.
 * Asynchronous database operations are handled using {@link java.util.concurrent.Executors}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for MathQuizActivity
public class MathQuizActivity extends AppCompatActivity {

    // Current index of the question being displayed
    private int currentQuestionIndex = 0;

    // UI elements
    private TextView tvQuestion, tvQuestionNumber;
    private Button btnNext;
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2, radioButton3, radioButton4;

    // Quiz scoring variables
    private int correctQuestion = 0;
    private int totalQuestionsLoaded = 0; // Tracks the actual number of loaded questions

    // Data structures to hold quiz questions and their options/answers
    private List<String> questionsOrder; // Stores the question texts in display order
    private Map<String, Map<String, Boolean>>
            questionsAnswerMap; // Maps question text to its options map (option text -> is_correct)

    // Quiz subject and database access object
    private String currentSubject; // To store the subject passed from the intent
    private QuestionDao questionDao; // Room DAO instance

    /**
     * Helper method to create a map of options for a given question, indicating which option is correct.
     *
     * @param q The {@link com.purnendu.quizo.models.Question} object.
     * @return A map where keys are option texts and values are booleans indicating if the option is correct.
     */
    @NonNull
    private static Map<String, Boolean> getStringBooleanMap(Question q) {
        Map<String, Boolean> optionsForQuestion = new HashMap<>();
        optionsForQuestion.put(q.getOptionA(),
                q.getOptionA().equals(q.getCorrectAnswer()));
        optionsForQuestion.put(q.getOptionB(),
                q.getOptionB().equals(q.getCorrectAnswer()));
        optionsForQuestion.put(q.getOptionC(),
                q.getOptionC().equals(q.getCorrectAnswer()));
        optionsForQuestion.put(q.getOptionD(),
                q.getOptionD().equals(q.getCorrectAnswer()));
        return optionsForQuestion;
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
        setContentView(R.layout.activity_math_quiz); // Set the layout for this activity

        // Set the navigation bar color to black
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,
                R.color.black));

        // Add a callback to handle the back button press, showing a confirmation dialog
        getOnBackPressedDispatcher().addCallback(this, new
                OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        showConfirmation(); // Show exit confirmation dialog
                    }
                });

        // Get the subject from the intent that started this activity
        Intent intent = getIntent();
        currentSubject = intent.getStringExtra(Constants.SUBJECT);

        // Initialize Room Database DAO
        questionDao = QuestionDatabaseClient.getInstance(this).questionDao();

        // Initialize the title TextView
        TextView tvTitle = findViewById(R.id.textView26);

        // Set the title based on the current subject.
        assert currentSubject != null;
        if (currentSubject.equals(getString(R.string.math))) {
            tvTitle.setText(getString(R.string.math_quiz));
        } else {
            // Handle unexpected subject, e.g., show a toast and finish the activity
            Toast.makeText(this, "Invalid quiz subject.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize other UI elements
        tvQuestion = findViewById(R.id.textView78);
        tvQuestionNumber = findViewById(R.id.textView18);
        btnNext = findViewById(R.id.btnNextQuestionMath);
        radioGroup = findViewById(R.id.radioGroup);

        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);

        // Load questions from Room database asynchronously
        loadQuestions();

        // Set OnClickListener for the "Next" button
        btnNext.setOnClickListener(view -> {
            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
            if (selectedRadioButtonId == -1) {
                // If no answer is selected, show a toast and vibrate
                Toast.makeText(MathQuizActivity.this,
                        "Please select an answer", Toast.LENGTH_SHORT).show();
                QuizoVibrator.vibratePhone(this); // Vibrate on warning
                return;
            }

            // Get the selected radio button and its text
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            String selectedAnswerText = selectedRadioButton.getText().toString();

            QuizoVibrator.vibratePhone(this); // Provide haptic feedback

            // Basic validation for quiz data before proceeding
            if (questionsOrder == null || questionsOrder.isEmpty() ||
                    currentQuestionIndex >= totalQuestionsLoaded) {
                Toast.makeText(this, "Quiz data error or no more questions.",
                        Toast.LENGTH_SHORT).show();
                finish(); // Exit if no valid questions or index out of bounds
                return;
            }

            // Get the current question's text and its options map
            String currentQuestionText = questionsOrder.get(currentQuestionIndex);
            Map<String, Boolean> optionsForCurrentQuestion = questionsAnswerMap.
                    get(currentQuestionText);

            // Check if the selected answer is correct
            if (optionsForCurrentQuestion != null && Boolean.TRUE.
                    equals(optionsForCurrentQuestion.get(selectedAnswerText))) {
                correctQuestion++; // Increment correct answer count
            }

            currentQuestionIndex++; // Move to the next question

            // Check if there are more questions to display
            if (currentQuestionIndex < totalQuestionsLoaded) {
                displayNextQuestions(); // Display the next question
            } else {
                // All questions answered, navigate to FinalResultActivity
                Intent intentResult = getIntentResult(); // Get the intent for results
                startActivity(intentResult); // Start the result activity
                finish(); // Finish the current quiz activity
            }
        });

        // Set OnClickListener for the back button (ImageView)
        findViewById(R.id.imageViewStartQuizmath).setOnClickListener(V -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            showConfirmation(); // Show exit confirmation dialog
        });
    }

    /**
     * Prepares and returns an {@link android.content.Intent} to navigate to {@link FinalResultActivity}.
     * This intent includes the quiz subject, number of correct answers, number of incorrect answers,
     * and the total number of questions attempted.
     *
     * @return An Intent configured for {@link FinalResultActivity}.
     */
    @NonNull
    private Intent getIntentResult() {
        Intent intentResult = new Intent(MathQuizActivity.this,
                FinalResultActivity.class);
        intentResult.putExtra(Constants.SUBJECT, currentSubject); // Pass the quiz subject
        intentResult.putExtra(Constants.CORRECT, correctQuestion); // Pass correct answers count
        // Calculate and pass incorrect answers count
        intentResult.putExtra(Constants.INCORRECT, totalQuestionsLoaded -
                correctQuestion);
        // Pass the total questions attempted for accurate final result calculation
        intentResult.putExtra(Constants.TOTAL_QUESTIONS_ATTEMPTED, totalQuestionsLoaded);
        return intentResult;
    }

    /**
     * Loads questions from the Room database asynchronously based on the {@code currentSubject}.
     * It populates {@code questionsAnswerMap} and {@code questionsOrder} lists.
     * After loading, it calls {@link #displayData()} to show the first question or
     * finishes the activity if no questions are found.
     */
    private void loadQuestions() {
        radioGroup.clearCheck(); //clear any previous selection for safety
        Executors.newSingleThreadExecutor().execute(() -> {
            // Fetch all questions for the current subject from the database
            List<Question> fetchedQuestions = questionDao.
                    getRandomQuestionsByTopic(currentSubject);

            // Initialize temporary maps and lists to build data in the background thread
            final HashMap<String, Map<String, Boolean>> tempQuestionsAnswerMap =
                    new HashMap<>();
            final ArrayList<String> tempQuestionsOrder = new ArrayList<>();

            if (fetchedQuestions != null && !fetchedQuestions.isEmpty()) {
                // Iterate through fetched questions to populate the maps
                for (Question q : fetchedQuestions) {
                    // Validate the question before adding it to our quiz data
                    if (isValidQuestion(q)) {
                        Map<String, Boolean> optionsForQuestion = getStringBooleanMap(q);
                        if (optionsForQuestion.size() == 4) { // Ensure 4 options were mapped
                            tempQuestionsAnswerMap.put(q.getQuestionText(), optionsForQuestion);
                            tempQuestionsOrder.add(q.getQuestionText());
                        } else {
                            // Log or handle questions with an incorrect number of options after mapping
                            // This case should ideally be caught by isValidQuestion, but provides a safeguard.
                            System.err.println("Question '" + q.getQuestionText() + "' has an invalid number of options after processing.");
                        }
                    } else {
                        // Log or handle invalid questions (e.g., missing options or correct answer)
                        System.err.println("Skipping malformed question: " + q.getQuestionText());
                        Toast.makeText(this, "Skipping malformed question: " + q.getQuestionText(), Toast.LENGTH_SHORT).show(); // Added a toast for user feedback
                    }
                }
            }

            // After processing, assign to final variables for use in the UI thread
            final int finalTotalQuestionsLoaded = tempQuestionsOrder.size();

            // Now switch to the main thread to update UI components
            runOnUiThread(() -> { // Using runOnUiThread for UI updates
                if (finalTotalQuestionsLoaded == 0) {
                    // If no questions are loaded (or all were malformed), show a toast and finish the activity
                    Toast.makeText(this,
                            "No questions found for " + currentSubject,
                            Toast.LENGTH_LONG).show();
                    finish(); // Or redirect to an error screen
                } else {
                    // Assign the populated data to the activity's member variables
                    questionsAnswerMap = tempQuestionsAnswerMap;
                    questionsOrder = tempQuestionsOrder;
                    totalQuestionsLoaded = finalTotalQuestionsLoaded; // Assign the actual count
                    displayData(); // Display the first question once loaded
                }
            });
        });
    }

    /**
     * Helper method to validate if a Question object has all necessary data for a quiz.
     * Ensures all options and correct answer are non-null and non-empty, and the correct
     * answer is one of the provided options.
     *
     * @param q The Question object to validate.
     * @return true if the question is valid, false otherwise.
     */
    private boolean isValidQuestion(Question q) {
        return q != null &&
                q.getQuestionText() != null && !q.getQuestionText().trim().isEmpty() &&
                q.getOptionA() != null && !q.getOptionA().trim().isEmpty() &&
                q.getOptionB() != null && !q.getOptionB().trim().isEmpty() &&
                q.getOptionC() != null && !q.getOptionC().trim().isEmpty() &&
                q.getOptionD() != null && !q.getOptionD().trim().isEmpty() &&
                q.getCorrectAnswer() != null && !q.getCorrectAnswer().trim().isEmpty() &&
                (q.getCorrectAnswer().equals(q.getOptionA()) ||
                        q.getCorrectAnswer().equals(q.getOptionB()) ||
                        q.getCorrectAnswer().equals(q.getOptionC()) ||
                        q.getCorrectAnswer().equals(q.getOptionD()));
    }


    /**
     * Displays the next question in the quiz sequence.
     * It clears the radio group selection, updates the question text and number,
     * and sets the answer options to the radio buttons.
     * Changes the "Next" button text to "Finish" on the last question.
     */
    @SuppressLint("SetTextI18n") // Suppresses lint warning for string concatenation
    private void displayNextQuestions() {
        // Clear selection for the next question
        radioGroup.clearCheck();

        // Ensure currentQuestionIndex is within the actual loaded questions count
        if (currentQuestionIndex < totalQuestionsLoaded) {
            tvQuestion.setText(questionsOrder.get(currentQuestionIndex)); // Set question text
            tvQuestionNumber.setText("Current Question: " + (currentQuestionIndex + 1)
                    + "/" + totalQuestionsLoaded); // Update question number display
            setAnswersToRadioButton(); // Set answer options to radio buttons

            // Change button text to "Finish" if it's the last question
            if (currentQuestionIndex == totalQuestionsLoaded - 1) {
                btnNext.setText(R.string.finish);
            }
        }
        // No else block here. If currentQuestionIndex is out of bounds,
        // it means quiz is finished and will be handled by the click listener.
    }

    /**
     * Displays the current question's data on the UI.
     * This method is typically called after questions are initially loaded.
     * It clears radio button selections, sets the question text and number,
     * and populates the radio buttons with answer options.
     */
    @SuppressLint("SetTextI18n") // Suppresses lint warning for string concatenation
    private void displayData() {
        // Only display if questions are loaded and available at the current index
        if (questionsOrder != null && !questionsOrder.isEmpty() &&
                currentQuestionIndex < totalQuestionsLoaded) {
            radioGroup.clearCheck(); // Clear existing radio button selection
            tvQuestion.setText(questionsOrder.get(currentQuestionIndex)); // Set question text
            tvQuestionNumber.setText("Current Question: " + (currentQuestionIndex + 1)
                    + "/" + totalQuestionsLoaded); // Update question number display
            setAnswersToRadioButton(); // Set answer options
        } else {
            // If no questions to display (e.g., all were filtered out), show a toast and finish the activity
            Toast.makeText(this, "No valid questions to display.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Populates the radio buttons with the answer options for the current question.
     * It shuffles the options randomly to ensure variety in presentation.
     * The previous handling for "Invalid options for question. Skipping." is now
     * largely mitigated by the validation in loadQuestions().
     */
    private void setAnswersToRadioButton() {
        // Get the options map for the current question based on its text
        Map<String, Boolean> currentOptionsMap = questionsAnswerMap.
                get(questionsOrder.get(currentQuestionIndex));

        // This check should almost always pass now due to validation in loadQuestions()
        if (currentOptionsMap != null && currentOptionsMap.size() == 4) {
            // Convert map keys (answer texts) to a list for easy access
            List<String> answerTexts = new ArrayList<>(currentOptionsMap.keySet());
            Collections.shuffle(answerTexts); // Shuffle the order of options

            // Set the text for each radio button
            radioButton1.setText(answerTexts.get(0));
            radioButton2.setText(answerTexts.get(1));
            radioButton3.setText(answerTexts.get(2));
            radioButton4.setText(answerTexts.get(3));
        } else {
            // This 'else' block serves as a final fallback.
            // If you reach here, it implies an extreme edge case or a logic error
            // where a question passed initial validation but still somehow
            // resulted in invalid options during display.
            Toast.makeText(this,
                    "Internal Error: Question options corrupted. Ending quiz.",
                    Toast.LENGTH_LONG).show();
            // It's safer to end the quiz than try to skip, as this indicates a more severe issue.
            finish();
        }
    }

    /**
     * Displays an {@link android.app.AlertDialog} to confirm if the user wants to exit the quiz.
     * If confirmed, the activity finishes, otherwise, the dialog is dismissed.
     */
    private void showConfirmation() {
        new AlertDialog.Builder(MathQuizActivity.this)
                .setTitle("Warning") // Dialog title
                .setMessage(R.string.progress_lost) // Dialog message from resources
                .setIcon(R.drawable.ic_warning) // Dialog icon
                .setPositiveButton("Yes", (dialog, which) -> finish()) // On "Yes" click, finish activity
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // On "No" click, dismiss dialog
                .setCancelable(false) // Prevent dialog dismissal by touching outside
                .show();
    }
}
