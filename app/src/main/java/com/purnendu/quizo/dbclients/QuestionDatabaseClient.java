package com.purnendu.quizo.dbclients;

import android.content.Context;

import androidx.room.Room;

import com.purnendu.quizo.databases.QuestionDatabase;

/**
 * A singleton client class for accessing the {@link com.purnendu.quizo.databases.QuestionDatabase}
 * instance throughout the Quizo application.
 * This ensures that only one instance of the database is created and managed,
 * providing a consistent access point for question-related data operations.
 * <p>
 * The database is built using Room's {@link androidx.room.Room#databaseBuilder(Context, Class, String)}
 * and configured with destructive migration for simplicity during development.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for QuestionDatabaseClient
public class QuestionDatabaseClient {

    /**
     * The name of the Room database file for questions.
     */
    private static final String DB_NAME = "com_quizo_question_db";

    /**
     * The singleton instance of the {@link com.purnendu.quizo.databases.QuestionDatabase}.
     */
    private static QuestionDatabase instance;

    /**
     * Returns the synchronized singleton instance of the {@link com.purnendu.quizo.databases.QuestionDatabase}.
     * If the instance does not exist, it is created using Room's database builder.
     * {@code fallbackToDestructiveMigration(true)} is used, meaning that if the database schema
     * changes, the existing database will be recreated, leading to data loss.
     *
     * @param context The application context, used to build the database.
     * @return The singleton instance of {@link com.purnendu.quizo.databases.QuestionDatabase}.
     */
    public static synchronized QuestionDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(), QuestionDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration(true) // Allows Room to recreate database if schema changes
                    .build();
        }
        return instance;
    }
}
