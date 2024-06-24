//package com.example.positioncalculator
//
//import android.hardware.Sensor
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.TextView
//import kotlin.math.cos
//import kotlin.math.sin
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var sensorManager: SensorManager
//    private lateinit var accelerometer: Sensor
//    private lateinit var gyroscope: Sensor
//    private lateinit var sensorData: SensorData
//    private var filteredAccelValues = FloatArray(3) { 0f }
//    private val lowPassAlpha = 0.95f
//    private lateinit var linearAccelerationXView: TextView
//    private lateinit var linearAccelerationYView: TextView
//    private lateinit var linearAccelerationZView: TextView
//    private lateinit var rollView: TextView
//    private lateinit var pitchView: TextView
//    private lateinit var yawView: TextView
//    private lateinit var positionXView: TextView
//    private lateinit var positionYView: TextView
//    private lateinit var positionZView: TextView
////    private lateinit var accelerometerXView: TextView
////    private lateinit var accelerometerYView: TextView
////    private lateinit var accelerometerZView: TextView
//
//    private var velocity = FloatArray(3) { 0f }
//    private var position = FloatArray(3) { 0f }
//    private var lastUpdateTime: Long = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        linearAccelerationXView = findViewById(R.id.linear_acceleration_x)
//        linearAccelerationYView = findViewById(R.id.linear_acceleration_y)
//        linearAccelerationZView = findViewById(R.id.linear_acceleration_z)
//        rollView = findViewById(R.id.roll)
//        pitchView = findViewById(R.id.pitch)
//        yawView = findViewById(R.id.yaw)
//        positionXView = findViewById(R.id.position_x)
//        positionYView = findViewById(R.id.position_y)
//        positionZView = findViewById(R.id.position_z)
////        accelerometerXView = findViewById(R.id.accelerometer_x)
////        accelerometerYView = findViewById(R.id.accelerometer_y)
////        accelerometerZView = findViewById(R.id.accelerometer_z)
//
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
//        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!
//
//        sensorData = SensorData().apply {
//            orientationListener = { roll, pitch, yaw ->
//                rollView.text = "Roll: $roll"
//                pitchView.text = "Pitch: $pitch"
//                yawView.text = "Yaw: $yaw"
//
//                accelerometerData?.let { acc ->
//                    val rollRad = Math.toRadians(roll.toDouble())
//                    val pitchRad = Math.toRadians(pitch.toDouble())
//                    val yawRad = Math.toRadians(yaw.toDouble())
//
////                    accelerometerXView.text = "Accelerometer X: ${acc[0]}"
////                    accelerometerYView.text = "Accelerometer Y: ${acc[1]}"
////                    accelerometerZView.text = "Accelerometer Z: ${acc[2]}"
//                    val linearAcc = extractLinearAcceleration(acc, rollRad, pitchRad, yawRad)
//
//                    linearAccelerationXView.text = "Linear Acceleration X: ${linearAcc[0]}"
//                    linearAccelerationYView.text = "Linear Acceleration Y: ${linearAcc[1]}"
//                    linearAccelerationZView.text = "Linear Acceleration Z: ${linearAcc[2]}"
//                    val pos = updatePosition(linearAcc, System.nanoTime())
//                    positionXView.text = "Position X: ${pos[0]}"
//                    positionYView.text = "Position Y: ${pos[1]}"
//                    positionZView.text = "Position Z: ${pos[2]}"
//                }
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        sensorManager.registerListener(sensorData, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
//        sensorManager.registerListener(sensorData, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        sensorManager.unregisterListener(sensorData)
//    }
//
//    private fun extractLinearAcceleration(accelReadings: FloatArray, roll: Double, pitch: Double, yaw: Double): FloatArray {
//        val g = 9.81f
//
//        val g_inertial = floatArrayOf(0.0f, 0.0f, g)
//
//        val R = rotationMatrix(roll, pitch, yaw)
//
//        val g_body = FloatArray(3)
//        for (i in 0..2) {
//            g_body[i] = 0.0f
//            for (j in 0..2) {
//                g_body[i] += R[i][j].toFloat() * g_inertial[j]
//            }
//        }
//
//        val a_linear = FloatArray(3)
//        for (i in 0..1) {
//            a_linear[i] = (accelReadings[i] + g_body[i])*1000
//        }
//        a_linear[2] = (accelReadings[2] - g_body[2])*1000
//        filteredAccelValues[0]=lowPassFilter(a_linear[0],filteredAccelValues[0])
//        filteredAccelValues[1]=lowPassFilter(a_linear[1],filteredAccelValues[1])
//        filteredAccelValues[2]=lowPassFilter(a_linear[2],filteredAccelValues[2])
//        return filteredAccelValues
//    }
//
//    private fun rotationMatrix(roll: Double, pitch: Double, yaw: Double): Array<DoubleArray> {
//        val R_x = arrayOf(
//            doubleArrayOf(1.0, 0.0, 0.0),
//            doubleArrayOf(0.0, cos(roll), -sin(roll)),
//            doubleArrayOf(0.0, sin(roll), cos(roll))
//        )
//
//        val R_y = arrayOf(
//            doubleArrayOf(cos(pitch), 0.0, sin(pitch)),
//            doubleArrayOf(0.0, 1.0, 0.0),
//            doubleArrayOf(-sin(pitch), 0.0, cos(pitch))
//        )
//
//        val R_z = arrayOf(
//            doubleArrayOf(cos(yaw), -sin(yaw), 0.0),
//            doubleArrayOf(sin(yaw), cos(yaw), 0.0),
//            doubleArrayOf(0.0, 0.0, 1.0)
//        )
//
//        val R_yx = Array(3) { DoubleArray(3) }
//        for (i in 0..2) {
//            for (j in 0..2) {
//                R_yx[i][j] = 0.0
//                for (k in 0..2) {
//                    R_yx[i][j] = R_yx[i][j] + R_y[i][k] * R_x[k][j]
//                }
//            }
//        }
//
//        val R = Array(3) { DoubleArray(3) }
//        for (i in 0..2) {
//            for (j in 0..2) {
//                R[i][j] = 0.0
//                for (k in 0..2) {
//                    R[i][j] += R_z[i][k] * R_yx[k][j]
//                }
//            }
//        }
//
//        return R
//    }
//
//    private fun updatePosition(linearAcceleration: FloatArray, currentTime: Long): FloatArray {
//        if (lastUpdateTime == 0L) {
//            lastUpdateTime = currentTime
//            return position
//        }
//
//        val dt = (currentTime - lastUpdateTime) * 1.0f / 1_000_000_000.0f
//        lastUpdateTime = currentTime
//
//        for (i in 0 until 3) {
//            velocity[i] += linearAcceleration[i] * dt
//            position[i] += velocity[i] * dt
//        }
//
//        return position
//    }
//
//    private fun lowPassFilter(value: Float, prevValue: Float): Float {
//        return prevValue * lowPassAlpha + value * (1 - lowPassAlpha)
//    }
//}
package com.example.positioncalculator

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.os.Bundle
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var gyroscope: Sensor
    private lateinit var sensorData: SensorData
    private lateinit var sensorCalibration: Calibration
    private var filteredAccelValues = FloatArray(3) { 0f }
    private val lowPassAlpha = 0.0f

    private lateinit var linearAccelerationXView: TextView
    private lateinit var linearAccelerationYView: TextView
    private lateinit var linearAccelerationZView: TextView
    private lateinit var rollView: TextView
    private lateinit var pitchView: TextView
    private lateinit var yawView: TextView
    private lateinit var positionXView: TextView
    private lateinit var positionYView: TextView
    private lateinit var positionZView: TextView

    private lateinit var linearAccelChartX: LineChart
    private lateinit var linearAccelChartY: LineChart
    private lateinit var linearAccelChartZ: LineChart

    private lateinit var positionChartX: LineChart
    private lateinit var positionChartY: LineChart
    private lateinit var positionChartZ: LineChart

    private var velocity = FloatArray(3) { 0f }
    private var position = FloatArray(3) { 0f }
    private var lastUpdateTime: Long = 0
    private var entryCount = 0

    private var sumLinearAccX = 0f
    private var sumLinearAccY = 0f
    private var sumLinearAccZ = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearAccelerationXView = findViewById(R.id.linear_acceleration_x)
        linearAccelerationYView = findViewById(R.id.linear_acceleration_y)
        linearAccelerationZView = findViewById(R.id.linear_acceleration_z)
        rollView = findViewById(R.id.roll)
        pitchView = findViewById(R.id.pitch)
        yawView = findViewById(R.id.yaw)
        positionXView = findViewById(R.id.position_x)
        positionYView = findViewById(R.id.position_y)
        positionZView = findViewById(R.id.position_z)
        linearAccelChartX = findViewById(R.id.linear_accel_chart_x)
        linearAccelChartY = findViewById(R.id.linear_accel_chart_y)
        linearAccelChartZ = findViewById(R.id.linear_accel_chart_z)
        positionChartX = findViewById(R.id.position_chart_x)
        positionChartY = findViewById(R.id.position_chart_y)
        positionChartZ = findViewById(R.id.position_chart_z)

        setupChart(linearAccelChartX, "Linear Acceleration X")
        setupChart(linearAccelChartY, "Linear Acceleration Y")
        setupChart(linearAccelChartZ, "Linear Acceleration Z")
        setupChart(positionChartX, "Position X")
        setupChart(positionChartY, "Position Y")
        setupChart(positionChartZ, "Position Z")

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!

        sensorCalibration = Calibration()

        sensorData = SensorData().apply {
            orientationListener = { roll, pitch, yaw ->
                rollView.text = "Roll: $roll"
                pitchView.text = "Pitch: $pitch"
                yawView.text = "Yaw: $yaw"

                accelerometerData?.let { acc ->
                    // change the acceleration here according to csv file
                    val correctedAcc=sensorCalibration.calculateCorrectedAcceleration(acc)
                    val rollRad = Math.toRadians(roll.toDouble())
                    val pitchRad = Math.toRadians(pitch.toDouble())
                    val yawRad = Math.toRadians(yaw.toDouble())

                    val linearAcc = extractLinearAcceleration2(correctedAcc, rollRad, pitchRad, yawRad)
                    linearAccelerationXView.text = "Linear Acceleration X: ${linearAcc[0]}"
                    linearAccelerationYView.text = "Linear Acceleration Y: ${linearAcc[1]}"
                    linearAccelerationZView.text = "Linear Acceleration Z: ${linearAcc[2]}"
                    val pos = updatePosition(linearAcc, System.nanoTime())
                    positionXView.text = "Position X: ${acc[0]}"
                    positionYView.text = "Position Y: ${acc[1]}"
                    positionZView.text = "Position Z: ${acc[2]}"

                    sumLinearAccX += linearAcc[0]
                    sumLinearAccY += linearAcc[1]
                    sumLinearAccZ += linearAcc[2]

                    updateCharts(linearAcc, pos)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorData, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(sensorData, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorData)
    }

    private fun setupChart(chart: LineChart, label: String) {
        chart.apply {
            description.text = label
            xAxis.labelRotationAngle = 0f
            axisRight.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            setMaxVisibleValueCount(150)
            setDrawBorders(false)
        }
    }

    private fun updateCharts(linearAcc: FloatArray, pos: FloatArray) {
        if (entryCount >= 500) {
            val avgLinearAccX = sumLinearAccX / 75
            val avgLinearAccY = sumLinearAccY / 75
            val avgLinearAccZ = sumLinearAccZ / 75

            Log.d("SensorData", "Average Linear Acceleration X: $avgLinearAccX")
            Log.d("SensorData", "Average Linear Acceleration Y: $avgLinearAccY")
            Log.d("SensorData", "Average Linear Acceleration Z: $avgLinearAccZ")
            clearChart(linearAccelChartX)
            clearChart(linearAccelChartY)
            clearChart(linearAccelChartZ)
            clearChart(positionChartX)
            clearChart(positionChartY)
            clearChart(positionChartZ)
            sumLinearAccX = 0f
            sumLinearAccY = 0f
            sumLinearAccZ = 0f
            entryCount = 0
        }
        updateChart(linearAccelChartX, linearAcc[0])
        updateChart(linearAccelChartY, linearAcc[1])
        updateChart(linearAccelChartZ, linearAcc[2])
        updateChart(positionChartX, pos[0])
        updateChart(positionChartY, pos[1])
        updateChart(positionChartZ, pos[2])
        entryCount++
    }

    private fun updateChart(chart: LineChart, value: Float) {
        val data = chart.data
        if (data == null) {
            val set = LineDataSet(mutableListOf(Entry(entryCount.toFloat(), value)), "Data")
            set.setDrawCircles(false)
            set.setDrawValues(false)
            val lineData = LineData(set)
            chart.data = lineData
        } else {
            var set = data.getDataSetByIndex(0) as? LineDataSet
            if (set == null) {
                set = LineDataSet(mutableListOf(Entry(entryCount.toFloat(), value)), "Data")
                set.setDrawCircles(false)
                set.setDrawValues(false)
                data.addDataSet(set)
            } else {
                set.addEntry(Entry(entryCount.toFloat(), value))
                data.notifyDataChanged()
                chart.notifyDataSetChanged()
            }
        }
        chart.invalidate()
    }

    private fun clearChart(chart: LineChart) {
        chart.data = null
        chart.invalidate()
    }
// Method 1 - Linear Acceleration = a-R*g
//    private fun extractLinearAcceleration(accelReadings: FloatArray, roll: Double, pitch: Double, yaw: Double): FloatArray {
//        val g = 9.81f
//        val g_inertial = floatArrayOf(0.0f, 0.0f, g)
//        val R = rotationMatrix(roll, pitch, yaw)
//        val g_body = FloatArray(3)
//        for (i in 0..2) {
//            g_body[i] = 0.0f
//            for (j in 0..2) {
//                g_body[i] += R[i][j].toFloat() * g_inertial[j]
//            }
//        }
//        val a_linear = FloatArray(3)
//        for (i in 0..2) {
//            a_linear[i] = (accelReadings[i] - g_body[i])
//        }
//        filteredAccelValues[0]=lowPassFilter(a_linear[0],filteredAccelValues[0])
//        filteredAccelValues[1]=lowPassFilter(a_linear[1],filteredAccelValues[1])
//        filteredAccelValues[2]=lowPassFilter(a_linear[2],filteredAccelValues[2])
//        return filteredAccelValues
//    }


// Method 2 Linear acceleration = R*a-g
    private fun extractLinearAcceleration2(accelReadings: FloatArray, roll: Double, pitch: Double, yaw: Double): FloatArray {
        val g = -9.81f
        val g_inertial = floatArrayOf(0.0f, 0.0f, g)
        val R = rotationsMatrix(roll, pitch, yaw)
        val g_body = FloatArray(3)
        for (i in 0..2) {
            g_body[i] = 0.0f
            for (j in 0..2) {
                g_body[i] += R[i][j].toFloat() * accelReadings[j]
            }
        }
        val a_linear = FloatArray(3)
        for (i in 0..2) {
            a_linear[i] = (g_body[i]) + (g_inertial[i])
        }
        filteredAccelValues[0]=lowPassFilter(a_linear[0],filteredAccelValues[0])
        filteredAccelValues[1]=lowPassFilter(a_linear[1],filteredAccelValues[1])
        filteredAccelValues[2]=lowPassFilter(a_linear[2],filteredAccelValues[2])
        return filteredAccelValues
    }

// Using different rotation matrix
//    private fun extractLinearAcceleration3(accelReadings: FloatArray, roll: Double, pitch: Double, yaw: Double): FloatArray {
//        val g = 9.81f
//        val g_inertial = floatArrayOf(0.0f, 0.0f, g)
//        val R = rotationsMatrix(roll, pitch, yaw)
//        val g_body = FloatArray(3)
//        for (i in 0..2) {
//            g_body[i] = (accelReadings[i] - g_inertial[i])
//        }
//        val a_linear = FloatArray(3)
//        for (i in 0..2) {
//            a_linear[i]=0f
//            for (j in 0..2) {
//                a_linear[i] += R[i][j].toFloat() * g_body[j]
//            }
//        }
//        filteredAccelValues[0]=lowPassFilter(a_linear[0],filteredAccelValues[0])
//        filteredAccelValues[1]=lowPassFilter(a_linear[1],filteredAccelValues[1])
//        filteredAccelValues[2]=lowPassFilter(a_linear[2],filteredAccelValues[2])
//        return filteredAccelValues
//    }
    private fun rotationMatrix(roll: Double, pitch: Double, yaw: Double): Array<DoubleArray> {
        val R_x = arrayOf(
            doubleArrayOf(1.0, 0.0, 0.0),
            doubleArrayOf(0.0, cos(roll), -sin(roll)),
            doubleArrayOf(0.0, sin(roll), cos(roll))
        )
        val R_y = arrayOf(
            doubleArrayOf(cos(pitch), 0.0, sin(pitch)),
            doubleArrayOf(0.0, 1.0, 0.0),
            doubleArrayOf(-sin(pitch), 0.0, cos(pitch))
        )
        val R_z = arrayOf(
            doubleArrayOf(cos(yaw), sin(yaw), 0.0),
            doubleArrayOf(-sin(yaw), cos(yaw), 0.0),
            doubleArrayOf(0.0, 0.0, 1.0)
        )
        val R_yz = Array(3) { DoubleArray(3) }
        for (i in 0..2) {
            for (j in 0..2) {
                R_yz[i][j] = 0.0
                for (k in 0..2) {
                    R_yz[i][j] += R_y[i][k] * R_z[k][j]
                }
            }
        }
        val R = Array(3) { DoubleArray(3) }
        for (i in 0..2) {
            for (j in 0..2) {
                R[i][j] = 0.0
                for (k in 0..2) {
                    R[i][j] += R_x[i][k] * R_yz[k][j]
                }
            }
        }
        return R
    }
    private fun rotationsMatrix(roll: Double, pitch: Double, yaw: Double): Array<DoubleArray> {
        val R = arrayOf(doubleArrayOf(cos(pitch)*cos(yaw),-cos(roll)*sin(yaw)+sin(roll)*sin(pitch)*cos(yaw),sin(roll)*sin(yaw)+cos(roll)*sin(pitch)*cos(yaw)),
            doubleArrayOf(cos(pitch)*sin(yaw),cos(roll)*cos(yaw)+sin(roll)*sin(pitch)*sin(yaw),sin(roll)*cos(pitch)),
            doubleArrayOf(-sin(pitch),sin(roll)*cos(pitch),cos(roll)*cos(pitch))
            )
        return R
    }
    private fun updatePosition(linearAcceleration: FloatArray, currentTime: Long): FloatArray {
        if (lastUpdateTime == 0L) {
            lastUpdateTime = currentTime
            return position
        }
        val dt = (currentTime - lastUpdateTime) * 1.0f / 1_000_000_000.0f
        lastUpdateTime = currentTime
        for (i in 0 until 3) {
            velocity[i] += linearAcceleration[i] * dt
            position[i] += velocity[i] * dt
        }
        return position
    }

    private fun lowPassFilter(value: Float, prevValue: Float): Float {
        return prevValue * lowPassAlpha + value * (1 - lowPassAlpha)
    }

}
