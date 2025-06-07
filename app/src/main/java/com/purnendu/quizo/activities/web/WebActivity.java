package com.purnendu.quizo.activities.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.purnendu.quizo.R;
import com.purnendu.quizo.utilities.QuizoVibrator;

import java.util.Objects;

/**
 * This activity is designed to display web content within the Quizo application using a {@link android.webkit.WebView}.
 * It loads a specified URL (or a default error page if the URL is invalid or null) and configures
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * {@link android.webkit.WebSettings} to enable JavaScript and DOM storage.
 * The activity also implements basic navigation control within the WebView and handles the device's back button.
 * <p>
 * It utilizes Android UI components such as {@link android.webkit.WebView} and {@link android.widget.TextView}.
 * User experience is enhanced with haptic feedback provided by {@link com.purnendu.quizo.utilities.QuizoVibrator}.
 * Security considerations for WebView are crucial, especially with JavaScript enabled.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for WebActivity
public class WebActivity extends AppCompatActivity {

    // The WebView component that displays web pages
    private WebView webView;

    // Declaring progress bar
    private ProgressBar progressBar;

    /**
     * Called when the activity is first created. This is where you should do all of your
     * normal static set up: create views, bind data to lists, etc.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @SuppressLint("SetJavaScriptEnabled")
    // Suppresses lint warning about enabling JavaScript, which is intentional here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lock the screen orientation to prevent rotation issues
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_web); // Set the layout for this activity

        progressBar = findViewById(R.id.loadingIndicator);

        // Add a callback to handle the device's back button press.
        // If the WebView can go back in its history, it navigates back within the WebView.
        // Otherwise, it allows the default back button behavior (finishing the activity).
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // pop the screen
                finish();
            }
        });

        // Initialize the TextView for the web page heading
        TextView webHeading = findViewById(R.id.webHeading);

        // Get the Intent that started this activity
        Intent webIntent = getIntent();
        // Retrieve the URL to load from the Intent extras
        String pushLink = webIntent.getStringExtra("accessURL");
        // Retrieve and set the heading for the web page from Intent extras
        webHeading.setText(webIntent.getStringExtra("webHeading"));

        // Set OnClickListener for the close button (ImageView)
        findViewById(R.id.webClose).setOnClickListener(view -> {
            QuizoVibrator.vibratePhone(WebActivity.this); // Provide haptic feedback
            finish(); // Close the current activity
        });

        // Set OnClickListener for the back button (ImageView)
        findViewById(R.id.webBack).setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack(); // Navigate back within the WebView's history
            } else {
                // If WebView cannot go back, allow default back press behavior (finish activity)
                findViewById(R.id.webBack).setEnabled(false); // Disable this callback
            }
        });

        // Initialize the WebView component
        webView = findViewById(R.id.webHolder);

        // Get WebView settings to configure its behavior
        WebSettings webSettings = webView.getSettings();

        // Set a WebViewClient and progress bar to handle page navigation within the WebView itself
        progressBar.setMax(100);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        // Load the specified URL. If the URL is null, load a local error HTML file from assets.
        webView.loadUrl(Objects.requireNonNullElse(
                pushLink, "file:///android_asset/exceptions/false_url.html")
        );

        // Reset the progress bar
        progressBar.setProgress(0);
        // Enable DOM storage (e.g., localStorage, sessionStorage) for web pages
        webSettings.setDomStorageEnabled(true);
        // Enable built-in zoom controls (pinch-to-zoom)
        webSettings.setBuiltInZoomControls(true);
        // Hide the zoom controls UI (only allow pinch-to-zoom gesture)
        webSettings.setDisplayZoomControls(false);
        // Enable JavaScript execution within the WebView (necessary for many modern websites)
        webSettings.setJavaScriptEnabled(true);
    }
}
