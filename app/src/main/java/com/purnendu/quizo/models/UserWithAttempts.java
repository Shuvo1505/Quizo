package com.purnendu.quizo.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * A data class that represents a {@link com.purnendu.quizo.models.User} along with their associated
 * quiz {@link com.purnendu.quizo.models.Attempt}. This class is designed to be used with Room's
 * relationship capabilities, where a user can have multiple attempts, but this specific class
 * represents a single user and a single attempt linked by email.
 * <p>
 * It uses {@link androidx.room.Embedded} to directly embed the {@link User} object
 * and {@link androidx.room.Relation} to define the one-to-one relationship with an {@link Attempt}
 * based on the 'email' column.
 * This class also implements {@link android.os.Parcelable} for efficient data transfer between Android components.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
public class UserWithAttempts implements Parcelable {

    /**
     * A {@link android.os.Parcelable.Creator} that implements the Parcelable interface,
     * allowing this class to be parceled and unparceled.
     * This is required for passing {@link UserWithAttempts} objects between Android components.
     */
    public static final Creator<UserWithAttempts> CREATOR = new Creator<>() {
        /**
         * Creates a new {@link UserWithAttempts} instance from a {@link android.os.Parcel}.
         * Handles different Android API levels for Parcelable reading.
         * @param in The Parcel to read the object's data from.
         * @return A new UserWithAttempts instance.
         */
        @Override
        public UserWithAttempts createFromParcel(Parcel in) {
            return new UserWithAttempts(in);
        }

        /**
         * Creates a new array of {@link UserWithAttempts} objects.
         * @param size The size of the array to create.
         * @return An array of UserWithAttempts objects.
         */
        @Override
        public UserWithAttempts[] newArray(int size) {
            return new UserWithAttempts[size];
        }
    };

    /**
     * The embedded {@link com.purnendu.quizo.models.User} object.
     * Room will embed all fields of the User object directly into the parent entity's table.
     */
    @Embedded
    private final User user;

    /**
     * The related {@link com.purnendu.quizo.models.Attempt} object.
     * This defines a relationship where the 'email' column in the parent (User)
     * is linked to the 'email' column in the child (Attempt).
     */
    @Relation(
            parentColumn = "email",
            entityColumn = "email",
            entity = Attempt.class
    )
    private final Attempt attempt;

    /**
     * Constructs a {@link UserWithAttempts} object from a {@link android.os.Parcel}.
     * This constructor is used by the {@link #CREATOR} to unparcel the object.
     * It handles API level differences for reading Parcelable objects.
     *
     * @param in The Parcel containing the UserWithAttempts object's data.
     */
    protected UserWithAttempts(Parcel in) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            user = in.readParcelable(User.class.getClassLoader(), User.class);
            attempt = in.readParcelable(Attempt.class.getClassLoader(), Attempt.class);
        } else {
            user = in.readParcelable(User.class.getClassLoader());
            attempt = in.readParcelable(Attempt.class.getClassLoader());
        }
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
        dest.writeParcelable(user, flags);
        dest.writeParcelable(attempt, flags);
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

    /**
     * Returns the embedded {@link com.purnendu.quizo.models.User} object.
     *
     * @return The User object.
     */
    public User getUser() {
        return user;
    }

}
