package com.purnendu.quizo.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.purnendu.quizo.dao.UserDao;
import com.purnendu.quizo.models.Attempt;
import com.purnendu.quizo.models.User;

/**
 * Abstract Room database class for managing user and quiz attempt data in the Quizo application.
 * This database contains both {@link com.purnendu.quizo.models.User} and {@link com.purnendu.quizo.models.Attempt} entities.
 * <p>
 * Configuration:
 * <ul>
 * <li>{@code entities}: Specifies the {@link com.purnendu.quizo.models.User} and {@link com.purnendu.quizo.models.Attempt} classes as entities.</li>
 * <li>{@code version}: Sets the database version to 2.</li>
 * <li>{@code exportSchema}: Set to {@code false} as schema export is not required for this application.</li>
 * </ul>
 * This class provides an abstract method to access the {@link com.purnendu.quizo.dao.UserDao}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
@Database(
        entities = {User.class, Attempt.class},
        version = 2,
        exportSchema = false
)

//Class for UserDatabase
public abstract class UserDatabase extends RoomDatabase {

    /**
     * Provides the Data Access Object (DAO) for {@link com.purnendu.quizo.models.User} and
     * {@link com.purnendu.quizo.models.Attempt} entities.
     *
     * @return An instance of {@link com.purnendu.quizo.dao.UserDao}.
     */
    public abstract UserDao userDao();
}
