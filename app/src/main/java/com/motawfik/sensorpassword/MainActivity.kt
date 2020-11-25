package com.motawfik.sensorpassword

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null
    private lateinit var sensorListener: SensorListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorListener = SensorListener()
        if (accelerometerSensor == null) {
            Toast.makeText(this, "No Accelerometer Found", Toast.LENGTH_LONG).show()
        }
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)
        for (deviceSensor in deviceSensors) {
            Log.d("SENSOR_AVAILABLE", deviceSensor.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometerSensor?.also { accelerometer ->
            sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
    }
}