package com.example.apptest

import android.Manifest
import android.util.Log
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.example.apptest.ui.theme.AppTestTheme
import android.widget.Button
import android.widget.TextView
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.result.ActivityResultLauncher
import android.app.Activity
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID
import android.os.AsyncTask
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity : ComponentActivity() {
    private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>
    private lateinit var nsdManager: NsdManager
    private lateinit var server: NanoHTTPD
    private lateinit var httpServer: AndroidHttpServer

    private val deviceDiscoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val textView: TextView = findViewById<TextView>(R.id.btList)
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {

                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME)

                if (deviceName != null) {
                    Log.d("Tag", "Discovered device: $deviceName")
                    val currentText = textView.text.toString() // Convert CharSequence to String
                    val updatedText =
                        "$currentText $deviceName, " // Concatenate the strings using string templates
                    textView.text = updatedText
                    if (deviceName == "AR SPEAKER") {
                        Log.d("Tag", "ATTEMPTING CONNECTION PROCESS")
                        initiateConnectionToDevice(device!!)
                       // initiatePairing(device!!)
                        Log.d("Tag", "POST ATTEMPTING CONNECTION PROCESS")
                    }
                }

            }
        }
    }

    private fun initiatePairing(device: BluetoothDevice) {
        device.let {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Tag", "CHECKING BLUETOOTH CONNECTION ACCESS")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1
                )
            }
            if (device.bondState != BluetoothDevice.BOND_BONDED) {
                // Initiate pairing
                Log.d("Tag", "Initiating pairing for: ${device.name}")
                device.createBond()
            }
        }
    }

    private val pairingRequestReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Tag", "CHECKING BLUETOOTH CONNECTION ACCESS")
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1
                )
            }

            val action = intent?.action
            if (BluetoothDevice.ACTION_PAIRING_REQUEST == action) {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME)
                val variant = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, -1)

                Log.d("Tag", "Pairing request for: $deviceName")
                //if (deviceName == "AR SPEAKER") {
                Log.d("Tag", "ATTEMPTING PAIRING")
                device?.createBond()
                Log.d("Tag", "POST ATTEMPTING PAIRING")

            }
        }
    }

    private fun initiateConnectionToDevice(device: BluetoothDevice) {

        // Use the standard SPP UUID
        val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        val bluetoothSocket: BluetoothSocket

        try {
            Log.d("Tag", "TRY")
            // Create a BluetoothSocket using the device and SPP UUID
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Tag", "CHECKING BLUETOOTH CONNECTION ACCESS")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1
                )
            }
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_ADMIN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Tag", "CHECKING BLUETOOTH ADMIN ACCESS")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_ADMIN), 1
                )
            }
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Tag", "CHECKING BLUETOOTH ACCESS")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH), 1
                )
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(sppUuid)
            // Establish the Bluetooth connection (this might block the thread)
            try {
                bluetoothSocket.connect()
                // If the connection was successful, this code will be reached
                Log.d("Tag", "SUCCESSFUL!")
                Log.d("Tag", "INITIATE PAIRING")
                initiatePairing(device!!)
                Log.d("Tag", "POST INITIATING PAIRING")

                try {
                    Log.d("Tag", "CONNECTED: ${bluetoothSocket.isConnected}!")
                    // AVRCP command code for play
                    val avrcpPlayCommand = byteArrayOf(0x14, 0x00, 0x00, 0x00)
                    val avrcpPauseCommand = byteArrayOf(0x50, 0x00, 0x00, 0x00)
                    val outputStream = bluetoothSocket.outputStream
                    outputStream.write(avrcpPlayCommand)
                    outputStream.flush()
                    Log.d("Tag", "PAUSE!")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                // Proceed with communication or other tasks related to the successful connection
            } catch (e: IOException) {
                // Handle connection failure
                Log.d("Tag", "FAILED!")
                e.printStackTrace()
            } finally {
                // Always close the socket when you're done with it
                try {
                    Log.d("Tag", "CLOSING")
                    //bluetoothSocket.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        } catch (e: IOException) {
            // Connection failed, handle the error
            e.printStackTrace()
        }
    }
    private fun startBluetoothDiscovery() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {

            // Device doesn't support Bluetooth
            Log.d("Tag", "Bluetooth not supported")
        } else {
            Log.d("Tag", "predisc")
            if (bluetoothAdapter.isEnabled) {
                // Bluetooth is enabled, start discovery
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("Tag", "CHECKING BLUETOOTH ACCESS")

                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.BLUETOOTH_SCAN),1
                    )
                    Log.d("Tag", "RETURN")
                }

                bluetoothAdapter.startDiscovery()
                Log.d("Tag", "postdisc")
            } else {
                // Bluetooth is not enabled, request enabling it
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver to avoid memory leaks
        unregisterReceiver(deviceDiscoveryReceiver)
        unregisterReceiver(pairingRequestReceiver)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Tag", "afq3242qaef")
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(enableBtIntent)
        } else {
            // Bluetooth is already enabled
            // Do something if Bluetooth is already enabled
            Log.d("Tag", "DDDD")
        }

        val button: Button = findViewById<Button>(R.id.toggleButton)
        val btButton: Button = findViewById<Button>(R.id.BTActivate) // Find the button by its ID
        val httpButtonSend: Button = findViewById<Button>(R.id.httpButtonSend)
        val httpButtonReceive: Button = findViewById<Button>(R.id.httpButtonReceive)
        val textView: TextView = findViewById<TextView>(R.id.textView) // Find the TextView by its ID
        var flag: Boolean =  false

        // Initialize NsdManager
       // nsdManager = getSystemService(Context.NSD_SERVICE) as NsdManager

        // Set up the discovery listener
       // val discoveryListener = createDiscoveryListener()

        // Set up NanoHTTPD server
       // server = HttpServer()

        /*
        httpButton.setOnClickListener {
            Log.d("Tag", "http before")
            startServiceDiscovery(discoveryListener)
            Log.d("Tag", "http after")
        }*/
        // Set up a click listener for the button

        Thread {
            try {
                httpServer = AndroidHttpServer()
                httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()

        // Example of sending a POST request to the server
        val message = "Hello from Android!"
        val postUrl = "http://localhost:8080/"
        SendMessageTask().execute(postUrl, message)

        httpButtonSend.setOnClickListener {
            val broadcastIpAddress = "192.168.2.43"
            val raspberryPiUrl = "http://$broadcastIpAddress:5000/receive_message"
            val message = "Hello from Android!"

            val task = SendMessageTask()
            //task.setContentType("application/json")
            task.execute(raspberryPiUrl, message)
        }


        httpButtonReceive.setOnClickListener {
            val receiveInfoUrl = "http://192.168.2.43:5000/send_info"

            // Create an instance of ReceiveInfoTask and execute it
            val receiveInfoTask = ReceiveInfoTask()

            // Set an onPostExecute listener to handle the result
            receiveInfoTask.onPostExecuteListener = { message ->
                // Update UI with the result here
                textView.text = message
                Log.d("Tag", message)
            }
            receiveInfoTask.execute(receiveInfoUrl)

        }

        btButton.setOnClickListener{
            // Update the text of the TextView when the button is clicked
            Log.d("Tag", "AAAAAAAAAA")

            // Register the BroadcastReceiver
            registerReceiver(deviceDiscoveryReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
            registerReceiver(pairingRequestReceiver, IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST))
            // Start Bluetooth discovery
            // PUT THE PERMISSION CHECK HERE

            Log.d("Tag", "predisc")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Tag", "CHECKING LOCATION ACCESS")
                // ACCESS_FINE_LOCATION permission is not granted
                // Request the necessary permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1
                )
            } else {
                // ACCESS_FINE_LOCATION permission is granted, start Bluetooth discovery
                startBluetoothDiscovery()
            }

            Log.d("Tag", "654e")

        }

        // Set up a click listener for the button
        button.setOnClickListener {
            // Update the text of the TextView when the button is clicked
            Log.d("Tag", "AAAAAAAAAA")
            if (!flag){
                textView.text = "Button Clicked!"
                flag = true

            } else {
                textView.text = "Button Unclicked!"
                flag = false
            }
        }
    }


}


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
    var onPostExecuteListener: ((String) -> Unit)? = null

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
            val jsonResponse = JSONObject(response)

            // Extract the "info_message" value
            val infoMessage = jsonResponse.getString("info_message")
            onPostExecuteListener?.invoke(infoMessage)
        } else {
            Log.e("SendMessageTask", "Failed to Received message")
        }
    }
}

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
                return newFixedLengthResponse("Error handling POST request")
            }
        }
        return newFixedLengthResponse("Error handling POST request")
    }

    private fun handleGetRequest(session: IHTTPSession): Response {
        // Handle GET request
        if ("/Send" == session.uri) {
            return newFixedLengthResponse("Hello from NanoHTTPD")
        }
        return newFixedLengthResponse("Error handling GET request")
    }
}