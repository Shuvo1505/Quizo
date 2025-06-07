package com.purnendu.quizo.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a single quiz attempt by a user in the Quizo application.
 * This class is designed as a Room {@link androidx.room.Entity} for database persistence,
 * storing details about when the attempt was made, the subject, performance, and points.
 * The {@code createdTime} serves as the {@link androidx.room.PrimaryKey}.
 * It also implements {@link android.os.Parcelable} for efficient data transfer between Android components.
 * <p>
 * Key fields include:
 * <ul>
 * <li>{@code createdTime}: The timestamp (in milliseconds) when the quiz attempt was recorded, serving as the primary key.</li>
 * <li>{@code subject}: The topic of the quiz (e.g., "Math", "Geography").</li>
 * <li>{@code correct}: The number of questions answered correctly.</li>
 * <li>{@code incorrect}: The number of questions answered incorrectly.</li>
 * <li>{@code earned}: The points earned from this specific quiz attempt.</li>
 * <li>{@code email}: The email of the user who made this attempt.</li>
 * <li>{@code overallPoints}: The user's total accumulated points after this attempt.</li>
 * </ul>
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
@Entity(tableName = "attempt")
public class Attempt implements Parcelable {

    /**
     * A {@link android.os.Parcelable.Creator} that implements the Parcelable interface,
     * allowing this class to be parceled and unparceled.
     * This is required for passing {@link Attempt} objects between Android components.
     */
    public static final Creator<Attempt> CREATOR = new Creator<>() {
        /**
         * Creates a new {@link Attempt} instance from a {@link android.os.Parcel}.
         * @param in The Parcel to read the object's data from.
         * @return A new Attempt instance.
         */
        @Override
        public Attempt createFromParcel(Parcel in) {
            return new Attempt(in);
        }

        /**
         * Creates a new array of {@link Attempt} objects.
         * @param size The size of the array to create.
         * @return An array of Attempt objects.
         */
        @Override
        public Attempt[] newArray(int size) {
            return new Attempt[size];
        }
    };

    /**
     * The timestamp (in milliseconds) when the quiz attempt was created.
     * This serves as the primary key for the attempt table.
     */
    @PrimaryKey()
    @ColumnInfo(name = "createdTimeAttempt")
    private final long createdTime;

    /**
     * The subject of the quiz for this attempt (e.g., "Computer", "Geography").
     */
    @ColumnInfo(name = "subject")
    private final String subject;

    /**
     * The number of questions answered correctly in this attempt.
     */
    @ColumnInfo(name = "correct")
    private final int correct;

    /**
     * The number of questions answered incorrectly in this attempt.
     */
    @ColumnInfo(name = "incorrect")
    private final int incorrect;

    /**
     * The points earned from this specific quiz attempt.
     */
    @ColumnInfo(name = "earned")
    private final long earned;

    /**
     * The email of the user who made this attempt.
     */
    @ColumnInfo(name = "email")
    private final String email;

    /**
     * The user's overall accumulated points after this attempt.
     */
    @ColumnInfo(name = "overallPoints")
    private long overallPoints;

    /**
     * Constructs a new {@link Attempt} object with the specified details.
     *
     * @param createdTime The timestamp when the attempt was created (in milliseconds).
     * @param subject     The subject of the quiz.
     * @param correct     The number of correct answers.
     * @param incorrect   The number of incorrect answers.
     * @param earned      The points earned from this attempt.
     * @param email       The email of the user who made the attempt.
     */
    public Attempt(long createdTime, String subject, int correct, int incorrect,
                   long earned, String email) {
        this.createdTime = createdTime;
        this.subject = subject;
        this.correct = correct;
        this.incorrect = incorrect;
        this.earned = earned;
        this.email = email;
    }

    /**
     * Constructs an {@link Attempt} object from a {@link android.os.Parcel}.
     * This constructor is used by the {@link #CREATOR} to unparcel the object.
     *
     * @param in The Parcel containing the Attempt object's data.
     */
    protected Attempt(Parcel in) {
        createdTime = in.readLong();
        subject = in.readString();
        correct = in.readInt();
        incorrect = in.readInt();
        earned = in.readLong();
        email = in.readString();
        overallPoints = in.readLong();
    }

    /**
     * Returns the timestamp when the quiz attempt was created.
     *
     * @return The creation timestamp in milliseconds.
     */
    public long getCreatedTime() {
        return createdTime;
    }

    /**
     * Returns the subject of the quiz attempt.
     *
     * @return The quiz subject.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Returns the number of correct answers in this attempt.
     *
     * @return The count of correct answers.
     */
    public int getCorrect() {
        return correct;
    }

    /**
     * Returns the number of incorrect answers in this attempt.
     *
     * @return The count of incorrect answers.
     */
    public int getIncorrect() {
        return incorrect;
    }

    /**
     * Returns the points earned from this specific quiz attempt.
     *
     * @return The earned points.
     */
    public long getEarned() {
        return earned;
    }

    /**
     * Returns the email of the user who made this attempt.
     *
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the user's overall accumulated points after this attempt.
     *
     * @return The overall points.
     */
    public long getOverallPoints() {
        return overallPoints;
    }

    /**
     * Sets the user's overall accumulated points after this attempt.
     *
     * @param overallPoints The new overall points value.
     */
    public void setOverallPoints(long overallPoints) {
        this.overallPoints = overallPoints;
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
        dest.writeLong(createdTime);
        dest.writeString(subject);
        dest.writeInt(correct);
        dest.writeInt(incorrect);
        dest.writeLong(earned);
        dest.writeString(email);
        dest.writeLong(overallPoints);
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
