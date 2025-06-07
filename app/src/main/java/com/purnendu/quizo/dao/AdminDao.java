package com.purnendu.quizo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.purnendu.quizo.models.Admin;

import java.util.List;

/**
 * This interface handles the data access object (DAO) for admin-related operations,
 * specifically for managing {@link com.purnendu.quizo.models.Admin} entities.
 * It leverages Room Persistence Library annotations such as {@link androidx.room.Dao},
 * {@link androidx.room.Insert}, {@link androidx.room.Update}, {@link androidx.room.Query},
 * and uses standard Java collections like {@link java.util.List}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
@Dao
public interface AdminDao {

    /**
     * Inserts a new {@link com.purnendu.quizo.models.Admin} into the database.
     * If an admin with the same primary key (email) already exists, a conflict strategy
     * (e.g., REPLACE, IGNORE) should be defined in the annotation if needed,
     * otherwise, a {@link android.database.sqlite.SQLiteConstraintException} will be thrown.
     *
     * @param admin The {@link com.purnendu.quizo.models.Admin} object to insert.
     */
    @Insert()
    void insertAdmin(Admin admin);

    /**
     * Updates an existing {@link com.purnendu.quizo.models.Admin} record in the database.
     * The update is based on the primary key (email) of the provided {@link com.purnendu.quizo.models.Admin} object.
     *
     * @param admin The {@link com.purnendu.quizo.models.Admin} object to update.
     */
    @Update
    void updateAdmin(Admin admin);

    /**
     * Retrieves all {@link com.purnendu.quizo.models.Admin} records from the 'admin' table.
     * This query returns a list of all administrators currently stored in the database.
     *
     * @return A {@link java.util.List} of {@link com.purnendu.quizo.models.Admin} objects.
     */
    @Query("SELECT * FROM admin")
    List<Admin> observeAllAdmins();
}
