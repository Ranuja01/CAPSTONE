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
import android.widget.EditText
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


