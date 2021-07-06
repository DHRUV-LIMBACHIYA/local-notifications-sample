package com.example.localnotifications.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.localnotifications.NotificationResponseActivity
import com.example.localnotifications.R

/**
 * Created by Dhruv Limbachiya on 06-07-2021.
 */

// Step 1 : Build Notification using NotificationCompat.Builder
// Step 2 : Create a Notification Channel for Android 8.0 or higher.
// Step 3: Display Notification.

const val NOTIFICATION_CHANNEL_ID = "CHANNEL_ID_ONE"
const val NOTIFICATION_ID = 101

class NotificationUtil(private val context: Context) {

    private lateinit var notificationManager: NotificationManager
    private var notificationBuilder: NotificationCompat.Builder? = null


    /**
     * Step 1 : Build Notification using NotificationCompat.Builder
     * Function responsible for building Notification.
     */
    fun buildNotification() {

        val intent = Intent(context,NotificationResponseActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Pending Intent to open a new activity in future.
        val pendingIntent = PendingIntent.getActivity(context,0,intent,0)

        notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_directions_bike_24) // Display a small icon on the left side.
            .setContentTitle("Cycling") // Notification Title
            .setContentText("Let take a ride") // Notification Subtitle.
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set the interrupting behaviour by giving priority.
            .setContentIntent(pendingIntent) // Execute the pending intent when user tap on the notification.
            .setAutoCancel(true) // Dismiss/Cancel the notification on Tap.
    }

    /**
     * Step 2 : Create a Notification Channel for Android 8.0 or higher.
     */
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.channel_number_one) // Channel name
            val channelDescription =
                context.getString(R.string.channel_number_one_desc) // Channel Description
            val importance =
                NotificationManager.IMPORTANCE_DEFAULT  // Channel Interrupting Level or priority.
            val notificationChannel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, importance).apply {
                    description = channelDescription // Channel Description [Optional]
                }

            // Register the channel with system.
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    /**
     * Step 3: Display Notification.
     */
    fun displayNotification() {
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder?.build())
    }

}
