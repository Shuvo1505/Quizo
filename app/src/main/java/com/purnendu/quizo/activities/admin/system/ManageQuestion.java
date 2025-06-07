package com.purnendu.quizo.activities.admin.system;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.purnendu.quizo.R;
import com.purnendu.quizo.dao.QuestionDao;
import com.purnendu.quizo.databases.QuestionDatabase;
import com.purnendu.quizo.dbclients.QuestionDatabaseClient;
import com.purnendu.quizo.models.Question;
import com.purnendu.quizo.utilities.QuizoVibrator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This activity provides administrators with the functionality to manage quiz questions within the Quizo application.
 * It allows filtering questions by topic using a {@link android.widget.Spinner} and displays them in a
 * {@link androidx.recyclerview.widget.RecyclerView}.
 * t extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * Administrators can view, edit, and delete questions, interacting directly with the
 * {@link com.purnendu.quizo.databases.QuestionDatabase} via {@link com.purnendu.quizo.dbclients.QuestionDatabaseClient}
 * and its {@link com.purnendu.quizo.dao.QuestionDao}.
 * <p>
 * The activity utilizes various Android UI components including {@link android.widget.TextView},
 * {@link android.widget.ImageView}, and custom layouts for RecyclerView items.
 * It provides user feedback through {@link android.widget.Toast} and {@link android.app.AlertDialog} for confirmations.
 * Asynchronous database operations are managed using {@link java.util.concurrent.Executor} and {@link android.os.Handler}.
 * Haptic feedback is provided by {@link com.purnendu.quizo.utilities.QuizoVibrator}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for ManageQuestion
public class ManageQuestion extends AppCompatActivity {

    // Hardcoded string literals for messages and formatting
    private static final String NO_TOPICS_AVAILABLE = "No topics available";
    private static final String NO_QUESTIONS_FOR_THIS_TOPIC = "No questions for this topic.";
    private static final String QUESTION_DELETED_SUCCESSFULLY =
            "Question deleted successfully!";
    private static final String FAILED_TO_DELETE_QUESTION = "Failed to delete question.";
    private static final String OPTION_A_FORMAT = "A. %s";
    private static final String OPTION_B_FORMAT = "B. %s";
    private static final String OPTION_C_FORMAT = "C. %s";
    private static final String OPTION_D_FORMAT = "D. %s";
    private static final String CORRECT_ANSWER_FORMAT = "Correct Answer: %s";

    // Lists to hold question data and topics
    private final List<Question> questionsList = new ArrayList<>();
    private final List<String> topicsList = new ArrayList<>();

    // Executor for background database operations to prevent UI freezing
    private final Executor executor = Executors.newSingleThreadExecutor();
    // Handler to post results back to the main (UI) thread
    private final Handler handler = new Handler(Looper.getMainLooper());

    // UI components
    private Spinner spinnerTopics;
    private QuestionAdapter questionAdapter;
    private ArrayAdapter<String> topicsAdapter;

    // Data Access Object for questions
    private QuestionDao questionDao;

    // Currently selected topic
    private String selectedTopic = "";

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
        setContentView(R.layout.activity_manage_questions);

        // Set the navigation bar color to black
        getWindow().setNavigationBarColor(ContextCompat.
                getColor(this, R.color.black));

        // Initialize views
        ImageView btnBack = findViewById(R.id.imageViewBack);
        spinnerTopics = findViewById(R.id.spinnerTopics);
        RecyclerView recyclerViewQuestions = findViewById(R.id.recyclerViewQuestions);

        // Initialize database client and DAO for questions
        QuestionDatabase questionDatabase = QuestionDatabaseClient.
                getInstance(getApplicationContext());
        questionDao = questionDatabase.questionDao();

        // Setup RecyclerView with a LinearLayoutManager and the custom adapter
        recyclerViewQuestions.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new QuestionAdapter(questionsList);
        recyclerViewQuestions.setAdapter(questionAdapter);

