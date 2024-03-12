package com.example.apptest

import android.content.Intent
import android.util.Log
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class NewDeviceSetup1 : ComponentActivity() {

    //private lateinit var wifiScanner: WifiScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_device_setup)
        Log.d("Tag", "afq3242qaef")

        val nextButton: Button = findViewById(R.id.nextButton)
        val editText1: EditText = findViewById(R.id.ssid)
        val editText2: EditText = findViewById(R.id.pass)
/*
        nextButton.setOnClickListener{
            val value1 = editText1.text.toString()
            HttpServerManager.updateMessageContent(value1)
        }
*/
        nextButton.setOnClickListener {
            //val broadcastIpAddress = "192.168.2.20"
            val broadcastIpAddress = "192.168.2.43"
            //val broadcastIpAddress = "172.18.148.119"
            val raspberryPiUrl = "http://$broadcastIpAddress:5000/receive_message"
            val value1 = editText1.text.toString()
            val value2 = editText2.text.toString()
            val tcpClient = TcpClient()
            tcpClient.sendMessage("SSID $value1", "192.168.4.1", 50000)
            Thread.sleep(250)
            tcpClient.sendMessage("PASS $value2", "192.168.4.1", 50000)
            Log.d("Tag", "CC")

            Thread.sleep(3000)
            val intent = Intent(this, NewDeviceSetup2::class.java)
            startActivity(intent)
        }



/*
        nextButton.setOnClickListener {
            //val broadcastIpAddress = "192.168.2.20"
            val broadcastIpAddress = "192.168.2.43"
            //val broadcastIpAddress = "172.18.148.119"
            val raspberryPiUrl = "http://$broadcastIpAddress:5000/receive_message"
            val value1 = editText1.text.toString()
            val value2 = editText2.text.toString()
            val message = "Hello from Android!"

            val task = SendMessageTask()
            //task.setContentType("application/json")
            task.execute(raspberryPiUrl, "$value1, $value2")

            Thread.sleep(3000)
            val intent = Intent(this, NewDeviceSetup2::class.java)
            startActivity(intent)
        }*/



    }

}


