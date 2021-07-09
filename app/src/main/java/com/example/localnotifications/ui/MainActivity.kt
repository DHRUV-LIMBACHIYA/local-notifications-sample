package com.example.localnotifications.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.localnotifications.R
import com.example.localnotifications.databinding.ActivityMainBinding
import com.example.localnotifications.util.NotificationUtil

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private var notificationId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        NotificationUtil.createNotificationChannel(this) // Create a notification channel for basic/non-expandable notification
        NotificationUtil.createExpandableNotificationChannel(this) // Create a notification channel for expandable notification

        mBinding.notificationTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                mBinding.basicNotificationRadioButton.id -> {
                    NotificationUtil.buildNotification(this)
                    notificationId = NotificationUtil.BASIC_NOTIFICATION_ID
                }

                mBinding.actionButtonNotificationRadioButton.id -> {
                    NotificationUtil.buildNotificationWithActionButtons(this)
                    notificationId = NotificationUtil.ACTION_BUTTON_NOTIFICATION_ID
                }

                mBinding.progressIndicatorNotificationRadioButton.id -> {
                    notificationId = 0
                }

                mBinding.bigPictureStyleNotificationRadioButton.id -> {
                    NotificationUtil.buildExpandableNotification(this,0)
                    notificationId = NotificationUtil.BIG_PICTURE_STYLE_NOTIFICATION_ID
                }

                mBinding.bigTextStyleNotificationRadioButton.id -> {
                    NotificationUtil.buildExpandableNotification(this,1)
                    notificationId = NotificationUtil.BIG_TEXT_STYLE_NOTIFICATION_ID
                }

                mBinding.inboxStyleNotificationRadioButton.id -> {
                    NotificationUtil.buildExpandableNotification(this,2)
                    notificationId = NotificationUtil.INBOX_STYLE_NOTIFICATION_ID
                }

                mBinding.mediaStyleNotificationRadioButton.id -> {
                    NotificationUtil.buildExpandableNotification(this,3)
                    notificationId = NotificationUtil.MEDIA_STYLE_NOTIFICATION_ID
                }
                mBinding.customNotificationRadioButton.id -> {
                    NotificationUtil.buildCustomNotification(this)
                    notificationId = NotificationUtil.CUSTOM_NOTIFICATION_ID
                }
            }

        }

        mBinding.fireNotificationButton.setOnClickListener {
            if(notificationId == 0){
                NotificationUtil.buildProgressIndicatorNotification(this) // Fire progress indicator notification.
            }else{
                NotificationUtil.displayNotification(this,notificationId) // Fire notification according notification id.
            }
        }

        mBinding.groupNotificationButton.setOnClickListener {
            NotificationUtil.buildGroupSummaryNotification(this)
            NotificationUtil.displayNotification(this,NotificationUtil.GROUP_SUMMARY_ID)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_setting){
            // Open notification channel settings
            val settingIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE,application.packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID,NotificationUtil.SIMPLE_NOTIFICATION_CHANNEL)
            }
            startActivity(settingIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        NotificationUtil.clearRes()
    }
}