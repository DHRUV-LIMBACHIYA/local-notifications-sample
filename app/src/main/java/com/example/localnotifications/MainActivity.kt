package com.example.localnotifications

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.localnotifications.databinding.ActivityMainBinding
import com.example.localnotifications.util.NotificationUtil

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        NotificationUtil.createNotificationChannel(this) // Create a notification channel for Android 8 or higher
        NotificationUtil.buildNotificationWithActionButtons(this) // Build a notification with action button.

        mBinding.fireNotificationButton.setOnClickListener {
            NotificationUtil.displayNotification(this) // Fire a notification.
        }

        mBinding.downloadNotificationButton.setOnClickListener {
            NotificationUtil.buildProgressIndicatorNotification(this)
        }
    }

    override fun onStop() {
        super.onStop()
        NotificationUtil.clearRes()
    }
}