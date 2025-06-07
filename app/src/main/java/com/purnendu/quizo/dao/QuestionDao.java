package com.purnendu.quizo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.purnendu.quizo.models.Question;

import java.util.List;

/**
 * This interface handles the data access object (DAO) for question-related operations,
 * focusing on managing {@link com.purnendu.quizo.models.Question} entities.
 * It utilizes Room Persistence Library annotations including {@link androidx.room.Dao},
 * {@link androidx.room.Insert}, {@link androidx.room.Delete}, {@link androidx.room.Query},
 * and specifies conflict resolution strategies via {@link androidx.room.OnConflictStrategy}.
 * It also interacts with standard Java collections like {@link java.util.List}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
@Dao
public interface QuestionDao {

    /**
     * Inserts a new {@link com.purnendu.quizo.models.Question} into the database.
     * If a conflict occurs (e.g., a question with the same primary key already exists),
     * the existing question will be replaced with the new one.
     *
     * @param question The {@link com.purnendu.quizo.models.Question} object to insert.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertQuestion(Question question);

    /**
     * Retrieves a random set of {@link com.purnendu.quizo.models.Question} objects for a specific topic.
     * The questions are ordered randomly using `ORDER BY RANDOM()`.
     *
     * @param topic The topic of the questions to retrieve.
     * @return A {@link java.util.List} of randomly ordered {@link com.purnendu.quizo.models.Question} objects.
     */
    @Query("SELECT * FROM questions WHERE topic = :topic ORDER BY RANDOM()")
    List<Question> getRandomQuestionsByTopic(String topic);

    /**
     * Retrieves all {@link com.purnendu.quizo.models.Question} objects for a specific topic.
     * The order of questions is not guaranteed.
     *
     * @param topic The topic of the questions to retrieve.
     * @return A {@link java.util.List} of {@link com.purnendu.quizo.models.Question} objects for the given topic.
     */
    @Query("SELECT * FROM questions WHERE topic = :topic")
    List<Question> getQuestionsByTopic(String topic);

    /**
     * Retrieves the count of questions for a specific topic.
     *
     * @param topic The topic for which to count questions.
     * @return The number of questions associated with the given topic.
     */
    @Query("SELECT COUNT(*) FROM questions WHERE topic = :topic")
    int getQuestionCountByTopic(String topic);

    /**
     * Retrieves a list of all distinct topics present in the 'questions' table.
     *
     * @return A {@link java.util.List} of unique topic strings.
     */
    @Query("SELECT DISTINCT topic FROM questions")
    List<String> getDistinctTopics();

    /**
     * Deletes a {@link com.purnendu.quizo.models.Question} from the database.
     * The question is identified by its primary key.
     *
     * @param question The {@link com.purnendu.quizo.models.Question} object to delete.
     * @return The number of rows deleted.
     */
    @Delete
    int deleteQuestion(Question question);
}
