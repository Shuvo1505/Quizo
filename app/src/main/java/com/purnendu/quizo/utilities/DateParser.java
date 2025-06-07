package com.purnendu.quizo.utilities;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A utility class for parsing and formatting date and time values within the Quizo application.
 * It provides a static method to convert a timestamp into a human-readable date string
 * based on a predefined format.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for DateParser
public class DateParser {

    /**
     * Formats a given timestamp (in milliseconds) into a readable date and time string.
     * The format used is defined in {@link com.purnendu.quizo.utilities.Constants#DATE_FORMAT}.
     * <p>
     * An {@code @SuppressLint("WeekBasedYear")} annotation is used to suppress a lint warning
     * related to week-based year formatting, as the intended format is not week-based.
     *
     * @param time The timestamp in milliseconds to be formatted.
     * @return A formatted date and time string (e.g., "01 Jan 12:30 PM").
     */
    public static String formatDate(long time) {
        @SuppressLint("WeekBasedYear") SimpleDateFormat formatter = new SimpleDateFormat
                (Constants.DATE_FORMAT, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return formatter.format(calendar.getTime());
    }
}
