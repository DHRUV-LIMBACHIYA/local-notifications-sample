package com.example.localnotifications.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.localnotifications.NotificationResponseActivity
import com.example.localnotifications.R
import com.example.localnotifications.actionhandler.NotificationActionIntentService
import com.example.localnotifications.ui.NotificationSpecialActivity
import com.example.localnotifications.ui.RegularActivity
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

    // Notification ids
    const val BASIC_NOTIFICATION_ID = 1
    const val ACTION_BUTTON_NOTIFICATION_ID = 2
    private const val PROGRESS_INDICATOR_NOTIFICATION_ID = 3
    const val BIG_PICTURE_STYLE_NOTIFICATION_ID = 4
    const val BIG_TEXT_STYLE_NOTIFICATION_ID = 5
    const val INBOX_STYLE_NOTIFICATION_ID = 6
    const val MEDIA_STYLE_NOTIFICATION_ID = 7

    // Notification Channels
    private const val SIMPLE_NOTIFICATION_CHANNEL = "CHANNEL_ID_ONE"
    private const val EXPANDABLE_NOTIFICATION_CHANNEL = "CHANNEL_ID_TWO"


    private val diposable = CompositeDisposable()

    @SuppressLint("StaticFieldLeak")
    private var notificationBuilder: NotificationCompat.Builder? = null

    /**
     * Step 1 : Build Notification using NotificationCompat.Builder
     * Function responsible for building Notification.
     */
    fun buildNotification(context: Context) {
        // Pending Intent to open a new activity in future.
        // Create a pending intent with back stack for regular activities(normal flows).
        val regularIntent = Intent(context,RegularActivity::class.java)

        // Create a special pending intent without back stack.
        // This intent will open activity in new task.
        val specialIntent = Intent(context,NotificationSpecialActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val regularPendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(regularIntent)
            getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val specialPendingIntent = PendingIntent.getActivity(context,0,specialIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        notificationBuilder = NotificationCompat.Builder(context, SIMPLE_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_baseline_directions_bike_24) // Display a small icon on the left side.
            .setContentTitle("Cycling") // Notification Title
            .setContentText("Let take a ride") // Notification Subtitle.
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set the interrupting behaviour by giving priority.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(specialPendingIntent) // Open an activity on new task.
//            .setContentIntent(regularPendingIntent) // Open an activity on existing task
            .setAutoCancel(true) // Dismiss/Cancel the notification on Tap.
    }

    /**
     * Step 2 : Create a Notification Channel for Android 8.0 or higher.
     * Notification channel for basic or non expandable notification.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.channel_number_one) // Channel name
            val channelDescription =
                context.getString(R.string.channel_number_one_desc) // Channel Description
            val importance =
                NotificationManager.IMPORTANCE_DEFAULT  // Channel Interrupting Level or priority.
            val notificationChannel =
                NotificationChannel(SIMPLE_NOTIFICATION_CHANNEL, channelName, importance).apply {
                    description = channelDescription // Channel Description [Optional]
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                }

            Log.i("TAG", "createNotificationChannel: ${notificationChannel.lockscreenVisibility}")

            // Register the channel with system.
            getNotificationManager(context).createNotificationChannel(notificationChannel)
        }
    }

    /**
     * Step 3: Display Notification.
     */
    fun displayNotification(context: Context, notificationId: Int) {
        getNotificationManager(context).notify(notificationId, notificationBuilder?.build())
    }

    // Build notification with snooze & dismiss action buttons
    fun buildNotificationWithActionButtons(context: Context): NotificationCompat.Builder? {

        // Snooze Action
        val snoozeIntent = Intent(context, NotificationActionIntentService::class.java).apply {
            action = NotificationActionIntentService.SNOOZE_ACTION
        }

        // Dismiss Action
        val dismissIntent = Intent(context, NotificationActionIntentService::class.java).apply {
            action = NotificationActionIntentService.DISMISS_ACTION
        }

        val fullScreenIntent = Intent(context, NotificationResponseActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, 0)

        val snoozePendingIntent = PendingIntent.getService(context, 0, snoozeIntent, 0)
        val dismissPendingIntent = PendingIntent.getService(context, 0, dismissIntent, 0)

        notificationBuilder = NotificationCompat.Builder(context, SIMPLE_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_baseline_directions_bike_24) // Display a small icon on the left side.
            .setContentTitle("Cycling") // Notification Title
            .setContentText("Let take a ride") // Notification Subtitle.
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Set the interrupting behaviour by giving priority.
            .setFullScreenIntent(fullScreenPendingIntent, true)
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
    fun buildProgressIndicatorNotification(context: Context) {
        val maxProgress = 100
        var currentProgress = 0

        val progressNotificationBuilder =
            NotificationCompat.Builder(context, SIMPLE_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                .setContentTitle(context.getString(R.string.text_map_route))
                .setContentText(context.getString(R.string.text_downloading))
                .setOngoing(true) // Ongoing notifications cannot be dismissed by the user
                .setProgress(maxProgress, currentProgress, true)

        // Initial notification
        getNotificationManager(context).notify(
            PROGRESS_INDICATOR_NOTIFICATION_ID,
            progressNotificationBuilder.build()
        )

        // RxJava implementation for updating progress status.
        diposable.add(Observable
            .interval(0, 2, TimeUnit.SECONDS)
            .take(6) // Use take operator to stop interval. 1 = 20,2 = 40,3 = 60,4 = 80,5 = 100,6 = Complete and now stop the interval.
            .flatMap {
                return@flatMap Observable.create<Int> { emitter ->
                    currentProgress += 20
                    emitter.onNext(currentProgress)
                }
            }
            .delay(2, TimeUnit.SECONDS)
            .subscribe { progress ->
                if (progress <= maxProgress) {
                    progressNotificationBuilder.setContentText("Progress : $progress%")
                    progressNotificationBuilder.setProgress(maxProgress, progress, false)
                } else {
                    progressNotificationBuilder.setContentText(context.getString(R.string.text_download_complete))
                    progressNotificationBuilder.setOngoing(false) // User can now dismiss the notification.
                    progressNotificationBuilder.setProgress(
                        0,
                        0,
                        false
                    ) // set 0 - max progess , 0 - current progress to indicate download completed.
                }
                // Notify the progress update.
                getNotificationManager(context).notify(
                    PROGRESS_INDICATOR_NOTIFICATION_ID,
                    progressNotificationBuilder.build()
                )
            })

    }


    /***
     * This section will demonstrate the Expandable Notification.
     */
    // Build an expandable notification.
    fun buildExpandableNotification(context: Context, notificationStyleIndex: Int) {

        val bitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.bike
        )

        val mediaSession = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaSessionCompat(context, "Tag")
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
        notificationBuilder =
            NotificationCompat.Builder(context, EXPANDABLE_NOTIFICATION_CHANNEL).apply {
                setSmallIcon(R.drawable.ic_music_note)
                setContentTitle(
                    if (notificationStyleIndex == 3) {
                        "Inna Sona"
                    } else {
                        context.getString(R.string.text_expandable_notification)
                    }
                )
                setContentText(
                    if (notificationStyleIndex == 3) {
                        "Arijit"
                    } else {
                        context.getString(R.string.text_expandable_notification)
                    }
                )
                setLargeIcon(bitmap) // Add thumbnail icon on right side of the notification
                setStyle(
                    when (notificationStyleIndex) {
                        0 -> {
                            // Big Picture style notification
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap) // Display big image on expand
                                .bigLargeIcon(null) // Hide thumbnail icon on expand
                        }
                        1 -> {
                            // Big Text style notification
                            NotificationCompat.BigTextStyle()
                                .bigText(context.getString(R.string.large_text))
                        }
                        2 -> {
                            // Inbox style notification
                            NotificationCompat.InboxStyle()
                                .addLine("Apply NotificationCompat.InboxStyle to a notification if you want to add multiple short summary lines, such as snippets from incoming emails. ")
                                .addLine("This allows you to add multiple pieces of content text that are each truncated to one line, instead of one continuous line of text provided by NotificationCompat.BigTextStyle.")
                        }
                        3 -> {
                            // Media style notification
                            androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(1, 2, 3)
                                .setMediaSession(mediaSession.sessionToken)
                        }
                        else -> {
                            // Big Picture style notification
                            NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap) // Display big image on expand
                                .bigLargeIcon(null) // Hide thumbnail icon on expand
                        }
                    }
                )

                if (notificationStyleIndex == 3) {
                    addAction(R.drawable.ic_dislike, "dislike", null) // Button = 1 & Index = 0
                    addAction(R.drawable.ic_previous, "previous", null) // Button = 2 & Index = 1
                    addAction(
                        R.drawable.ic_baseline_play_arrow_24,
                        "play",
                        null
                    ) // Button = 3 & Index = 2
                    addAction(R.drawable.ic_next, "next", null) // Button = 4 & Index = 3
                    addAction(R.drawable.ic_like, "like", null) // Button = 5 & Index = 5
                }

            }


    }

    // Create a separate channel for expandable notifications.
    fun createExpandableNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.channel_number_two) // Channel name
            val channelDescription =
                context.getString(R.string.channel_number_two_desc) // Channel Description
            val importance =
                NotificationManager.IMPORTANCE_DEFAULT  // Channel Interrupting Level or priority.
            val notificationChannel =
                NotificationChannel(
                    EXPANDABLE_NOTIFICATION_CHANNEL,
                    channelName,
                    importance
                ).apply {
                    description = channelDescription // Channel Description [Optional]
                }

            // Register the channel with system.
            getNotificationManager(context).createNotificationChannel(notificationChannel)
        }
    }

    /**
     * This section contains utility methods
     */
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
