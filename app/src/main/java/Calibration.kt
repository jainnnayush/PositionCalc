package com.example.positioncalculator

class Calibration {
    private val gains = floatArrayOf(0.998f, 0.998f, 0.999f)
    private val offsets = floatArrayOf(-0.381f, -0.186f, 0.059f)
    private val angles = floatArrayOf(1.13f, -1.34f, -0.95f)
    fun calculateCorrectedAcceleration(rawAccel: FloatArray): FloatArray {
        val correctedAccel = FloatArray(3)
        for (i in 0..2) {
            correctedAccel[i] = (rawAccel[i])
        }
        return correctedAccel
    }
}