        // Setup Spinner with a custom layout for items
        topicsAdapter = new ArrayAdapter<>(this,
                R.layout.quizo_spinner, // Custom spinner item layout
                android.R.id.text1, // TextView ID within the custom layout
                topicsList
        );

        // Set the dropdown view resource for the spinner
        topicsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Assign the adapter to the spinner
        spinnerTopics.setAdapter(topicsAdapter);

        // Load topics from the database when the activity starts
        loadTopics();

        // Set an item selected listener for the spinner to filter questions by topic
        spinnerTopics.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                QuizoVibrator.vibratePhone(ManageQuestion.this); // Provide haptic feedback
                selectedTopic = parent.getItemAtPosition(position).toString(); // Get selected topic
                loadQuestionsForTopic(selectedTopic); // Load questions based on the selected topic
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });

        // Set OnClickListener for the back button
        btnBack.setOnClickListener(v -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            finish(); // Close the current activity
        });
    }

    /**
     * Loads distinct quiz topics from the database in a background thread.
     * Updates the spinner's adapter on the main thread once topics are fetched.
     * If no topics are found, a placeholder message is displayed.
     */
    private void loadTopics() {
        executor.execute(() -> {
            List<String> fetchedTopics = questionDao.getDistinctTopics(); // Fetch distinct topics
            handler.post(() -> {
                topicsList.clear(); // Clear existing topics
                if (fetchedTopics != null && !fetchedTopics.isEmpty()) {
                    topicsList.addAll(fetchedTopics); // Add fetched topics
                    // Select the first topic by default or previously selected one
                    if (!selectedTopic.isEmpty() && topicsList.contains(selectedTopic)) {
                        spinnerTopics.setSelection(topicsList.indexOf(selectedTopic));
                    } else {
                        selectedTopic = topicsList.get(0);
                    }
                } else {
                    // If no topics, add a placeholder
                    topicsList.add(NO_TOPICS_AVAILABLE);
                    selectedTopic = NO_TOPICS_AVAILABLE;
                    findViewById(R.id.textEmptyHolder).setVisibility(View.VISIBLE);
                }
                topicsAdapter.notifyDataSetChanged(); // Notify adapter of data change
                loadQuestionsForTopic(selectedTopic); // Load questions for the initially selected topic
            });
        });
    }

    /**
     * Loads questions for a specific topic from the database in a background thread.
     * Updates the RecyclerView's adapter on the main thread once questions are fetched.
     * Displays a toast message if no questions are found for the selected topic.
     *
     * @param topic The topic for which to load questions.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void loadQuestionsForTopic(String topic) {
        // If no topics are available, clear the questions list and return
        if (topic.equals(NO_TOPICS_AVAILABLE)) {
            questionsList.clear();
            questionAdapter.notifyDataSetChanged();
            return;
        }

        executor.execute(() -> {
            List<Question> fetchedQuestions = questionDao.getQuestionsByTopic(topic); // Fetch questions by topic
            handler.post(() -> {
                questionsList.clear(); // Clear existing questions
                if (fetchedQuestions != null) {
                    questionsList.addAll(fetchedQuestions);
                }
                questionAdapter.notifyDataSetChanged(); // Notify adapter of data change
                if (questionsList.isEmpty()) {
                    // Show a toast if no questions are found for the topic
                    Toast.makeText(this, NO_QUESTIONS_FOR_THIS_TOPIC,
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Deletes a given question from the database in a background thread.
     * Displays a toast message indicating success or failure and reloads topics/questions.
     *
     * @param question The {@link com.purnendu.quizo.models.Question} object to be deleted.
     */
    private void deleteQuestion(Question question) {
        executor.execute(() -> {
            int rowsAffected = questionDao.deleteQuestion(question); // Execute deletion
            handler.post(() -> {
                if (rowsAffected > 0) {
                    Toast.makeText(this, QUESTION_DELETED_SUCCESSFULLY,
                            Toast.LENGTH_SHORT).show();
                    // No need to call topicsAdapter.notifyDataSetChanged() here,
                    // as loadTopics() will handle it.
                    loadTopics(); // Reload topics and questions after deletion
                } else {
                    Toast.makeText(this, FAILED_TO_DELETE_QUESTION,
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Displays an {@link android.app.AlertDialog} to confirm the deletion of a question.
     * If confirmed, it calls {@link #deleteQuestion(Question)}.
     *
     * @param questionToDelete The specific {@link Question} object to be deleted.
     */
    private void showConfirmation(final Question questionToDelete) { // Changed to accept parameter
        new AlertDialog.Builder(ManageQuestion.this)
                .setTitle("Confirm Deletion") // Dialog title
                .setMessage(R.string.deletion_message) // Dialog message from resources
                .setPositiveButton("Yes", (dialog, which) -> deleteQuestion(questionToDelete)) // Pass the specific question
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Dismiss dialog on "No"
                .setCancelable(false) // Prevent dialog dismissal by touching outside
                .show();
    }

    /**
     * RecyclerView Adapter for displaying a list of {@link com.purnendu.quizo.models.Question} objects.
     * It inflates the `item_question_manage` layout for each question and binds question data to the views.
     */
    private class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.
            QuestionViewHolder> {

        private final List<Question> questions; // List of questions to display

        /**
         * Constructor for the QuestionAdapter.
         *
         * @param questions The list of {@link com.purnendu.quizo.models.Question} objects to be displayed.
         */
        public QuestionAdapter(List<Question> questions) {
            this.questions = questions;
        }

        /**
         * Called when RecyclerView needs a new {@link QuestionViewHolder} of the given type to represent
         * an item.
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new QuestionViewHolder that holds a View of the given view type.
         */
        @NonNull
        @Override
        public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_question_manage, parent, false); // Inflate item layout
            return new QuestionViewHolder(view);
        }

        /**
         * Called by RecyclerView to display the data at the specified position. This method
         * updates the contents of the {@link QuestionViewHolder#itemView} to reflect the item at the given
         * position.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
            // Get the question for the current position. This 'currentQuestion' is local to this onBindViewHolder call.
            final Question currentQuestion = questions.get(position);

            holder.tvQuestionText.setText(currentQuestion.getQuestionText()); // Set question text
            // Set option texts using format strings
            holder.tvOptionA.setText(String.format(OPTION_A_FORMAT, currentQuestion.getOptionA()));
            holder.tvOptionB.setText(String.format(OPTION_B_FORMAT, currentQuestion.getOptionB()));
            holder.tvOptionC.setText(String.format(OPTION_C_FORMAT, currentQuestion.getOptionC()));
            holder.tvOptionD.setText(String.format(OPTION_D_FORMAT, currentQuestion.getOptionD()));
            // Set correct answer text
            holder.tvCorrectAnswer.setText(String.format(CORRECT_ANSWER_FORMAT,
                    currentQuestion.getCorrectAnswer()));

            // Set OnClickListener for the delete button
            holder.btnDelete.setOnClickListener(v -> {
                QuizoVibrator.vibratePhone(ManageQuestion.this); // Provide haptic feedback
                // Pass the specific 'currentQuestion' object to the confirmation dialog
                showConfirmation(currentQuestion);
            });
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            return questions.size();
        }

        /**
         * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
         * This inner class holds references to all UI components within a single question item layout.
         */
        class QuestionViewHolder extends RecyclerView.ViewHolder {
            TextView tvQuestionText, tvOptionA, tvOptionB, tvOptionC, tvOptionD,
                    tvCorrectAnswer;
            ImageView btnDelete;

            /**
             * Constructor for the QuestionViewHolder.
             *
             * @param itemView The View for a single question item.
             */
            public QuestionViewHolder(@NonNull View itemView) {
                super(itemView);
                // Initialize TextViews and ImageView from the item layout
                tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
                tvOptionA = itemView.findViewById(R.id.tvOptionA);
                tvOptionB = itemView.findViewById(R.id.tvOptionB);
                tvOptionC = itemView.findViewById(R.id.tvOptionC);
                tvOptionD = itemView.findViewById(R.id.tvOptionD);
                tvCorrectAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}
