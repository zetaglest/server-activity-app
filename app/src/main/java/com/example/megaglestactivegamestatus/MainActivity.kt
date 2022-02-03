package com.example.megaglestactivegamestatus

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val alertDetails = AlertDetails(this)
        // Before you can deliver the notification on Android 8.0 and higher,
        // you must register your app's notification channel with the system by
        // passing an instance of NotificationChannel
        // to createNotificationChannel().
        alertDetails.createNotificationChannel()

        val getPlayMG = GetPlayMG()

        getPlayMG.getRaw(this, alertDetails)
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
}