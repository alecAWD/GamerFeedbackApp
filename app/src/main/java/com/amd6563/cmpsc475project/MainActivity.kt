package com.amd6563.cmpsc475project

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import java.util.*
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private val userViewModel: UserViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(currentFragment == null){
            val fragment = LoginFragment()
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
        }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener{
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt(x * x +  y * y + z * z).toDouble().toFloat()
            var delta : Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
            if(acceleration > 12){
                Toast.makeText(applicationContext, "Shake event detected, logging out", Toast.LENGTH_SHORT).show()
                userViewModel.logout()
                val fragment = LoginFragment()
                var fragmentManager: FragmentManager = supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
            }

        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }
    override fun onResume() {
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }
    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }
    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}