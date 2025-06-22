package com.hissain.android.demo.sensordatacollector.utils

import android.content.Context
import android.os.Environment
import com.hissain.android.demo.sensordatacollector.config.Configuration
import com.hissain.android.demo.sensordatacollector.model.HeartRateData
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FileManager(private val context: Context) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())

    fun getDataDirectory(): File {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val dataDir = File(downloadsDir, Configuration.DATA_FOLDER_NAME)
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }
        return dataDir
    }

    fun saveHeartRateData(data: List<HeartRateData>): Boolean {
        return try {
            val dataDir = getDataDirectory()
            val timestamp = dateFormat.format(Date())
            val fileName = "heart_rate_$timestamp.csv"
            val file = File(dataDir, fileName)

            FileWriter(file).use { writer ->
                writer.append(Configuration.CSV_HEADER_HEART_RATE).append("\n")
                data.forEach { heartRateData ->
                    writer.append(heartRateData.toCsvRow()).append("\n")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun getExistingDataFiles(): List<File> {
        val dataDir = getDataDirectory()
        return dataDir.listFiles { file ->
            file.isFile && file.name.endsWith(".csv")
        }?.toList() ?: emptyList()
    }

    fun clearAllData(): Boolean {
        return try {
            val dataDir = getDataDirectory()
            dataDir.listFiles()?.forEach { file ->
                if (file.isFile) {
                    file.delete()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}