package com.purnendu.quizo.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.purnendu.quizo.R;
import com.purnendu.quizo.models.LeaderBoard;
import com.purnendu.quizo.utilities.Constants;

import java.util.List;

/**
 * `LeaderBoardAdapter` is a custom {@link RecyclerView.Adapter} used to display a list of
 * {@link com.purnendu.quizo.models.LeaderBoard} entries in a {@link RecyclerView}.
 * It populates each row with a player's name and their total points.
 * <p>
 * This adapter is responsible for:
 * <ul>
 * <li>Inflating the layout for each leaderboard entry (defined in `R.layout.item_leaderboard_entry`).</li>
 * <li>Binding {@link LeaderBoard} data to the corresponding `TextView` elements in each list item.</li>
 * <li>Updating its data set efficiently when new leaderboard information becomes available.</li>
 * </ul>
 * <p>
 * The adapter uses an inner static {@link LeaderboardViewHolder} class to hold references
 * to the views for each item, promoting efficient view recycling.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 * @see com.purnendu.quizo.models.LeaderBoard
 * @see com.purnendu.quizo.utilities.Constants
 * @see androidx.recyclerview.widget.RecyclerView
 */
public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.LeaderboardViewHolder> {

    private List<LeaderBoard> leaderboardList;

    /**
     * Constructor for the LeaderBoardAdapter.
     *
     * @param leaderboardList The list of LeaderBoard objects to display.
     */
    public LeaderBoardAdapter(List<LeaderBoard> leaderboardList) {
        this.leaderboardList = leaderboardList;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each row in the RecyclerView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard_entry, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        // Get the LeaderBoard object for the current position
        LeaderBoard entry = leaderboardList.get(position);

        // Bind the data to the TextViews in the ViewHolder
        holder.tvPlayerName.setText(entry.getName());
        holder.tvPlayerPoints.setText(Constants.formatScore(entry.getTotalPoints()));
    }

    @Override
    public int getItemCount() {
        // Return the total number of items in the list
        return leaderboardList.size();
    }

    /**
     * Updates the data set of the adapter and notifies the RecyclerView to refresh.
     *
     * @param newLeaderboardList The new list of LeaderBoard objects.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<LeaderBoard> newLeaderboardList) {
        this.leaderboardList = newLeaderboardList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for the LeaderBoardAdapter. Holds references to the UI elements
     * of each list item.
     * Declared as public static to ensure proper visibility and prevent lint warnings.
     */
    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder { // Changed to public static
        TextView tvPlayerName;
        TextView tvPlayerPoints;

        LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize TextViews from the item layout
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvPlayerPoints = itemView.findViewById(R.id.tvPlayerPoints);
        }
    }
}
