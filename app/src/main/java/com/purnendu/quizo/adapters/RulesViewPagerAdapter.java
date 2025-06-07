package com.purnendu.quizo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.purnendu.quizo.R;

/**
 * This adapter is responsible for populating a {@link androidx.viewpager.widget.ViewPager}
 * with individual rule pages for the Quizo application.
 * It inflates custom layouts for each rule page, displaying text and images as needed.
 * <p>
 * The adapter extends {@link androidx.viewpager.widget.PagerAdapter} and utilizes
 * Android UI components such as {@link android.widget.TextView} and {@link android.widget.ImageView}
 * for presenting the rules content. It requires a {@link android.content.Context} to inflate views.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
public class RulesViewPagerAdapter extends PagerAdapter {

    // Context from the activity, used for inflating layouts
    Context ctx;

    /**
     * Constructor for the RulesViewPagerAdapter.
     *
     * @param ctx The {@link android.content.Context} of the activity or application.
     */
    public RulesViewPagerAdapter(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * Returns the number of views available.
     * In this case, it's hardcoded to 4, representing 4 rule pages.
     *
     * @return The number of pages to be displayed in the ViewPager.
     */
    @Override
    public int getCount() {
        return 4;
    }

    /**
     * Determines whether a page View is associated with a specific key object as returned by {@link #instantiateItem(ViewGroup, int)}.
     * This method is required by {@link androidx.viewpager.widget.PagerAdapter}.
     *
     * @param view   The page View to check.
     * @param object The key object returned by {@link #instantiateItem(ViewGroup, int)}.
     * @return true if the view is associated with the key object.
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * Create the page for the given position. The adapter is responsible for adding the view to the container.
     *
     * @param container The containing View in which the page will be shown.
     * @param position  The page position to be instantiated.
     * @return Returns an {@link java.lang.Object} representing the new page. This does not need to be a {@link android.view.View}.
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        // Get LayoutInflater service from the context
        LayoutInflater layoutInflater = (LayoutInflater) ctx.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflate the slide_rules layout for the current page
        View view = layoutInflater.inflate(R.layout.slide_rules, container, false);

        // Initialize ImageView and TextView within the inflated layout
        ImageView logo = view.findViewById(R.id.dancer); // Assuming 'dancer' is the ID for the image
        TextView title = view.findViewById(R.id.textView31); // Assuming this is the ID for the title/description

        // Set content (image and text) based on the current page position
        switch (position) {
            case 0:
                logo.setImageResource(R.drawable.question); // Set image for the first page
                title.setText(R.string.rules1_description); // Set text for the first page
                break;
            case 1:
                logo.setImageResource(R.drawable.correct); // Set image for the second page
                title.setText(R.string.rules2_description); // Set text for the second page
                break;
            case 2:
                logo.setImageResource(R.drawable.incorrect); // Set image for the third page
                title.setText(R.string.rules3_description); // Set text for the third page
                break;
            case 3:
                logo.setImageResource(R.drawable.noted); // Set image for the fourth page
                title.setText(R.string.rules4_description); // Set text for the fourth page
                break;
        }
        container.addView(view); // Add the inflated view to the ViewPager container
        return view; // Return the view
    }

    /**
     * Remove a page for the given position from the containing "container".
     * This method is required by {@link androidx.viewpager.widget.PagerAdapter}.
     *
     * @param container The containing View from which the page will be removed.
     * @param position  The page position to be removed.
     * @param object    The key object representing the page to be removed, as returned by {@link #instantiateItem(ViewGroup, int)}.
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position,
                            @NonNull Object object) {
        container.removeView((View) object); // Remove the view from the container
    }
}
