package com.purnendu.quizo.activities.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;

import com.purnendu.quizo.R;
import com.purnendu.quizo.activities.user.access.LoginActivity;

/**
 * The `SplashActivity` class serves as the initial entry point for the application,
 * displaying a splash screen for a short duration. This activity ensures a locked
 * screen orientation and hides system UI elements for a clean, immersive user experience
 * during the splash period.
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created. This method performs the following actions:
     * <ul>
     * <li>Locks the screen orientation to portrait mode to prevent rotation issues.</li>
     * <li>Sets the content view to the `activity_splash` layout.</li>
     * <li>Hides system navigation bars to provide a full-screen, immersive display.</li>
     * <li>Starts a new thread to introduce a delay (2000 milliseconds) before
     * finishing the activity, effectively creating the splash screen effect.</li>
     * </ul>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_splash);

        //Hide navigation bar to view full screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }


        // Using a Handler to post a delayed action to the main thread
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            finish();
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }, 2000); // 2000 milliseconds = 2 seconds
    }
}
