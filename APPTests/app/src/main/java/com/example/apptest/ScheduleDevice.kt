package com.example.apptest

import android.content.Intent
import android.util.Log
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.app.TimePickerDialog
import java.util.Calendar

// Class for creating a device schedule
class ScheduleDevice : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_device)
        Log.d("Tag", "SCHEDULE")

        val editText1: EditText = findViewById(R.id.startTime)
        val editText2: EditText = findViewById(R.id.endTime)
        val nextButton: Button = findViewById(R.id.nextButton)

        // Show time picker dialog when editText1 is clicked
        editText1.setOnClickListener {
            showTimePicker(editText1)
        }

        // Show time picker dialog when editText2 is clicked
        editText2.setOnClickListener {
            showTimePicker(editText2)
        }

        // Send the schedule to the microcontroller
        nextButton.setOnClickListener {
            val value1 = editText1.text.toString()
            val value2 = editText2.text.toString()
            val tcpClient = TcpClient()
            tcpClient.sendMessage("START $value1", "192.168.1.111", 50000)
            Thread.sleep(250)
            tcpClient.sendMessage("END $value2", "192.168.1.111", 50000)
            Log.d("Tag", "CC")

            Thread.sleep(3000)
            val intent = Intent(this, NewDeviceSetup2::class.java)
            startActivity(intent)
        }
    }

    // Time picker
    private fun showTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                editText.setText(time)
            },
            hour,
            minute,
            true // Set to true for 24-hour format
        )

        timePickerDialog.show()
    }

}


