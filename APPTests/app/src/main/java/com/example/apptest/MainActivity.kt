package com.example.apptest

import android.Manifest
import android.util.Log
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.UUID
import fi.iki.elonen.NanoHTTPD


class MainActivity : ComponentActivity() {
    private lateinit var nsdManager: NsdManager
    private lateinit var server: NanoHTTPD
    //private lateinit var httpServer: AndroidHttpServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Tag", "afq3242qaef")

        val button: Button = findViewById<Button>(R.id.toggleButton)
        val tcpButton: Button = findViewById<Button>(R.id.TCPButton) // Find the button by its ID
        val httpButtonSend: Button = findViewById<Button>(R.id.httpButtonSend)
        val httpButtonReceive: Button = findViewById<Button>(R.id.httpButtonReceive)
        val textView: TextView = findViewById<TextView>(R.id.textView) // Find the TextView by its ID
        val deviceSetup: Button = findViewById<Button>(R.id.deviceSetup)
        val graphing: Button = findViewById<Button>(R.id.graphButton)
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

        HttpServerManager.startServer(object : MessageUpdateCallback {
            override fun onMessageUpdated(newContent: String) {
                // Handle the updated message content here
                println("Updated message content: $newContent")
            }
        })

        graphing.setOnClickListener {
            val intent = Intent(this, GraphActivity::class.java)
            startActivity(intent)
        }

        deviceSetup.setOnClickListener {
            val intent = Intent(this, NewDeviceSetup1::class.java)
            startActivity(intent)
        }

        httpButtonSend.setOnClickListener {
            val broadcastIpAddress = "192.168.2.43"
            val raspberryPiUrl = "http://$broadcastIpAddress:5000/receive_message"
            val message = "Hello from Android!"

            val task = SendMessageTask()
            //task.setContentType("application/json")
            task.execute(raspberryPiUrl, message)
        }


        httpButtonReceive.setOnClickListener {
            //val receiveInfoUrl = "http://192.168.2.43:5000/send_info"
            val receiveInfoUrl = "http://PicoPlug/cm?cmnd=Power%20TOGGLE"
            //192.168.2.121 - My House
            // 192.168.1.102 - Andrew's House
            //val receiveInfoUrl = "http://ezplug-8b4df5-3573/cm?cmnd=Power%20TOGGLE"
            // Create an instance of ReceiveInfoTask and execute it
            val receiveInfoTask = ReceiveInfoTask()

            // Set an onPostExecute listener to handle the result
/*
            receiveInfoTask.onPostExecuteListener = { message ->
                // Update UI with the result here
                textView.text = message
                Log.d("Tag", message)
            }

            */

            receiveInfoTask.execute(receiveInfoUrl)

        }

        tcpButton.setOnClickListener{
            // Update the text of the TextView when the button is clicked
            Log.d("Tag", "AA")
            val tcpClient = TcpClient()
            tcpClient.sendMessage("graph", "192.168.1.111", 50000)
            Log.d("Tag", "CC")



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

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver to avoid memory leaks
        //unregisterReceiver(deviceDiscoveryReceiver)
        //unregisterReceiver(pairingRequestReceiver)

    }


}


