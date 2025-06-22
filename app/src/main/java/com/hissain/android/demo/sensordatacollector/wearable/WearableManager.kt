package com.hissain.android.demo.sensordatacollector.wearable

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import com.hissain.android.demo.sensordatacollector.config.Configuration
import com.hissain.android.demo.sensordatacollector.model.CollectionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WearableManager(private val context: Context) {

    private val dataClient: DataClient = Wearable.getDataClient(context)
    private val messageClient: MessageClient = Wearable.getMessageClient(context)
    private val nodeClient: NodeClient = Wearable.getNodeClient(context)

    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus

    private val _collectionStatus = MutableStateFlow(CollectionStatus())
    val collectionStatus: StateFlow<CollectionStatus> = _collectionStatus

    companion object {
        private const val TAG = "WearableManager"
    }

    fun sendCommand(command: String): Task<Int> {
        return nodeClient.connectedNodes.continueWithTask { task ->
            val nodes = task.result
            if (nodes.isNotEmpty()) {
                val nodeId = nodes.first().id
                messageClient.sendMessage(nodeId, Configuration.COMMAND_PATH, command.toByteArray())
            } else {
                throw Exception("No connected wearable device found")
            }
        }
    }

    fun startDataCollection(): Task<Int> {
        Log.d(TAG, "Starting data collection")
        return sendCommand(Configuration.CMD_START_COLLECTION)
    }

    fun stopDataCollection(): Task<Int> {
        Log.d(TAG, "Stopping data collection")
        return sendCommand(Configuration.CMD_STOP_COLLECTION)
    }

    fun syncData(): Task<Int> {
        Log.d(TAG, "Syncing data")
        return sendCommand(Configuration.CMD_SYNC_DATA)
    }

    fun resetData(): Task<Int> {
        Log.d(TAG, "Resetting data")
        return sendCommand(Configuration.CMD_RESET_DATA)
    }

    fun checkConnection() {
        nodeClient.connectedNodes.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val nodes = task.result
                _connectionStatus.value = nodes.isNotEmpty()
                Log.d(TAG, "Connected nodes: ${nodes.size}")
            } else {
                _connectionStatus.value = false
                Log.e(TAG, "Failed to get connected nodes", task.exception)
            }
        }
    }

    fun updateCollectionStatus(status: CollectionStatus) {
        _collectionStatus.value = status
    }
}