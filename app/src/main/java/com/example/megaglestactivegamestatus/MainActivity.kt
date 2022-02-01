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

//        val getPlayMG = GetPlayMG()
//        getPlayMG.getRaw(this)
//        while (true) {
//            viewModelScope.launch(Dispatchers.IO)  { getRaw() }
            runBlocking { getRaw() }
//            runBlocking {delay(60000)}
//        }
    }

    private suspend fun getRaw() {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.get("https://play.mg")
        val stringBody: String = response.receive()
        val resultTextView: TextView = findViewById(R.id.textView)
        if (response.status.toString().startsWith("200")) {
            //        resultTextView.text = response.status.toString()

            val waitingStr = ">waiting for players</td>"
            val td = "<td>"
            var idx = stringBody.indexOf(waitingStr)
            var resultText = ""
            while (idx != -1) {
                var tdCount = idx
                repeat(5) { tdCount = stringBody.indexOf(td, tdCount) + td.length }
                resultText += "\n" + stringBody[tdCount]
                idx = stringBody.indexOf(waitingStr, tdCount)
            }

            //        val resultTextView: TextView = findViewById(R.id.textView)
            //        resultTextView.text = stringBody[tdCount+1].toString()
            resultTextView.text = resultText
            // println(stringBody)
        }
        else {
//            resultTextView.text = context.getString(R.string.server_response, response.status.toString())
            resultTextView.text = """Help!!"""
        }
        client.close()
    }
}