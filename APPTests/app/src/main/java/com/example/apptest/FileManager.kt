package com.example.apptest

import android.content.Context
import java.io.File

// File manager class to create local files on the mobile device
class FileManager(private val context: Context, private val fileName: String) {

    fun saveData(data: String) {
        val file = File(context.filesDir, fileName)
        file.writeText(data)
    }

    fun readData(): String {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            file.readText()
        } else {
            ""
        }
    }
}

