package com.purnendu.quizo.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * Represents a regular user entity in the Quizo application.
 * This class is designed as a Room {@link androidx.room.Entity} for database persistence,
 * with fields for username, email, and password. The email serves as the {@link androidx.room.PrimaryKey}.
 * It also implements {@link android.os.Parcelable} to allow efficient data transfer between Android components.
 * <p>
 * Key fields include:
 * <ul>
 * <li>{@code username}: The display name of the user.</li>
 * <li>{@code email}: The unique email address, serving as the primary identifier.</li>
 * <li>{@code password}: The hashed password for authentication.</li>
 * </ul>
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
@Entity(tableName = "user")
public class User implements Parcelable {

    /**
     * A {@link android.os.Parcelable.Creator} that implements the Parcelable interface,
     * allowing this class to be parceled and unparceled.
     * This is required for passing {@link User} objects between Android components.
     */
    public static final Creator<User> CREATOR = new Creator<>() {
        /**
         * Creates a new {@link User} instance from a {@link android.os.Parcel}.
         * @param in The Parcel to read the object's data from.
         * @return A new User instance.
         */
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        /**
         * Creates a new array of {@link User} objects.
         * @param size The size of the array to create.
         * @return An array of User objects.
         */
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * The username of the user. Stored as a column in the database.
     */
    @ColumnInfo(name = "username")
    private final String username;

    /**
     * The email of the user. This is the primary key and must be unique.
     * Stored as a column in the database.
     */
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "email")
    private final String email;

    /**
     * The password of the user. Stored as a column in the database.
     */
    @ColumnInfo(name = "password")
    private String password;

    /**
     * Constructs a new {@link User} object with the specified username, email, and password.
     *
     * @param username The username for the user.
     * @param email    The unique email address for the user.
     * @param password The password for the user.
     */
    public User(String username, @NonNull String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /**
     * Constructs a {@link User} object from a {@link android.os.Parcel}.
     * This constructor is used by the {@link #CREATOR} to unparcel the object.
     *
     * @param in The Parcel containing the User object's data.
     */
    protected User(Parcel in) {
        username = in.readString();
        email = Objects.requireNonNull(in.readString());
        password = in.readString();
    }

    /**
     * Returns the username of the user.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the email address of the user.
     *
     * @return The email address.
     */
    @NonNull
    public String getEmail() {
        return email;
    }

    /**
     * Returns the password of the user.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets a new password for the user.
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
