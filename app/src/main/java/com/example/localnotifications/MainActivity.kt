package com.example.localnotifications

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.localnotifications.databinding.ActivityMainBinding
import com.example.localnotifications.util.NotificationUtil

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        NotificationUtil.createNotificationChannel(this) // Create a notification channel for Android 8 or higher
        NotificationUtil.createExpandableNotificationChannel(this)

        mBinding.fireBasicNotificationButton.setOnClickListener {
            NotificationUtil.buildNotificationWithActionButtons(this) // Build a notification with action button.
            NotificationUtil.displayNotification(this) // Fire a notification.
        }

        mBinding.downloadNotificationButton.setOnClickListener {
            NotificationUtil.buildProgressIndicatorNotification(this)
        }

        mBinding.expandableNotificationButton.setOnClickListener {
            NotificationUtil.buildExpandableNotification(this)
            NotificationUtil.displayExpandableNotification(this)
        }
    }

    override fun onStop() {
        super.onStop()
        NotificationUtil.clearRes()
    }
}