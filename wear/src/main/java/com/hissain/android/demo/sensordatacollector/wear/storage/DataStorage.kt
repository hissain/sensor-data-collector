package com.hissain.android.demo.sensordatacollector.wear.storage

import android.content.Context
import android.util.Log
import com.hissain.android.demo.sensordatacollector.wear.config.Configuration
import com.hissain.android.demo.sensordatacollector.wear.model.HeartRateData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException

class DataStorage(private val context: Context) {

    private val dataDir: File by lazy {
        File(context.filesDir, "sensor_data").apply {
            if (!exists()) mkdirs()
        }
    }

    companion object {
        private const val TAG = "DataStorage"
    }

    suspend fun saveHeartRateData(data: HeartRateData): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(dataDir, Configuration.HEART_RATE_FILE)
            val isNewFile = !file.exists()

            FileWriter(file, true).use { writer ->
                if (isNewFile) {
                    writer.append(Configuration.CSV_HEADER_HEART_RATE).append("\n")
                }
                writer.append(data.toCsvRow()).append("\n")
            }

            Log.d(TAG, "Saved heart rate data: ${data.heartRate} BPM")
            true
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save heart rate data", e)
            false
        }
    }

    suspend fun getAllHeartRateData(): List<HeartRateData> = withContext(Dispatchers.IO) {
        try {
            val file = File(dataDir, Configuration.HEART_RATE_FILE)
            if (!file.exists()) return@withContext emptyList()

            val lines = file.readLines()
            val dataLines = if (lines.isNotEmpty() && lines[0].contains("timestamp")) {
                lines.drop(1) // Skip header
            } else {
                lines
            }

            dataLines.mapNotNull { line ->
                HeartRateData.fromCsvRow(line)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read heart rate data", e)
            emptyList()
        }
    }

    suspend fun getHeartRateDataCount(): Int = withContext(Dispatchers.IO) {
        try {
            val file = File(dataDir, Configuration.HEART_RATE_FILE)
            if (!file.exists()) return@withContext 0

            val lines = file.readLines()
            // Subtract 1 for header if present
            if (lines.isNotEmpty() && lines[0].contains("timestamp")) {
                lines.size - 1
            } else {
                lines.size
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to count heart rate data", e)
            0
        }
    }

    suspend fun clearAllData(): Boolean = withContext(Dispatchers.IO) {
        try {
            dataDir.listFiles()?.forEach { file ->
                if (file.isFile) {
                    file.delete()
                }
            }
            Log.d(TAG, "Cleared all sensor data")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear data", e)
            false
        }
    }

    suspend fun getHeartRateDataAsString(): String = withContext(Dispatchers.IO) {
        try {
            val file = File(dataDir, Configuration.HEART_RATE_FILE)
            if (file.exists()) {
                file.readText()
            } else {
                Configuration.CSV_HEADER_HEART_RATE + "\n"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read heart rate data as string", e)
            Configuration.CSV_HEADER_HEART_RATE + "\n"
        }
    }
}