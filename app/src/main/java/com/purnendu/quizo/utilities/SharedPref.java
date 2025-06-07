package com.purnendu.quizo.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.purnendu.quizo.models.Admin;
import com.purnendu.quizo.models.User;

/**
 * A singleton utility class for managing data persistence using Android's {@link android.content.SharedPreferences}.
 * This class provides methods to store and retrieve {@link com.purnendu.quizo.models.User} and
 * {@link com.purnendu.quizo.models.Admin} objects as JSON strings, as well as clear stored data.
 * It uses {@link com.google.gson.Gson} for object serialization and deserialization.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
public class SharedPref {

    /**
     * The name for the SharedPreferences file used to store user-related data.
     */
    private static final String sharedPreferencesNameUser =
            "com.purnendu.quizo.data.accesstoken.user";

    /**
     * The name for the SharedPreferences file used to store admin-related data.
     */
    private static final String sharedPreferencesNameAdmin =
            "com.purnendu.quizo.data.accesstoken.admin";

    /**
     * The singleton instance of the {@link SharedPref} class.
     */
    private static SharedPref instance = null;

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private SharedPref() {
    }

    /**
     * Returns the singleton instance of the {@link SharedPref} class.
     * If the instance does not exist, it is created.
     *
     * @return The singleton instance of {@link SharedPref}.
     */
    public static SharedPref getInstance() {
        if (instance == null) {
            instance = new SharedPref();
        }
        return instance;
    }

    /**
     * Stores a {@link com.purnendu.quizo.models.User} object in SharedPreferences.
     * The object is converted to a JSON string using Gson before saving.
     *
     * @param context The application context.
     * @param user    The {@link com.purnendu.quizo.models.User} object to store.
     */
    public void setUser(Context context, User user) {
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesNameUser,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.USER, new Gson().toJson(user));
        editor.apply();
    }

    /**
     * Retrieves a {@link com.purnendu.quizo.models.User} object from SharedPreferences.
     * The JSON string is converted back to a {@link com.purnendu.quizo.models.User} object using Gson.
     *
     * @param context The application context.
     * @return The retrieved {@link com.purnendu.quizo.models.User} object, or {@code null} if not found or parsing fails.
     */
    public User getUser(Context context) {
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesNameUser,
                Context.MODE_PRIVATE);
        return new Gson().fromJson(pref.getString(Constants.USER, ""), User.class);
    }

    /**
     * Stores an {@link com.purnendu.quizo.models.Admin} object in SharedPreferences.
     * The object is converted to a JSON string using Gson before saving.
     *
     * @param context The application context.
     * @param admin   The {@link com.purnendu.quizo.models.Admin} object to store.
     */
    public void setAdmin(Context context, Admin admin) {
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesNameAdmin,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.ADMIN, new Gson().toJson(admin));
        editor.apply();
    }

    /**
     * Retrieves an {@link com.purnendu.quizo.models.Admin} object from SharedPreferences.
     * The JSON string is converted back to an {@link com.purnendu.quizo.models.Admin} object using Gson.
     *
     * @param context The application context.
     * @return The retrieved {@link com.purnendu.quizo.models.Admin} object, or {@code null} if not found or parsing fails.
     */
    public Admin getAdmin(Context context) {
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesNameAdmin,
                Context.MODE_PRIVATE);
        return new Gson().fromJson(pref.getString(Constants.ADMIN, ""), Admin.class);
    }

    /**
     * Clears all user-related data from the corresponding SharedPreferences file.
     *
     * @param context The application context.
     */
    public void clearSharedPrefUser(@NonNull Context context) {
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesNameUser,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Clears all admin-related data from the corresponding SharedPreferences file.
     *
     * @param context The application context.
     */
    public void clearSharedPrefAdmin(@NonNull Context context) {
        SharedPreferences pref = context.getSharedPreferences(sharedPreferencesNameAdmin,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
