package com.example.apptest

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SendMessageTask : AsyncTask<String, Void, Boolean>() {

    override fun doInBackground(vararg params: String): Boolean {
        val url = URL(params[0])
        val message = params[1]
        val urlConnection = url.openConnection() as HttpURLConnection
        Log.d("Tag", "qwerty")
        return try {
            Log.d("Tag", "A")

            urlConnection.requestMethod = "POST"
            urlConnection.setRequestProperty("Content-Type", "application/json")
            urlConnection.doOutput = true
            Log.d("Tag", "B")
            // Write the message to the request body
            val outputStream: OutputStream = urlConnection.outputStream
            val writer = BufferedWriter(OutputStreamWriter(outputStream, "UTF-8"))
            writer.write("{\"message\":\"$message\"}")  // Construct the JSON string
            writer.flush()
            writer.close()


            Log.d("Tag", "C")
            val responseCode = urlConnection.responseCode
            outputStream.flush()
            outputStream.close()
            Log.d("Tag", urlConnection.responseCode.toString())

            responseCode == HttpURLConnection.HTTP_OK

            //Log.d("Tag", "D")
        } catch (e: Exception) {
            Log.e("SendMessageTask", "Error sending message: ${e.message}", e)
            false
        }  finally {
            urlConnection.disconnect()
        }

    }

    override fun onPostExecute(result: Boolean) {
        if (result) {
            Log.d("SendMessageTask", "Message sent successfully")
        } else {
            Log.e("SendMessageTask", "Failed to send message")
        }
    }
}

class ReceiveInfoTask : AsyncTask<String, Void, String>() {
    // Define a listener for onPostExecute
    //var onPostExecuteListener: ((String) -> Unit)? = null

    override fun doInBackground(vararg params: String): String {
        val url = URL(params[0])
        val urlConnection = url.openConnection() as HttpURLConnection

        try {
            urlConnection.requestMethod = "GET"
            val responseCode = urlConnection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read and process the response
                val inputStream: InputStream = urlConnection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = reader.readText()
                println("Response from server: $response")
                return response
            }

            //responseCode == HttpURLConnection.HTTP_OK

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            urlConnection.disconnect()
        }
        return ""
    }

    override fun onPostExecute(response: String) {
        if (response != null) {
            Log.d("SendMessageTask", "Message Received successfully")
            //val jsonResponse = JSONObject(response)

            // Extract the "info_message" value
            //val infoMessage = jsonResponse.getString("info_message")
            // onPostExecuteListener?.invoke(infoMessage)
        } else {
            Log.e("SendMessageTask", "Failed to Received message")
        }
    }
}