package com.hissain.android.demo.sensordatacollector.wear.model

data class HeartRateData(
    val timestamp: Long,
    val heartRate: Float,
    val accuracy: Int
) {
    fun toCsvRow(): String {
        return "$timestamp,$heartRate,$accuracy"
    }

    companion object {
        fun fromCsvRow(csvRow: String): HeartRateData? {
            return try {
                val parts = csvRow.split(",")
                if (parts.size == 3) {
                    HeartRateData(
                        timestamp = parts[0].toLong(),
                        heartRate = parts[1].toFloat(),
                        accuracy = parts[2].toInt()
                    )
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }
}

data class CollectionStatus(
    val isCollecting: Boolean = false,
    val lastSyncTime: Long = 0L,
    val totalRecords: Int = 0,
    val batteryLevel: Int = 100,
    val sensorStatus: String = "Ready"
)