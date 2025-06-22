package com.hissain.android.demo.sensordatacollector.service

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService

class WearableDataService : WearableListenerService() {

    companion object {
        private const val TAG = "WearableDataService"
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                Log.d(TAG, "Data changed: ${dataItem.uri}")
                // Process the data item here
            } else if (event.type == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "Data deleted: ${event.dataItem.uri}")
            }
        }
    }
}