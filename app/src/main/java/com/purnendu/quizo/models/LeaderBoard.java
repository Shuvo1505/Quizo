package com.purnendu.quizo.models;

/**
 * `LeaderBoard` is a data class that represents a single entry in the quiz leaderboard.
 * It encapsulates the essential information for a leaderboard participant, including
 * their total accumulated points, their display name, and their unique email address.
 * <p>
 * This class is designed to be compatible with Firebase Firestore's automatic data mapping,
 * hence the inclusion of a no-argument constructor.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
public class LeaderBoard {

    /**
     * The total points accumulated by the user in the quiz.
     */
    private long totalPoints;

    /**
     * The display name of the user.
     */
    private String name;

    /**
     * The email address of the user, which serves as a unique identifier.
     */
    private String email;

    /**
     * Required no-argument constructor for Firebase Firestore automatic data mapping.
     * This constructor is necessary for Firestore to convert a document snapshot into a `LeaderBoard` object.
     */
    public LeaderBoard() {
        // Default constructor required for calls to DataSnapshot.getValue(LeaderboardEntry.class)
    }

    /**
     * Constructs a new `LeaderBoard` instance with the specified name, email, and total points.
     *
     * @param name        The display name of the user.
     * @param email       The email address of the user.
     * @param totalPoints The total points accumulated by the user.
     */
    public LeaderBoard(String name, String email, long totalPoints) {
        this.name = name;
        this.email = email;
        this.totalPoints = totalPoints;
    }

    /**
     * Retrieves the display name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of the user.
     *
     * @param name The new display name for the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the email address of the user.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The new email address for the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the total points accumulated by the user.
     *
     * @return The user's total points.
     */
    public long getTotalPoints() {
        return totalPoints;
    }
}
