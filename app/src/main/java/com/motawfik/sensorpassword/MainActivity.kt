package com.motawfik.sensorpassword

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd


class MainActivity : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null
    private lateinit var sensorListener: SensorListener
    private lateinit var firstProgressBar: ProgressBar
    private lateinit var secondProgressBar: ProgressBar
    private lateinit var thirdProgressbar: ProgressBar

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private lateinit var thirdTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorListener = SensorListener()
        if (accelerometerSensor == null) {
            Toast.makeText(this, "No Accelerometer Found", Toast.LENGTH_LONG).show()
        }

        firstProgressBar = findViewById(R.id.firstProgressBar)
        secondProgressBar = findViewById(R.id.secondProgressBar)
        thirdProgressbar = findViewById(R.id.thirdProgressBar)

        firstTextView = findViewById(R.id.firstTextView)
        secondTextView = findViewById(R.id.secondTextView)
        thirdTextView = findViewById(R.id.thirdTextView)

        sensorListener.count = 0
        var animator = getAnimator(firstProgressBar, firstTextView) // animate the first circular progress bar
        sensorListener.textView = findViewById(R.id.firstDigit)

        animator.doOnEnd { // wait till the first progress bar finish
            sensorListener.textView = findViewById(R.id.secondDigit)
            sensorListener.count = 0
            animator = getAnimator(secondProgressBar, secondTextView) // animate the second circular progress bar
            animator.doOnEnd { // wait till the second progress bar finish
                sensorListener.textView = findViewById(R.id.thirdDigit)
                sensorListener.count = 0
                animator = getAnimator(thirdProgressbar, thirdTextView) // animate the third progress bar
            }
        }

    }

    private fun getAnimator(progressBar: ProgressBar, textView: TextView): ObjectAnimator {
        val animator: ObjectAnimator = ObjectAnimator.ofInt(progressBar, "progress", 100)
        animator.duration = 2500 // in milliseconds
        animator.interpolator = LinearInterpolator()
        animator.start()
        animator.addUpdateListener { updatedAnimation ->
            when (updatedAnimation.animatedValue.toString().toInt()) {
                in 20..39 -> {
                    textView.text = getString(R.string.two_seconds)
                }
                in 40..59 -> {
                    textView.text = getString(R.string.one_and_half_seconds)
                }
                in 60..79 -> {
                    textView.text = getString(R.string.one_second)
                }
                in 80..99 -> {
                    textView.text = getString(R.string.half_second)
                }
                100 -> {
                    textView.text = getString(R.string.finish)
                }
            }
        }
        return animator
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