package com.udacity.project4.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.udacity.project4.R
import com.udacity.project4.data.localDataSource.ReminderData
import com.udacity.project4.ui.ReminderInformationActivity
import kotlin.random.Random

const val NOTIFICATION_ID = "notification_id"
const val REMINDER_NOW = "reminder_now"

fun NotificationManager.sendNotification(reminder: ReminderData,context: Context) {
    //to make notification clickable and navigates to details Activity
    val contentIntent = Intent(context,ReminderInformationActivity::class.java)
    val notificationId = Random.nextInt(0,20)
    contentIntent.putExtra(REMINDER_NOW,reminder)
    contentIntent.putExtra(NOTIFICATION_ID,notificationId)
    val pendingIntent = PendingIntent.getActivity(context, notificationId,contentIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    //builder to create my notification and i use NotificationCompat to support old versions
    val builder = NotificationCompat.Builder(context,context.getString(R.string.notification_channel_id))
        //.addAction(R.drawable.ic_location_icon,"Open reminder",pendingIntent)
        .setSmallIcon(R.drawable.ic_location_icon)
        .setContentTitle(reminder.title)
        .setContentText(reminder.description)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notify(notificationId,builder.build())
}

fun NotificationManager.cancelNotification(notificationId: Int) {
    cancel(notificationId)
}