package com.example.apptest

import android.util.Log
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.widget.TextView
import java.io.IOException
import java.util.UUID
import android.os.AsyncTask
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast

class NewDeviceSetup : ComponentActivity() {

    //private lateinit var wifiScanner: WifiScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_device_setup)
        Log.d("Tag", "afq3242qaef")

        val button: Button = findViewById<Button>(R.id.ScanWifi)
        //val listView: ListView = findViewById(R.id.listView)
       // wifiScanner = WifiScanner(this, listView)

        val editText1: EditText = findViewById(R.id.editText1)
        val editText2: EditText = findViewById(R.id.editText2)
        val submitButton: Button = findViewById(R.id.submitButton)
        val sendCustomMessage: Button = findViewById(R.id.customMessageButton)

        submitButton.setOnClickListener {
            val value1 = editText1.text.toString()
            val value2 = editText2.text.toString()

            // Do something with the user input, for example, display a Toast
            val message = "Value 1: $value1, Value 2: $value2"
            HttpServerManager.updateMessageContent(value1)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        button.setOnClickListener {
            Log.d("Tag", "before wifi scan")
             // Replace with your actual ListView ID
            //wifiScanner.scanWifi()

            Log.d("Tag", "after wifi scan")
        }

        sendCustomMessage.setOnClickListener {
            val broadcastIpAddress = "192.168.2.20"
            val raspberryPiUrl = "http://$broadcastIpAddress:5000/receive_message"
            val value1 = editText1.text.toString()
            val message = "Hello from Android!"

            val task = SendMessageTask()
            //task.setContentType("application/json")
            task.execute(raspberryPiUrl, value1)
        }


    }

    override fun onResume() {
        super.onResume()
        //wifiScanner.onResume()
    }

    override fun onPause() {
        super.onPause()
        //wifiScanner.onPause()
    }


}


