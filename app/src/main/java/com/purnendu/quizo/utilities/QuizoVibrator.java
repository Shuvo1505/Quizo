package com.purnendu.quizo.utilities;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

import androidx.core.content.ContextCompat;

/**
 * A utility class for providing haptic feedback (vibration) on the device within the Quizo application.
 * This class ensures that vibration is handled safely and appropriately across different Android versions,
 * using modern APIs where available.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for QuizoVibrator
public class QuizoVibrator {
    /**
     * Vibrates the phone for a short, predefined duration (90 milliseconds) with default amplitude.
     * This method checks for the necessary permissions and API levels before triggering vibration.
     * It uses {@link android.os.VibratorManager} for API 31+ and falls back to {@link android.os.Vibrator}
     * for older versions.
     *
     * @param context The {@link android.content.Context} of the calling activity or application,
     *                used to get the Vibrator service.
     */
    public static void vibratePhone(Context context) {
        if (context == null) {
            return; // Avoid NullPointerException if context is null
        }

        Vibrator vibrator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For API 31 (Android 12) and above, use VibratorManager
            VibratorManager vibratorManager = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        } else {
            // For older APIs, use ContextCompat.getSystemService for Vibrator
            vibrator = ContextCompat.getSystemService(context, Vibrator.class);
        }


        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // For API 26 (Android 8.0) and above, use VibrationEffect
                vibrator.vibrate(VibrationEffect.createOneShot(90,
                        VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // For APIs below 26, use the deprecated vibrate method
                vibrator.vibrate(90);
            }
        }
    }
}
