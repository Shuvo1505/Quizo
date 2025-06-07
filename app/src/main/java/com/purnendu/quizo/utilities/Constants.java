package com.purnendu.quizo.utilities;

import java.text.DecimalFormat;

/**
 * A utility class containing constant values used throughout the Quizo application.
 * These constants include keys for SharedPreferences, intent extras, and predefined
 * values such as points for correct/incorrect answers and date formatting patterns.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for Constants
public class Constants {

    /**
     * SharedPreferences key or Intent extra key for user-related data.
     */
    public static final String USER = "com_quizo_user";

    /**
     * SharedPreferences key or Intent extra key for admin-related data.
     */
    public static final String ADMIN = "com_quizo_admin";

    /**
     * Intent extra key for the quiz subject.
     */
    public static final String SUBJECT = "com_quizo_subject";

    /**
     * Intent extra key for the number of correct answers.
     */
    public static final String CORRECT = "com_quizo_correct";

    /**
     * Intent extra key for the number of incorrect answers.
     */
    public static final String INCORRECT = "com_quizo_incorrect";

    /**
     * Points awarded for each correct answer.
     */
    public static final int CORRECT_POINT = 5;

    /**
     * Intent extra key for the total number of questions attempted in a quiz.
     */
    public static final String TOTAL_QUESTIONS_ATTEMPTED =
            "com_quizo_total_questions_attempted";

    /**
     * Points deducted for each incorrect answer.
     */
    public static final int INCORRECT_POINT = 2;

    /**
     * Date format string used for displaying timestamps (e.g., "dd MMM hh:mm a").
     */
    public static final String DATE_FORMAT = "dd MMM hh:mm a";

    /**
     * Formats a large integer score into a more readable string using K (thousands) or M (millions) suffixes.
     * Examples: 65000 -> 65K, 20200 -> 20.2K, 65000000 -> 65M, 1234 -> 1234
     *
     * @param score The integer score to format.
     * @return The formatted string representation of the score.
     */
    public static String formatScore(long score) { // Changed parameter to long
        if (score < 1000) {
            return String.valueOf(score);
        }

        final long K = 1000L;
        final long M = 1000000L;
        final long B = 1000000000L;
        final long T = 1000000000000L; // For trillions

        DecimalFormat formatter = new DecimalFormat("#.#"); // For one decimal place

        if (score >= T) { // Handle trillions
            return formatter.format((double) score / T) + "T";
        } else if (score >= B) { // Handle billions
            return formatter.format((double) score / B) + "B";
        } else if (score >= M) { // Handle millions
            return formatter.format((double) score / M) + "M";
        } else { // Handle thousands
            return formatter.format((double) score / K) + "K";
        }
    }
}
