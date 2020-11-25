package com.motawfik.sensorpassword

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import android.widget.TextView

class SensorListener : SensorEventListener {

    private var lastShake = System.currentTimeMillis()
    private val SHAKE_SLOP = 280
    var count = 0
    lateinit var textView: TextView

    override fun onSensorChanged(event: SensorEvent?) {
        val maxRange = event!!.sensor.maximumRange
        val xValue = event.values[0]
        if (xValue >= maxRange * 0.13) {
            val now = System.currentTimeMillis()
            if (now <= lastShake + SHAKE_SLOP) { // ignore shake events that are too close to each other - (280 milliseconds difference)
                return
            }
            count++
            textView.text = count.toString()
            lastShake = now
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something here if sensor accuracy changes.
        Log.d("ACCURACY_CHANGED", accuracy.toString())
    }

}