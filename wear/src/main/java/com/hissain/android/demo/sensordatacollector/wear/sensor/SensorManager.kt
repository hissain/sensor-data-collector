package com.hissain.android.demo.sensordatacollector.wear.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.hissain.android.demo.sensordatacollector.wear.config.Configuration
import com.hissain.android.demo.sensordatacollector.wear.model.HeartRateData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class HeartRateSensorManager(
    private val context: Context
) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val heartRateSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    private val _heartRateData = MutableSharedFlow<HeartRateData>()
    val heartRateData: SharedFlow<HeartRateData> = _heartRateData

    private var isListening = false
    private var samplingFrequency = Configuration.DEFAULT_HEART_RATE_FREQUENCY

    companion object {
        private const val TAG = "HeartRateSensorManager"
    }

    fun isHeartRateSensorAvailable(): Boolean {
        return heartRateSensor != null
    }

    fun startListening(frequency: Long = Configuration.DEFAULT_HEART_RATE_FREQUENCY): Boolean {
        if (!isHeartRateSensorAvailable()) {
            Log.e(TAG, "Heart rate sensor not available")
            return false
        }

        if (isListening) {
            Log.w(TAG, "Already listening to heart rate sensor")
            return true
        }

        samplingFrequency = frequency
        val samplingPeriodUs = (frequency * 1000).toInt() // Convert ms to microseconds

        val success = sensorManager.registerListener(
            this,
            heartRateSensor,
            samplingPeriodUs
        )

        if (success) {
            isListening = true
            Log.d(TAG, "Started listening to heart rate sensor with frequency: ${frequency}ms")
        } else {
            Log.e(TAG, "Failed to register heart rate sensor listener")
        }

        return success
    }

    fun stopListening() {
        if (isListening) {
            sensorManager.unregisterListener(this)
            isListening = false
            Log.d(TAG, "Stopped listening to heart rate sensor")
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            if (sensorEvent.sensor.type == Sensor.TYPE_HEART_RATE) {
                val heartRate = sensorEvent.values[0]
                val timestamp = System.currentTimeMillis()
                val accuracy = sensorEvent.accuracy

                if (heartRate > 0) { // Valid heart rate reading
                    val data = HeartRateData(timestamp, heartRate, accuracy)
                    _heartRateData.tryEmit(data)
                    Log.d(TAG, "Heart rate: $heartRate BPM, Accuracy: $accuracy")
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Sensor accuracy changed: $accuracy")
    }

    fun isListening(): Boolean = isListening

    fun setSamplingFrequency(frequency: Long) {
        if (frequency != samplingFrequency && isListening) {
            stopListening()
            startListening(frequency)
        } else {
            samplingFrequency = frequency
        }
    }
}