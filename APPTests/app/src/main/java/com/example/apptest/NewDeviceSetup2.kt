package com.example.apptest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.apptest.databinding.ActivityNewDeviceSetup2Binding

class NewDeviceSetup2 : AppCompatActivity() {

    //private lateinit var wifiScanner: WifiScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_device_setup2)
        Log.d("Tag", "afq3242qaef")

        val nextButton: Button = findViewById(R.id.nextButton)
        val editText1: EditText = findViewById(R.id.plugName)

        nextButton.setOnClickListener {
            //val broadcastIpAddress = "192.168.2.20"
            val broadcastIpAddress = "192.168.2.43"
            //val broadcastIpAddress = "172.18.148.119"
            val raspberryPiUrl = "http://$broadcastIpAddress:5000/receive_message"
            val value1 = editText1.text.toString()
            val message = "Hello from Android!"

            val task = SendMessageTask()
            //task.setContentType("application/json")
            task.execute(raspberryPiUrl, value1)

            Toast.makeText(this, "Plug added successfully! (Returning to main page shortly...)", Toast.LENGTH_SHORT).show()
            Thread.sleep(5000)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}