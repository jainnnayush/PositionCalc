package com.example.positioncalculator

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

class SensorData : SensorEventListener {
    var accelerometerData: FloatArray? = null
    private var filteredAccelValues = FloatArray(3) { 0f }
    private var gyroscopeData: FloatArray? = null
    private var lastTime: Long = 0

    var orientationListener: ((Float, Float, Float) -> Unit)? = null

    private val alpha = 0.0f
    private val lowPassAlpha = 0.0f
    private val highPassAlpha = 1.00f
    private var prevGyroAngles = FloatArray(3) { 0f }
    private var highPassGyroAngles = FloatArray(3) { 0f }
    private var roll = 0f
    private var pitch = 0f
    private var yaw = 0f

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                accelerometerData = event.values.clone()
            }
            Sensor.TYPE_GYROSCOPE -> {
                gyroscopeData = event.values.clone()
                calculateOrientation(event.timestamp)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    private fun calculateOrientation(timestamp: Long) {
        val acc = accelerometerData
        val gyr = gyroscopeData
        if (acc != null && gyr != null) {
            if (lastTime == 0L) {
                lastTime = timestamp
                return
            }
            filteredAccelValues[0] = lowPassFilter(acc[0], filteredAccelValues[0])
            filteredAccelValues[1] = lowPassFilter(acc[1], filteredAccelValues[1])
            filteredAccelValues[2] = lowPassFilter(acc[2], filteredAccelValues[2])
            val dt = (timestamp - lastTime) * 1.0f / 1_000_000_000.0f
            lastTime = timestamp

            val accelRoll = atan(filteredAccelValues[1] / filteredAccelValues[2])*(180 / Math.PI)
            val accelPitch = atan(-filteredAccelValues[0] / sqrt(filteredAccelValues[1].pow(2) + filteredAccelValues[2].pow(2))) *(180 / Math.PI)

            val gyrRoll = roll + gyr[0] * dt * (180 / Math.PI)
            val gyrPitch = pitch + gyr[1] * dt * (180 / Math.PI)
            val gyrYaw = yaw + gyr[2] * dt * (180 / Math.PI)

            highPassGyroAngles[0] = highPassFilter(gyrRoll.toFloat(), prevGyroAngles[0], highPassGyroAngles[0])
            highPassGyroAngles[1] = highPassFilter(gyrPitch.toFloat(), prevGyroAngles[1], highPassGyroAngles[1])

            prevGyroAngles[0] = gyrRoll.toFloat()
            prevGyroAngles[1] = gyrPitch.toFloat()

            roll = (alpha * highPassGyroAngles[0] + (1 - alpha) * accelRoll).toFloat()
            pitch = (alpha * highPassGyroAngles[1] + (1 - alpha) * accelPitch).toFloat()
            yaw = (gyrYaw).toFloat()

            orientationListener?.invoke(roll, pitch, yaw)
        }
    }

    private fun lowPassFilter(value: Float, prevValue: Float): Float {
        return prevValue * lowPassAlpha + value * (1 - lowPassAlpha)
    }

    private fun highPassFilter(currentValue: Float, prevValue: Float, prevHighPassValue: Float): Float {
        return highPassAlpha * (prevHighPassValue + currentValue - prevValue)
    }
}
