package com.example.megaglestactivegamestatus

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetPlayMG: ViewModel() {
     fun getRaw(context: AppCompatActivity, alertDetails: AlertDetails) {
         viewModelScope.launch {
             while (true) {
                 val client = HttpClient(CIO)
                 val response = getResponse(client)
                 val stringBody: String = getReceive(response)
                 client.close()
                 val resultTextView: TextView = context.findViewById(R.id.textView)
                 if (response.status.toString().startsWith("200")) {
                     val waitingStr = ">waiting for players</td>"
                     val td = "<td>"
                     var idx = stringBody.indexOf(waitingStr)
                     var resultText = ""
                     while (idx != -1) {
                         var tdCount = idx
                         repeat(5) { tdCount = stringBody.indexOf(td, tdCount) + td.length }
                         val playerCount = stringBody[tdCount]
                         if (playerCount != '0') {
                             resultText += "\n" + playerCount
                             alertDetails.showNotification()
                             break
                         }
                         resultText += "\n" + playerCount
                         idx = stringBody.indexOf(waitingStr, tdCount)
                     }

                     resultTextView.text = resultText
                 } else {
                     resultTextView.text =
                         context.getString(R.string.server_response, response.status.toString())
                 }
                 delay(60000L)
             }
         }
     }

    private suspend fun getResponse(client: HttpClient): HttpResponse {
        val response: HttpResponse
        withContext(Dispatchers.IO) {
            response = client.get("https://play.mg")
        }
        return response
    }

    private suspend fun getReceive(response: HttpResponse): String {
        val stringBody: String
        withContext(Dispatchers.IO) {
            stringBody = response.receive()
        }
        return stringBody
    }
}