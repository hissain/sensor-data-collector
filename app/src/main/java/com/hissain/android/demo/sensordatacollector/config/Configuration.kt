package com.hissain.android.demo.sensordatacollector.config

object Configuration {
    // App Constants
    const val APP_NAME = "HotFlash Detection"
    const val TARGET_AGE_MIN = 45
    const val TARGET_AGE_MAX = 55

    // Data Layer API Constants
    const val WEAR_DATA_PATH = "/hotflash_data"
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
    const val KEY_USER_PROFILE = "user_profile"
    const val KEY_SENSOR_CONFIG = "sensor_config"

    // File Constants
    const val DATA_FOLDER_NAME = "hotflash_data"
    const val HEART_RATE_FILE = "heart_rate_data.csv"
    const val CSV_HEADER_HEART_RATE = "timestamp,heart_rate,accuracy"

    // Sync Settings
    const val DEFAULT_SYNC_INTERVAL = 300000L // 5 minutes
    const val MIN_SYNC_INTERVAL = 60000L // 1 minute
    const val MAX_SYNC_INTERVAL = 3600000L // 1 hour

    // Sensor Settings
    const val DEFAULT_HEART_RATE_FREQUENCY = 1000L // 1 second
}