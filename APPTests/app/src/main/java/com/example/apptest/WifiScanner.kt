package com.example.apptest

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.app.ActivityCompat


class WifiScanner(private val context: Context, private val listView: ListView) {

    private val wifiManager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val arrayAdapter: ArrayAdapter<String> =
        ArrayAdapter(context, android.R.layout.simple_list_item_1)

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION == intent.action) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                }
                val scanResults = wifiManager.scanResults
                updateWifiList(scanResults)
            }
        }
    }

    init {
        listView.adapter = arrayAdapter

        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }

        //scanWifi()
    }

    fun onResume() {
        context.registerReceiver(wifiScanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    fun onPause() {
        context.unregisterReceiver(wifiScanReceiver)
    }

    fun scanWifi() {
        wifiManager.startScan()
    }

    private val filterString = "Bell"

    private fun updateWifiList(scanResults: List<ScanResult>) {
        arrayAdapter.clear()

        for (result in scanResults) {
            if (result.SSID.contains(filterString, ignoreCase = true)) {
                // Add only those SSIDs that contain the specific string
                arrayAdapter.add(result.SSID)
            }
        }
    }
}
