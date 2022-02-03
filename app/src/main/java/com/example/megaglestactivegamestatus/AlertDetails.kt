package com.example.megaglestactivegamestatus

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// https://developer.android.com/training/notify-user/build-notification#kts
class AlertDetails(private val mainThread: Context) {
    private val channelID = "c42"
    private val notificationID = 42

    // show the notification
    // getter for the call to with()
    fun showNotification() {
        with(NotificationManagerCompat.from(mainThread)) {
            // notificationId is a unique int for each notification
            // that you must define
            notify(notificationID, builder.build())
        }
    }

    // Create an explicit intent for an Activity in your app
    private val intent = Intent(mainThread, AlertDetails::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    // https://developer.android.com/guide/components/intents-filters#DeclareMutabilityPendingIntent
    // Avoids the warning: "Missing PendingIntent mutability flag [UnspecifiedImmutableFlag]"
    // https://developer.android.com/about/versions/12/behavior-changes-12#test-pending-intent
    private val pendingFlag = if (Build.VERSION.SDK_INT >= 23) FLAG_IMMUTABLE else 0
    private val pendingIntent: PendingIntent =
        PendingIntent.getActivity(mainThread, 0, intent, pendingFlag)

    // Set the notification content
    // https://developer.android.com/training/notify-user/build-notification#kts
    private val builder = NotificationCompat.Builder(mainThread, channelID)
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle("My MG notification")
        .setContentText("Players Online")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        // Set the intent that will fire when the user taps the notification
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    // For notifications, create a channel and set the importance
    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = mainThread.getString(R.string.channel_name)
            val descriptionText = mainThread.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                mainThread.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}