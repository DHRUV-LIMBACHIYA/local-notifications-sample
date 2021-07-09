package com.example.localnotifications

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
            }

        }

        mBinding.fireNotificationButton.setOnClickListener {
            if(notificationId == 0){
                NotificationUtil.buildProgressIndicatorNotification(this) // Fire progress indicator notification.
            }else{
                NotificationUtil.displayNotification(this,notificationId) // Fire notification according notification id.
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NotificationUtil.clearRes()
    }
}