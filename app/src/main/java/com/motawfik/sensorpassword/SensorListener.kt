package com.motawfik.sensorpassword

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log

class SensorListener : SensorEventListener {

    private var lastShake = System.currentTimeMillis()
    private val SHAKE_SLOP  = 280
    private var count = 0

    override fun onSensorChanged(event: SensorEvent?) {
        val maxRange = event!!.sensor.maximumRange
        val xValue = event.values[0]
        if (xValue >= maxRange * 0.15) {
            val now = System.currentTimeMillis()
            if (now <= lastShake + SHAKE_SLOP) { // ignore shake events that are too close to each other - (280 milliseconds difference)
                return
            }
            Log.d("SENSOR_VALUE_CHANGE", event.toString())
            Log.d("SENSOR_VALUE_CHANGE", event.values[0].toString())
            Log.d("SENSOR_VALUE_CHANGE", event.values[1].toString())
            Log.d("SENSOR_VALUE_CHANGE", event.values[2].toString())
            Log.d("SENSOR_VALUE_CHANGE", "COUNT = ${count++}")
            Log.d("SENSOR_VALUE_CHANGE", "=================================")
            lastShake = now
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        Log.d("ACCURACY_CHANGED", accuracy.toString())
    }

}