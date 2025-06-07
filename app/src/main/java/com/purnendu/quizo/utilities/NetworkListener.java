package com.purnendu.quizo.utilities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import androidx.appcompat.app.AlertDialog;

import com.purnendu.quizo.R;

/**
 * A {@link BroadcastReceiver} that listens for network connectivity changes and notifies
 * registered listeners. This class manages the display and dismissal of an
 * {@link AlertDialog} to inform the user about network disconnection.
 *
 * <p>It requires an {@link Activity} context to properly display UI-related elements like
 * {@link AlertDialog} and to ensure UI operations run on the main thread.</p>
 *
 * <p>To use this class, register an instance of it in your {@link Activity}'s
 * {@code onResume()} or {@code onStart()} method using an {@link android.content.IntentFilter}
 * for {@link ConnectivityManager#CONNECTIVITY_ACTION}, and unregister it in
 * {@code onPause()} or {@code onStop()} to prevent memory leaks.</p>
 *
 * @author Purnendu Guha
 * @version 2.0.1
 * @see NetworkChangeListener
 * @see ConnectivityManager
 * @see AlertDialog
 */
public class NetworkListener extends BroadcastReceiver {

    /**
     * An interface to provide callbacks for network connection status changes.
     */
    private final NetworkChangeListener networkChangeListener;
    /**
     * The {@link Activity} context associated with this receiver. Used for UI operations
     * like displaying dialogs and running on the UI thread.
     */
    private final Activity activity;
    /**
     * The {@link AlertDialog} instance used to display network disconnection messages.
     * Managed to prevent multiple dialogs and ensure proper dismissal.
     */
    private AlertDialog alertDialog;

    /**
     * Constructs a new {@code NetworkListener}.
     *
     * @param activity              The {@link Activity} context that will host the network listener.
     *                              This is crucial for displaying UI elements like dialogs.
     * @param networkChangeListener The {@link NetworkChangeListener} instance to notify
     *                              of network connectivity changes.
     */
    public NetworkListener(Activity activity, NetworkChangeListener networkChangeListener) {
        this.networkChangeListener = networkChangeListener;
        this.activity = activity;
    }

    /**
     * Called when the {@link BroadcastReceiver} is receiving an {@link Intent} broadcast.
     * This method checks the current network connectivity status and triggers the
     * appropriate callbacks on the {@link NetworkChangeListener}. It also manages
     * the display and dismissal of the network disconnected dialog.
     *
     * @param context The {@link Context} in which the receiver is running.
     * @param intent  The {@link Intent} being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Ensure the activity is still alive and not finishing/destroyed before performing UI operations.
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        if (isConnected(context)) {
            networkChangeListener.onNetworkConnected();
            dismissNetworkDisconnectedDialog();
        } else {
            networkChangeListener.onNetworkDisconnected();
            showNetworkDisconnectedDialog();
        }
    }

    /**
     * Checks if the device is currently connected to a network (Wi-Fi, Cellular, or Ethernet).
     *
     * @param context The {@link Context} used to access system services.
     * @return {@code true} if connected to Wi-Fi, Cellular, or Ethernet; {@code false} otherwise.
     */
    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            );
        }
        return false;
    }

    /**
     * Displays an {@link AlertDialog} to inform the user that they are not connected to the internet.
     * The dialog is shown on the UI thread and is not dismissible by the user via outside tap or back button.
     * It ensures that only one dialog is shown at a time.
     */
    private void showNetworkDisconnectedDialog() {
        activity.runOnUiThread(() -> {
            // Re-check activity state before showing dialog in case it was destroyed recently.
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }

            if (alertDialog == null || !alertDialog.isShowing()) {
                alertDialog = new AlertDialog.Builder(activity) // Use the Activity context for the dialog
                        .setCancelable(false) // Prevents user from dismissing the dialog
                        .setMessage("It seems that you're currently not connected to the internet." +
                                " Please check your connection and try again.")
                        .setTitle("Connectivity Issue")
                        .setIcon(R.drawable.no_internet)
                        .show();
            }
        });
    }

    /**
     * Dismisses the currently displayed network disconnected {@link AlertDialog}, if it is showing.
     * This operation is performed on the UI thread and clears the dialog reference.
     */
    public void dismissNetworkDisconnectedDialog() {
        activity.runOnUiThread(() -> {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
                alertDialog = null; // Clear reference to prevent memory leaks and allow garbage collection
            }
        });
    }

    /**
     * Interface definition for a callback to be invoked when network connectivity changes.
     */
    public interface NetworkChangeListener {
        /**
         * Called when the device establishes a network connection.
         */
        void onNetworkConnected();

        /**
         * Called when the device loses its network connection.
         */
        void onNetworkDisconnected();
    }
}
