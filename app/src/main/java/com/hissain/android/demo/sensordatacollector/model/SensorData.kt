package com.hissain.android.demo.sensordatacollector.model

data class HeartRateData(
    val timestamp: Long,
    val heartRate: Float,
    val accuracy: Int
) {
    fun toCsvRow(): String {
        return "$timestamp,$heartRate,$accuracy"
    }
}

data class CollectionStatus(
    val isCollecting: Boolean = false,
    val lastSyncTime: Long = 0L,
    val totalRecords: Int = 0,
    val batteryLevel: Int = 100
)
