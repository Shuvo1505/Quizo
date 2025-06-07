package com.purnendu.quizo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.purnendu.quizo.models.Attempt;
import com.purnendu.quizo.models.User;

import java.util.List;

/**
 * This interface handles the data access object (DAO) for user-related operations,
 * including managing {@link com.purnendu.quizo.models.User} and {@link com.purnendu.quizo.models.Attempt} data.
 * It leverages Room Persistence Library annotations such as {@link androidx.room.Dao},
 * {@link androidx.room.Insert}, {@link androidx.room.Update}, {@link androidx.room.Query},
 * {@link androidx.room.Transaction}, and uses standard Java collections like {@link java.util.List}.
 * It also specifies conflict resolution strategies via {@link androidx.room.OnConflictStrategy}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
@Dao
public interface UserDao {

    /**
     * Inserts a new {@link com.purnendu.quizo.models.User} into the database.
     * If a user with the same primary key (email) already exists, a
     * {@link android.database.sqlite.SQLiteConstraintException} will be thrown
     * unless an {@link OnConflictStrategy} is specified.
     *
     * @param user The {@link com.purnendu.quizo.models.User} object to insert.
     */
    @Insert()
    void insertUser(User user);

    /**
     * Inserts a new {@link com.purnendu.quizo.models.Attempt} record into the database.
     * If an attempt with the same primary key (createdTimeAttempt) already exists,
     * the existing attempt will be replaced with the new one.
     *
     * @param attempt The {@link com.purnendu.quizo.models.Attempt} object to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAttempt(Attempt attempt);

    /**
     * Updates an existing {@link com.purnendu.quizo.models.User} record in the database.
     * The update is based on the primary key (email) of the provided {@link com.purnendu.quizo.models.User} object.
     *
     * @param user The {@link com.purnendu.quizo.models.User} object to update.
     */
    @Update
    void updateUser(User user);

    /**
     * Retrieves all {@link com.purnendu.quizo.models.User} records from the 'user' table.
     *
     * @return A {@link java.util.List} of all {@link com.purnendu.quizo.models.User} objects.
     */
    @Query("SELECT * FROM user")
    List<User> observeAllUser();

    /**
     * Retrieves all {@link com.purnendu.quizo.models.Attempt} records associated with a specific user email.
     * This query uses a {@link androidx.room.Transaction} to ensure atomicity if multiple database
     * operations were involved (though for a single SELECT, it's primarily for clarity).
     *
     * @param email The email address of the user whose attempts are to be retrieved.
     * @return A {@link java.util.List} of {@link com.purnendu.quizo.models.Attempt} objects.
     */
    @Transaction
    @Query("SELECT DISTINCT * FROM attempt WHERE email = :email")
    List<Attempt> getUserAndAttemptsWithSameEmail(String email);

    /**
     * Calculates the sum of 'earned' points from all {@link com.purnendu.quizo.models.Attempt} records
     * for a specific user email.
     * This query uses a {@link androidx.room.Transaction} to ensure atomicity.
     *
     * @param email The email address of the user whose total points are to be calculated.
     * @return The sum of earned points for the specified user, or 0 if no attempts are found.
     */
    @Transaction
    @Query("SELECT SUM(earned) FROM attempt WHERE email = :email")
    long getOverAllPoints(String email);

}
