package com.example.apptest

import android.util.Log
import fi.iki.elonen.NanoHTTPD
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.concurrent.CountDownLatch

class TcpClient {

    fun sendMessage(message: String, ipAddress: String, port: Int) {

        var currentValue: Double? = null
        Thread {
            try {
                Log.d("Tag", "BB")
                // Create a socket with the given IP address and port number
                val socket = Socket(ipAddress, port)

                // Get the output stream of the socket
                val outputStream = socket.getOutputStream()

                // Write the message to the output stream
                val writer = OutputStreamWriter(outputStream)
                writer.write(message)
                writer.flush()

                while (true) {
                    Log.d("Tag", "BB")
                    // Read the response from the server
                    val inputStream = socket.getInputStream()
                    Log.d("Tag", "DD1")
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    Log.d("Tag", "DD2")
                    val response = reader.readLine()
                    Log.d("TcpClient", "Response line $response")
                    //currentValue = response?.substringAfter("Current:")?.trim()?.toDoubleOrNull()
                    dataObject.set(response.toFloat())
                    if (response == "FIN"){

                        socket.close()
                        break
                    }
                }

                /*


                 val responseStringBuilder = StringBuilder()
                var char: Int
                while (reader.read().also { char = it } != -1) {
                    responseStringBuilder.append(char.toChar())
                    // Check if the received character indicates the end of the response
                    if (char.toChar() == '\n') {
                        break
                    }
                }
                val response = responseStringBuilder.toString().trim()
                 */


                Log.d("Tag", "DD3")
                // Assuming 'response' is a list of strings
               /* response.forEachIndexed { index, line ->
                    Log.d("TcpClient", "Response line $index: $line")

                }*/

                Log.d("Tag", "DD")



            } catch (e: Exception) {
                Log.d("Tag", "EE")
                e.printStackTrace()
            }
        }.start()
    }

    fun close(socket : Socket){

        // Close the socket
        socket.close()
    }

}

object dataObject {
    var curData: Float? = null

    fun set(value: Float) {
        curData = value
    }

    fun reset() {
        curData = null
    }

    fun isNull(): Boolean {
        return curData == null
    }


}