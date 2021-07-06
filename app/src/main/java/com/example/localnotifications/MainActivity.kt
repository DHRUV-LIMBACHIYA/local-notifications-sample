package com.example.localnotifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.localnotifications.databinding.ActivityMainBinding
import com.example.localnotifications.util.NotificationUtil

class MainActivity : AppCompatActivity() {

    private lateinit var notificationUtil: NotificationUtil
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        notificationUtil = NotificationUtil(this)

        notificationUtil.createNotificationChannel() // Create a notification channel for Android 8 or higher
        notificationUtil.buildNotification() // Build a notification.

        mBinding.fireNotificationButton.setOnClickListener {
            notificationUtil.displayNotification() // Fire a notification.
        }
    }
}