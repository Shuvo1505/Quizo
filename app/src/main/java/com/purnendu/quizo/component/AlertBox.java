package com.purnendu.quizo.component;

import android.app.Activity;
import android.app.AlertDialog;

/**
 * This utility class provides a standardized way to display custom alert dialogs within the Quizo application.
 * It simplifies the creation and presentation of {@link android.app.AlertDialog} instances,
 * ensuring consistent UI and behavior for user confirmations.
 * <p>
 * It requires an {@link android.app.Activity} context to properly display the dialog.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for Alertbox
public class AlertBox {
    /**
     * Displays a simple alert dialog with a title, message, and an "OK" button.
     * The dialog will only be shown if the provided activity is not null and not finishing.
     *
     * @param title    The title of the alert dialog.
     * @param message  The message content of the alert dialog.
     * @param activity The {@link android.app.Activity} context in which the dialog should be shown.
     *                 The dialog will not be shown if the activity is null or currently finishing.
     */
    public static void showDialog(String title, String message, Activity activity) {
        // Ensure the activity is valid and not in the process of finishing to prevent WindowManager$BadTokenException
        if (activity != null && !activity.isFinishing()) {
            new AlertDialog.Builder(activity) // Create a new AlertDialog.Builder with the provided activity context
                    .setTitle(title) // Set the title of the dialog
                    .setMessage(message) // Set the message content of the dialog
                    .setPositiveButton("OK", null) // Set a positive button with "OK" text and no specific click listener (it will just dismiss the dialog)
                    .show(); // Display the dialog
        }
    }
}
