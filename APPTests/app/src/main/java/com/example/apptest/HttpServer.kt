package com.example.apptest

import android.util.Log
import fi.iki.elonen.NanoHTTPD
import java.io.IOException

class AndroidHttpServer : NanoHTTPD(8080) {

    override fun serve(session: IHTTPSession): Response {
        return when (session.method) {
            Method.POST -> handlePostRequest(session)
            Method.GET -> handleGetRequest(session)
            else -> newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, "Method not allowed")
        }
    }

    private fun handlePostRequest(session: IHTTPSession): Response {
        if ("/Receive" == session.uri) {
            try {
                val reader = session.inputStream.bufferedReader()
                val response = reader.readText()
                println("Response from server: $response")

                return newFixedLengthResponse("Message received successfully")
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d("SendMessageTask", "ERRORRRRR")
                //return newFixedLengthResponse("Error handling POST request")
            }
        }
        return newFixedLengthResponse("Error handling POST request")
    }

    private fun handleGetRequest(session: IHTTPSession): Response {
        // Handle GET request
        if ("/Send" == session.uri) {
            return newFixedLengthResponse("Hello from Ranuja")
        }
        return newFixedLengthResponse("Error handling GET request")
    }
}