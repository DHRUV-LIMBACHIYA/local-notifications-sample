package com.example.localnotifications.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.localnotifications.NotificationResponseActivity
import com.example.localnotifications.R
import com.example.localnotifications.actionhandler.NotificationActionIntentService
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

/**
 * Created by Dhruv Limbachiya on 06-07-2021.
 */

// Step 1 : Build Notification using NotificationCompat.Builder
// Step 2 : Create a Notification Channel for Android 8.0 or higher.
// Step 3: Display Notification.


object NotificationUtil {

    const val NOTIFICATION_CHANNEL_ID = "CHANNEL_ID_ONE"
    const val NOTIFICATION_ID = 101
    const val NOTIFICATION_PROGRESS_INDICATOR = 102

    private val diposable = CompositeDisposable()

    @SuppressLint("StaticFieldLeak")
    private var notificationBuilder: NotificationCompat.Builder? = null

    /**
     * Step 1 : Build Notification using NotificationCompat.Builder
     * Function responsible for building Notification.
     */
    fun buildNotification(context: Context) {

        val intent = Intent(context, NotificationResponseActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Pending Intent to open a new activity in future.
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

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
    fun createNotificationChannel(context: Context) {
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
            getNotificationManager(context).createNotificationChannel(notificationChannel)
        }
    }

    /**
     * Step 3: Display Notification.
     */
    fun displayNotification(context: Context) {
        getNotificationManager(context).notify(NOTIFICATION_ID, notificationBuilder?.build())
    }

    // Build notification with snooze & dismiss action buttons
    fun buildNotificationWithActionButtons(context: Context) : NotificationCompat.Builder?{
        // Snooze Action
        val snoozeIntent = Intent(context, NotificationActionIntentService::class.java).apply {
            action = NotificationActionIntentService.SNOOZE_ACTION
        }

        // Dismiss Action
        val dismissIntent = Intent(context, NotificationActionIntentService::class.java).apply {
            action = NotificationActionIntentService.DISMISS_ACTION
        }

        val snoozePendingIntent = PendingIntent.getService(context, 0, snoozeIntent, 0)
        val dismissPendingIntent = PendingIntent.getService(context, 0, dismissIntent, 0)

        notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_directions_bike_24) // Display a small icon on the left side.
            .setContentTitle("Cycling") // Notification Title
            .setContentText("Let take a ride") // Notification Subtitle.
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set the interrupting behaviour by giving priority.
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_alarm_24,
                    "Snooze",
                    snoozePendingIntent
                )
            ) // Add Snooze action button
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_baseline_cancel_24,
                    "Dismiss",
                    dismissPendingIntent
                )
            ) // Add Dismiss action button.
            .setAutoCancel(true) // Dismiss/Cancel the notification on Tap.

        setNotificationBuilderInstance(notificationBuilder!!)

        return notificationBuilder
    }

    // Build and fire an progress indicator Notification.
    fun buildProgressIndicatorNotification(context: Context){
        val maxProgress = 100
        var currentProgress = 0

        val progressNotificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_alarm_24)
            .setContentTitle(context.getString(R.string.text_map_route))
            .setContentText(context.getString(R.string.text_downloading))
            .setOngoing(true) // Ongoing notifications cannot be dismissed by the user
            .setProgress(maxProgress,currentProgress,true)

        // Initial notification
        getNotificationManager(context).notify(NOTIFICATION_PROGRESS_INDICATOR, progressNotificationBuilder.build())

        // RxJava implementation for updating progress status.
        diposable.add(Observable
            .interval(0,2,TimeUnit.SECONDS)
            .take(6) // 1 = 20,2 = 40,3 = 60,4 = 80,5 = 100,6 = Complete
            .flatMap {
                return@flatMap Observable.create<Int> { emitter ->
                    currentProgress += 20
                    emitter.onNext(currentProgress)
                }
            }
            .delay(2,TimeUnit.SECONDS)
            .subscribe  { progress ->
                if(progress <= maxProgress){
                    progressNotificationBuilder.setContentText("Progress : $progress%")
                    progressNotificationBuilder.setProgress(maxProgress,progress,false)
                }else{
                    progressNotificationBuilder.setContentText(context.getString(R.string.text_download_complete))
                    progressNotificationBuilder.setOngoing(false)
                    progressNotificationBuilder.setProgress(0,0,false) // set 0 - max , 0- current to indicate progress completed.
                }
                // Notify the progress
                getNotificationManager(context).notify(NOTIFICATION_PROGRESS_INDICATOR, progressNotificationBuilder.build())
            })

    }

    // Get Notification Manager
    fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    // Set notification builder instance
    private fun setNotificationBuilderInstance(builder: NotificationCompat.Builder) {
        this.notificationBuilder = builder
    }

    // Get notification builder instance
    fun getNotificationBuilder(): NotificationCompat.Builder? = notificationBuilder

    // Clear notification resources.
    fun clearRes() {
        notificationBuilder = null
        diposable.dispose()
    }

}
