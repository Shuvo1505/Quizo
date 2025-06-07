package com.purnendu.quizo.utilities;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * A utility class for managing the soft keyboard (on-screen keyboard) in Android applications.
 * It provides a static method to programmatically hide the keyboard, which is useful for
 * improving user experience after form submissions or when input is no longer needed.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for Keyboard
public class Keyboard {

    /**
     * Hides the soft keyboard from the screen.
     * This method retrieves the {@link android.view.inputmethod.InputMethodManager} service
     * and instructs it to hide the keyboard associated with the given view's window token.
     *
     * @param context The {@link android.content.Context} of the application or activity.
     * @param view    The {@link android.view.View} that currently has focus or from which
     *                the window token can be obtained.
     */
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
