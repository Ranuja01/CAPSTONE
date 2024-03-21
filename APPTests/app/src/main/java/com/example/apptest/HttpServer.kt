package com.example.apptest

import fi.iki.elonen.NanoHTTPD
import java.io.IOException

// Class to define the HTTP Server
class AndroidHttpServer() : NanoHTTPD(8080) {

    private var messageContent = "Hello From Ranuja"

    // function to serve the request
    override fun serve(session: IHTTPSession): Response {
        return when (session.method) {
            Method.POST -> handlePostRequest(session)
            Method.GET -> handleGetRequest(session)
            else -> newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, "Method not allowed")
        }
    }

    // POST request to send data to the server
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

    // GET request to get data from the server
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
        //callback.onMessageUpdated(messageContent)
    }
}

// Global object to store the server instance
object HttpServerManager {
    private var httpServer: AndroidHttpServer? = null

    fun startServer() {
        httpServer = AndroidHttpServer()

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
