package com.motawfik.sensorpassword

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.google.android.material.snackbar.Snackbar
import com.motawfik.sensorpassword.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null
    private lateinit var sensorListener: SensorListener
    private lateinit var binding: ActivityMainBinding
    private var animator: ObjectAnimator? = null
    private val password: String = "312"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorListener = SensorListener()
        if (accelerometerSensor == null) {
            Toast.makeText(this, "No Accelerometer Found", Toast.LENGTH_LONG).show()
        } else {
            binding.startBtn.setOnClickListener {
                resetUI()
                sensorListener.count = 0
                var animator = getAnimator(
                    binding.firstProgressBar,
                    binding.firstTextView
                ) // animate the first circular progress bar
                sensorListener.textView = binding.firstDigit
                animator.doOnEnd { // wait till the first progress bar finish
                    sensorListener.textView = binding.secondDigit
                    sensorListener.count = 0
                    animator = getAnimator(
                        binding.secondProgressBar,
                        binding.secondTextView
                    ) // animate the second circular progress bar
                    animator.doOnEnd { // wait till the second progress bar finish
                        sensorListener.textView = binding.thirdDigit
                        sensorListener.count = 0
                        animator = getAnimator(
                            binding.thirdProgressBar,
                            binding.thirdTextView
                        ) // animate the third progress bar
                        animator.doOnEnd {
                            val enteredPassword = "${binding.firstDigit.text}${binding.secondDigit.text}${binding.thirdDigit.text}"
                            val message: String
                            val color: Int
                            val isCorrectPassword = (enteredPassword == password)
                            if (isCorrectPassword) {
                                message = "Correct Password!"
                                color = Color.GREEN
                            } else {
                                message = "Wrong password, try again ($enteredPassword)"
                                color = Color.RED
                            }
                            val snackBar = Snackbar.make(
                                view,
                                message,
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction("DISMISS") {}
                            snackBar.view.setBackgroundColor(color)
                            snackBar.show()
                        }
                    }
                }
            }
        }
    }

    private fun getAnimator(progressBar: ProgressBar, textView: TextView): ObjectAnimator {
        animator = ObjectAnimator.ofInt(progressBar, "progress", 100)
        animator?.duration = 2500 // in milliseconds
        animator?.interpolator = LinearInterpolator()
        animator?.start()
        animator?.addUpdateListener { updatedAnimation ->
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
        return animator!!
    }

    private fun resetUI() {
        animator?.removeAllListeners()
        animator?.cancel()
        binding.firstDigit.text = ""
        binding.secondDigit.text = ""
        binding.thirdDigit.text = ""
        binding.firstTextView.text = getString(R.string.remaining_seconds)
        binding.secondTextView.text = getString(R.string.remaining_seconds)
        binding.thirdTextView.text = getString(R.string.remaining_seconds)
        binding.firstProgressBar.progress = 0
        binding.secondProgressBar.progress = 0
        binding.thirdProgressBar.progress = 0
    }

    override fun onResume() {
        super.onResume()
        accelerometerSensor?.also { accelerometer ->
            sensorManager.registerListener(
                sensorListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
    }
}