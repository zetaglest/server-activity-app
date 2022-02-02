package com.example.megaglestactivegamestatus

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private val cid = "c42"
    private val nid = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Before you can deliver the notification on Android 8.0 and higher,
        // you must register your app's notification channel with the system by
        // passing an instance of NotificationChannel
        // to createNotificationChannel().
        createNotificationChannel()

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // Set the notification content
        // https://developer.android.com/training/notify-user/build-notification#kts
        val builder = NotificationCompat.Builder(this, cid)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("My MG notification")
            .setContentText("Players Online")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

       // val getPlayMG = GetPlayMG()
       // getPlayMG.getRaw(this)
        while (true) {
            val isWaiting = getActivity()
            if (isWaiting) {
                // show the notification
                with(NotificationManagerCompat.from(this)) {
                    // notificationId is a unique int for each notification
                    // that you must define
                    notify(nid, builder.build())
                }
            }
            runBlocking { delay(60000L) }
        }
    }

    private fun getActivity(): Boolean {
        val client = HttpClient(CIO)
        val response: HttpResponse = runBlocking {client.get("https://play.mg")}
        val stringBody: String = runBlocking { response.receive() }
        val resultTextView: TextView = findViewById(R.id.textView)
        var isWaiting = false
        if (response.status.toString().startsWith("200")) {
            //        resultTextView.text = response.status.toString()

            val waitingStr = ">waiting for players</td>"
            val td = "<td>"
            var idx = stringBody.indexOf(waitingStr)
            var resultText = ""
            while (idx != -1) {
                var tdCount = idx
                repeat(5) { tdCount = stringBody.indexOf(td, tdCount) + td.length }
                val playerCount = stringBody[tdCount]
                if (playerCount != '0') {
                    isWaiting = true
                    break
                }
                resultText += "\n" + playerCount
                idx = stringBody.indexOf(waitingStr, tdCount)
            }

            //        val resultTextView: TextView = findViewById(R.id.textView)
            //        resultTextView.text = stringBody[tdCount+1].toString()
            resultTextView.text = resultText
            // println(stringBody)
        }
        else {
            resultTextView.text = getString(R.string.server_response, response.status.toString())
        }
        client.close()
        return isWaiting
    }

    // For notifications, create a channel and set the importance
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(cid, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}