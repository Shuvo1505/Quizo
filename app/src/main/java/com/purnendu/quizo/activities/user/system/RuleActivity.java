package com.purnendu.quizo.activities.user.system;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.purnendu.quizo.R;
import com.purnendu.quizo.adapters.RulesViewPagerAdapter;
import com.purnendu.quizo.utilities.QuizoVibrator;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

/**
 * This activity displays the rules of the Quizo application to users,
 * likely in a paginated format using a {@link androidx.viewpager.widget.ViewPager}.
 * It extends {@link androidx.appcompat.app.AppCompatActivity}
 * for standard Android activity lifecycle management.
 * The rules content is managed by a {@link com.purnendu.quizo.adapters.RulesViewPagerAdapter},
 * and navigation through the rules pages is visually indicated by
 * {@link com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator}.
 * <p>
 * The activity utilizes Android UI components such as {@link android.widget.ImageView} for navigation
 * and {@link androidx.viewpager.widget.ViewPager}.
 * Haptic feedback is provided by {@link com.purnendu.quizo.utilities.QuizoVibrator}.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for RuleActivity
public class RuleActivity extends AppCompatActivity {

    /**
     * Static {@link androidx.viewpager.widget.ViewPager} instance, allowing external access if needed.
     * This might be used for programmatic page changes or other interactions from outside the activity.
     */
    public static ViewPager viewPager;

    /**
     * The adapter responsible for providing views to the {@link #viewPager}.
     */
    RulesViewPagerAdapter adapter;

    /**
     * Called when the activity is first created. This is where you should do all of your
     * normal static set up: create views, bind data to lists, etc.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lock the screen orientation to prevent rotation issues
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_rule); // Set the layout for this activity

        // Set the navigation bar color to black
        getWindow().setNavigationBarColor(ContextCompat.getColor(this,
                R.color.black));

        // Initialize UI components for dots indicator and back button
        SpringDotsIndicator springDotsIndicator = findViewById(R.id.spring_dots_indicator);
        ImageView back = findViewById(R.id.imageRule);

        // Set OnClickListener for the back button (ImageView)
        back.setOnClickListener(V -> {
            QuizoVibrator.vibratePhone(this); // Provide haptic feedback
            finish(); // Close the current activity
        });

        // Initialize ViewPager and its adapter
        viewPager = findViewById(R.id.viewpager);
        adapter = new RulesViewPagerAdapter(this);
        viewPager.setAdapter(adapter); // Set the adapter for the ViewPager
        springDotsIndicator.setViewPager(viewPager); // Link the dots indicator to the ViewPager
    }
}
