package com.hissain.android.demo.sensordatacollector.wear.config

object Configuration {
    // App Constants
    const val APP_NAME = "Sensor Data Collector"

    // Data Layer API Constants
    const val WEAR_DATA_PATH = "/sensor_data"
    const val COMMAND_PATH = "/commands"
    const val STATUS_PATH = "/status"

    // Commands
    const val CMD_START_COLLECTION = "start_collection"
    const val CMD_STOP_COLLECTION = "stop_collection"
    const val CMD_SYNC_DATA = "sync_data"
    const val CMD_RESET_DATA = "reset_data"

    // Data Keys
    const val KEY_HEART_RATE = "heart_rate"
    const val KEY_TIMESTAMP = "timestamp"
    const val KEY_SENSOR_CONFIG = "sensor_config"

    // File Constants
    const val HEART_RATE_FILE = "heart_rate_data.csv"
    const val CSV_HEADER_HEART_RATE = "timestamp,heart_rate,accuracy"

    // Sensor Settings
    const val DEFAULT_HEART_RATE_FREQUENCY = 1000L // 1 second
    const val NOTIFICATION_ID = 1001
    const val NOTIFICATION_CHANNEL_ID = "data_collection_channel"
}