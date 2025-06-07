package com.purnendu.quizo.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * Represents an administrator entity in the Quizo application, used for authentication and management.
 * This class is designed as a Room {@link androidx.room.Entity} for database persistence,
 * with fields for username, email, and password. The email serves as the {@link androidx.room.PrimaryKey}.
 * It also implements {@link android.os.Parcelable} to allow efficient data transfer between Android components.
 * <p>
 * Key fields include:
 * <ul>
 * <li>{@code username}: The display name of the administrator.</li>
 * <li>{@code email}: The unique email address, serving as the primary identifier.</li>
 * <li>{@code password}: The hashed password for authentication.</li>
 * </ul>
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
@Entity(tableName = "admin")
public class Admin implements Parcelable {

    /**
     * A {@link android.os.Parcelable.Creator} that implements the Parcelable interface,
     * allowing this class to be parceled and unparceled.
     * This is required for passing {@link Admin} objects between Android components.
     */
    public static final Creator<Admin> CREATOR = new Creator<>() {
        /**
         * Creates a new {@link Admin} instance from a {@link android.os.Parcel}.
         * @param in The Parcel to read the object's data from.
         * @return A new Admin instance.
         */
        @Override
        public Admin createFromParcel(Parcel in) {
            return new Admin(in);
        }

        /**
         * Creates a new array of {@link Admin} objects.
         * @param size The size of the array to create.
         * @return An array of Admin objects.
         */
        @Override
        public Admin[] newArray(int size) {
            return new Admin[size];
        }
    };

    /**
     * The username of the administrator. Stored as a column in the database.
     */
    @ColumnInfo(name = "username")
    private final String username;

    /**
     * The email of the administrator. This is the primary key and must be unique.
     * Stored as a column in the database.
     */
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "email")
    private final String email;

    /**
     * The password of the administrator. Stored as a column in the database.
     */
    @ColumnInfo(name = "password")
    private String password;

    /**
     * Constructs a new {@link Admin} object with the specified username, email, and password.
     *
     * @param username The username for the admin.
     * @param email    The unique email address for the admin.
     * @param password The password for the admin.
     */
    public Admin(String username, @NonNull String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /**
     * Constructs an {@link Admin} object from a {@link android.os.Parcel}.
     * This constructor is used by the {@link #CREATOR} to unparcel the object.
     *
     * @param in The Parcel containing the Admin object's data.
     */
    protected Admin(Parcel in) {
        username = in.readString();
        email = Objects.requireNonNull(in.readString());
        password = in.readString();
    }

    /**
     * Returns the username of the administrator.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the email address of the administrator.
     *
     * @return The email address.
     */
    @NonNull
    public String getEmail() {
        return email;
    }

    /**
     * Returns the password of the administrator.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets a new password for the administrator.
     *
     * @param password The new password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Writes the object's data to a {@link android.os.Parcel}.
     * This method is part of the {@link android.os.Parcelable} interface.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(password);
    }

    /**
     * Describes the kinds of special objects contained in this Parcelable's marshalled representation.
     * This method is part of the {@link android.os.Parcelable} interface.
     *
     * @return A bitmask indicating the set of special object types marshalled by this Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }
}
