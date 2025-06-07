package com.purnendu.quizo.adapters;


import static com.purnendu.quizo.utilities.DateParser.formatDate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.purnendu.quizo.R;
import com.purnendu.quizo.models.Attempt;

import java.util.List;

/**
 * This adapter is responsible for displaying a list of quiz attempts (history) in a
 * {@link androidx.recyclerview.widget.RecyclerView} within the Quizo application.
 * It binds {@link com.purnendu.quizo.models.Attempt} data to individual list items,
 * showing details such as subject, earned points, and date.
 * The date formatting is handled by {@link com.purnendu.quizo.utilities.DateParser#formatDate(long)}.
 * <p>
 * The adapter utilizes {@link androidx.recyclerview.widget.RecyclerView.ViewHolder} pattern
 * for efficient view recycling and Android UI components like {@link android.widget.TextView}
 * and {@link androidx.cardview.widget.CardView} for item presentation.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.AttemptViewHolder> {

    // List of quiz attempts to be displayed by the adapter
    private final List<Attempt> attempts;

    /**
     * Constructor for the HistoryAdapter.
     *
     * @param attempts The list of {@link com.purnendu.quizo.models.Attempt} objects to be displayed.
     */
    public HistoryAdapter(List<Attempt> attempts) {
        this.attempts = attempts;
    }

    /**
     * Called when RecyclerView needs a new {@link AttemptViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new AttemptViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public AttemptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_history layout for each list item
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_history, parent, false);
        return new HistoryAdapter.AttemptViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * updates the contents of the {@link AttemptViewHolder#itemView} to reflect the item at the given
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull AttemptViewHolder holder, int position) {

        // Get the Attempt object for the current position
        Attempt item = attempts.get(position);

        // Set the text for subject, earned points, and formatted date
        holder.tvSubject.setText(String.valueOf(item.getSubject()));
        holder.tvEarned.setText(String.valueOf(item.getEarned()));
        holder.tvDate.setText(formatDate(item.getCreatedTime()));

        // Set an OnClickListener for the parent CardView.
        // Currently, it does nothing, but can be extended for item click actions.
        holder.cvParent.setOnClickListener(view -> {
            // DO NOTHING - Placeholder for future item click handling
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return attempts.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     * This inner class holds references to all UI components within a single history item layout.
     */
    public static class AttemptViewHolder extends RecyclerView.ViewHolder {

        // TextViews to display subject, earned points, and date
        public TextView tvSubject, tvEarned, tvDate; //textview
        // CardView representing the parent container for a single history item
        public CardView cvParent; //cardview

        /**
         * Constructor for the AttemptViewHolder.
         *
         * @param v The View for a single history item.
         */
        public AttemptViewHolder(View v) {
            super(v);
            // Initialize TextViews and CardView by finding their IDs in the item layout
            tvSubject = v.findViewById(R.id.textView23);
            tvEarned = v.findViewById(R.id.textView24);
            tvDate = v.findViewById(R.id.textView25);
            cvParent = v.findViewById(R.id.cvItemHistory);
        }
    }
}
