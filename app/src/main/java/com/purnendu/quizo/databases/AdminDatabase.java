package com.purnendu.quizo.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.purnendu.quizo.dao.AdminDao;
import com.purnendu.quizo.models.Admin;

/**
 * Abstract Room database class for managing administrator data in the Quizo application.
 * This database contains the {@link com.purnendu.quizo.models.Admin} entity.
 * <p>
 * Configuration:
 * <ul>
 * <li>{@code entities}: Specifies the {@link com.purnendu.quizo.models.Admin} class as an entity.</li>
 * <li>{@code version}: Sets the database version to 1.</li>
 * <li>{@code exportSchema}: Set to {@code false} as schema export is not required for this application.</li>
 * </ul>
 * This class provides an abstract method to access the {@link com.purnendu.quizo.dao.AdminDao}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
@Database(
        entities = {Admin.class},
        version = 1,
        exportSchema = false
)
// Class for Admin Database
public abstract class AdminDatabase extends RoomDatabase {
    /**
     * Provides the Data Access Object (DAO) for {@link com.purnendu.quizo.models.Admin} entities.
     *
     * @return An instance of {@link com.purnendu.quizo.dao.AdminDao}.
     */
    public abstract AdminDao adminDao();
}
