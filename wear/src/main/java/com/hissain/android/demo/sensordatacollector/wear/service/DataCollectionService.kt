package com.hissain.android.demo.sensordatacollector.wear.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.hissain.android.demo.sensordatacollector.wear.R
import com.hissain.android.demo.sensordatacollector.wear.config.Configuration
import com.hissain.android.demo.sensordatacollector.wear.model.CollectionStatus
import com.hissain.android.demo.sensordatacollector.wear.sensor.HeartRateSensorManager
import com.hissain.android.demo.sensordatacollector.wear.storage.DataStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DataCollectionService : Service() {

    private lateinit var heartRateSensorManager: HeartRateSensorManager
    private lateinit var dataStorage: DataStorage
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _collectionStatus = MutableStateFlow(CollectionStatus())
    val collectionStatus: StateFlow<CollectionStatus> = _collectionStatus

    private var isCollecting = false
    private var recordCount = 0

    companion object {
        private const val TAG = "DataCollectionService"

        fun startService(context: Context) {
            val intent = Intent(context, DataCollectionService::class.java)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, DataCollectionService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        heartRateSensorManager = HeartRateSensorManager(this)
        dataStorage = DataStorage(this)

        createNotificationChannel()
        startForeground(Configuration.NOTIFICATION_ID, createNotification())

        // Initialize record count
        serviceScope.launch {
            recordCount = dataStorage.getHeartRateDataCount()
            updateStatus()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        stopDataCollection()
        serviceScope.cancel()
    }

    fun startDataCollection(): Boolean {
        if (isCollecting) {
            Log.w(TAG, "Data collection already in progress")
            return true
        }

        if (!heartRateSensorManager.isHeartRateSensorAvailable()) {
            Log.e(TAG, "Heart rate sensor not available")
            updateStatus(sensorStatus = "Heart rate sensor not available")
            return false
        }

        val success = heartRateSensorManager.startListening()
        if (success) {
            isCollecting = true
            updateStatus(isCollecting = true, sensorStatus = "Collecting data...")

            // Collect heart rate data
            serviceScope.launch {
                heartRateSensorManager.heartRateData.collect { heartRateData ->
                    dataStorage.saveHeartRateData(heartRateData)
                    recordCount++
                    updateStatus()

                    // Update notification
                    val notification = createNotification("Collecting: ${heartRateData.heartRate} BPM")
                    startForeground(Configuration.NOTIFICATION_ID, notification)
                }
            }

            Log.d(TAG, "Data collection started")
        } else {
            updateStatus(sensorStatus = "Failed to start heart rate sensor")
        }

        return success
    }

    fun stopDataCollection() {
        if (!isCollecting) {
            Log.w(TAG, "Data collection not in progress")
            return
        }

        heartRateSensorManager.stopListening()
        isCollecting = false
        updateStatus(isCollecting = false, sensorStatus = "Stopped")

        // Update notification
        val notification = createNotification("Data collection stopped")
        startForeground(Configuration.NOTIFICATION_ID, notification)

        Log.d(TAG, "Data collection stopped")
    }

    private fun updateStatus(
        isCollecting: Boolean = this.isCollecting,
        sensorStatus: String = if (isCollecting) "Collecting data..." else "Ready"
    ) {
        val status = CollectionStatus(
            isCollecting = isCollecting,
            lastSyncTime = System.currentTimeMillis(),
            totalRecords = recordCount,
            batteryLevel = getBatteryLevel(),
            sensorStatus = sensorStatus
        )
        _collectionStatus.value = status
    }

    private fun getBatteryLevel(): Int {
        // Simplified battery level - in real implementation, use BatteryManager
        return 100
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Configuration.NOTIFICATION_CHANNEL_ID,
                "Data Collection",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows data collection status"
                setSound(null, null)
                enableVibration(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(contentText: String = "Ready to collect data"): Notification {
        return NotificationCompat.Builder(this, Configuration.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("HotFlash Data Collection")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.splash_icon)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }
}