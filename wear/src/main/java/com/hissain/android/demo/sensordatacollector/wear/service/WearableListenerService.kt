package com.hissain.android.demo.sensordatacollector.wear.service

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.*
import com.hissain.android.demo.sensordatacollector.wear.config.Configuration
import com.hissain.android.demo.sensordatacollector.wear.storage.DataStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WearableListenerService : com.google.android.gms.wearable.WearableListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var dataStorage: DataStorage

    companion object {
        private const val TAG = "WearableListenerService"
    }

    override fun onCreate() {
        super.onCreate()
        dataStorage = DataStorage(this)
        Log.d(TAG, "WearableListenerService created")
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        when (messageEvent.path) {
            Configuration.COMMAND_PATH -> {
                val command = String(messageEvent.data)
                Log.d(TAG, "Received command: $command")
                handleCommand(command)
            }
        }
    }

    private fun handleCommand(command: String) {
        when (command) {
            Configuration.CMD_START_COLLECTION -> {
                startDataCollection()
            }
            Configuration.CMD_STOP_COLLECTION -> {
                stopDataCollection()
            }
            Configuration.CMD_SYNC_DATA -> {
                syncDataToPhone()
            }
            Configuration.CMD_RESET_DATA -> {
                resetData()
            }
        }
    }

    private fun startDataCollection() {
        Log.d(TAG, "Starting data collection service")
        DataCollectionService.startService(this)

        // Send status update to phone
        sendStatusToPhone("Data collection started")
    }

    private fun stopDataCollection() {
        Log.d(TAG, "Stopping data collection service")
        DataCollectionService.stopService(this)

        // Send status update to phone
        sendStatusToPhone("Data collection stopped")
    }

    private fun syncDataToPhone() {
        Log.d(TAG, "Syncing data to phone")

        serviceScope.launch {
            try {
                val heartRateData = dataStorage.getHeartRateDataAsString()

                // Create data item for sync
                val putDataMapReq = PutDataMapRequest.create(Configuration.WEAR_DATA_PATH)
                putDataMapReq.dataMap.apply {
                    putString(Configuration.KEY_HEART_RATE, heartRateData)
                    putLong(Configuration.KEY_TIMESTAMP, System.currentTimeMillis())
                }

                val putDataReq = putDataMapReq.asPutDataRequest()
                val dataClient = Wearable.getDataClient(this@WearableListenerService)

                dataClient.putDataItem(putDataReq).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Data synced successfully to phone")
                        sendStatusToPhone("Data synced successfully")
                    } else {
                        Log.e(TAG, "Failed to sync data to phone", task.exception)
                        sendStatusToPhone("Failed to sync data")
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error during data sync", e)
                sendStatusToPhone("Error during sync")
            }
        }
    }

    private fun resetData() {
        Log.d(TAG, "Resetting all data")

        serviceScope.launch {
            val success = dataStorage.clearAllData()
            val message = if (success) "Data reset successfully" else "Failed to reset data"
            sendStatusToPhone(message)
        }
    }

    private fun sendStatusToPhone(status: String) {
        val messageClient = Wearable.getMessageClient(this)
        val nodeClient = Wearable.getNodeClient(this)

        nodeClient.connectedNodes.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val nodes = task.result
                nodes.forEach { node ->
                    messageClient.sendMessage(
                        node.id,
                        Configuration.STATUS_PATH,
                        status.toByteArray()
                    )
                }
            }
        }
    }
}