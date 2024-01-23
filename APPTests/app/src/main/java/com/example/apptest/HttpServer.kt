package com.example.apptest

import fi.iki.elonen.NanoHTTPD
import java.io.IOException

interface MessageUpdateCallback {
    fun onMessageUpdated(newContent: String)
}

class AndroidHttpServer(private val callback: MessageUpdateCallback) : NanoHTTPD(8080) {

    private var messageContent = "Hello From Ranuja"

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

                // Update the message content based on user input
                updateMessageContent(response)

                return newFixedLengthResponse("Message received successfully")
            } catch (e: IOException) {
                e.printStackTrace()
                //return newFixedLengthResponse("Error handling POST request")
            }
        }
        return newFixedLengthResponse("Error handling POST request")
    }

    private fun handleGetRequest(session: IHTTPSession): Response {
        // Handle GET request
        if ("/Send" == session.uri) {
            // Use the current message content
            return newFixedLengthResponse(messageContent)
        }
        return newFixedLengthResponse("Error handling GET request")
    }

    // Method to update the message content
    fun updateMessageContent(newContent: String) {
        messageContent = newContent
        // Notify the callback about the updated message content
        callback.onMessageUpdated(messageContent)
    }
}

object HttpServerManager {
    private var httpServer: AndroidHttpServer? = null

    fun startServer(callback: MessageUpdateCallback) {
        httpServer = AndroidHttpServer(callback)

        Thread {
            try {
                httpServer?.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    fun stopServer() {
        httpServer?.stop()
    }

    fun updateMessageContent(newContent: String) {
        httpServer?.updateMessageContent(newContent)
    }
}
