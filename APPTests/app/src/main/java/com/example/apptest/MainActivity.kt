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
    //private lateinit var nsdManager: NsdManager
    //private lateinit var httpServer: AndroidHttpServer-

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("Tag", "Start Main")

        // Create file on the mobile device for storing plug IP address
        val myFileManager = FileManager(this,"data.txt")

        val toggle: Button = findViewById<Button>(R.id.toggle) // Button for toggling device
        val deviceSetup: Button = findViewById<Button>(R.id.deviceSetup) // Button to trigger device setup
        val schedule: Button = findViewById<Button>(R.id.schedulingButton) // Button to trigger scheduling
        val graphing: Button = findViewById<Button>(R.id.graphButton) // Button to trigger current graphing
        //val tcpButton: Button = findViewById<Button>(R.id.TCPButton)
        //val httpButtonSend: Button = findViewById<Button>(R.id.httpButtonSend)
        //val httpButtonReceive: Button = findViewById<Button>(R.id.httpButtonReceive)
        //val textView: TextView = findViewById<TextView>(R.id.textView)
        //var flag: Boolean =  false

        // Start the HTTP Server
        HttpServerManager.startServer()

        // Schedule button listener
        schedule.setOnClickListener {
            val intent = Intent(this, ScheduleDevice::class.java)
            startActivity(intent)
        }

        // Graph button listener
        graphing.setOnClickListener {
            val intent = Intent(this, GraphActivity::class.java)
            startActivity(intent)
        }

        // Device setup listener
        deviceSetup.setOnClickListener {
            val intent = Intent(this, NewDeviceSetup1::class.java)
            startActivity(intent)
        }

        // Device toggle button listener
        toggle.setOnClickListener {

            // Read the data containing the ip address of the plug
            val dataRead = myFileManager.readData()
            if (dataRead != "null") { // Check if the read data is not null
                Log.d("Tag","Data read from file: $dataRead")
            } else {
                Log.d("Tag","No data found in file")
                var dataToSave = "192.168.2.121"
                myFileManager.saveData(dataToSave)
            }
            // Create the toggle request
            var broadcastIpAddress = myFileManager.readData()
            val receiveInfoUrl = "http://$broadcastIpAddress/cm?cmnd=Power%20TOGGLE"
            //192.168.2.121 - My House
            // 192.168.1.102 - Andrew's House http://PicoPlug/cm?cmnd=Power%20TOGGLE

            // Create an instance of ReceiveInfoTask and execute it
            val receiveInfoTask = ReceiveInfoTask()
            receiveInfoTask.execute(receiveInfoUrl)

        }

     /*   httpButtonSend.setOnClickListener {
            val broadcastIpAddress = "192.168.2.43"
            val raspberryPiUrl = "http://$broadcastIpAddress:5000/receive_message"
            val message = "Hello from Android!"

            val task = SendMessageTask()
            //task.setContentType("application/json")
            task.execute(raspberryPiUrl, message)
        }
*/

/*
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
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver to avoid memory leaks
        //unregisterReceiver(deviceDiscoveryReceiver)
        //unregisterReceiver(pairingRequestReceiver)

    }


}


