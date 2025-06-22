package com.hissain.android.demo.sensordatacollector.utils

import android.content.Context
import android.content.SharedPreferences
import com.hissain.android.demo.sensordatacollector.config.Configuration
import com.hissain.android.demo.sensordatacollector.model.UserProfile

class SharedPreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "sensor_data_prefs", Context.MODE_PRIVATE
    )

    fun saveUserProfile(profile: UserProfile) {
        prefs.edit().apply {
            putString("user_name", profile.name)
            putInt("user_age", profile.age)
            putString("user_sex", profile.sex)
            putFloat("user_height", profile.height)
            putFloat("user_weight", profile.weight)
            apply()
        }
    }

    fun getUserProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("user_name", "") ?: "",
            age = prefs.getInt("user_age", 0),
            sex = prefs.getString("user_sex", "") ?: "",
            height = prefs.getFloat("user_height", 0f),
            weight = prefs.getFloat("user_weight", 0f)
        )
    }

    fun setSyncInterval(interval: Long) {
        prefs.edit().putLong("sync_interval", interval).apply()
    }

    fun getSyncInterval(): Long {
        return prefs.getLong("sync_interval", Configuration.DEFAULT_SYNC_INTERVAL)
    }

    fun setHeartRateSensorEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("heart_rate_enabled", enabled).apply()
    }

    fun isHeartRateSensorEnabled(): Boolean {
        return prefs.getBoolean("heart_rate_enabled", true)
    }

    fun setAutoSyncEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("auto_sync_enabled", enabled).apply()
    }

    fun isAutoSyncEnabled(): Boolean {
        return prefs.getBoolean("auto_sync_enabled", true)
    }
}