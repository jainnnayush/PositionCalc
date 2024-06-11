package com.example.positioncalculator

class Position {
    private var velocity = FloatArray(3) { 0f }
    private var position = FloatArray(3) { 0f }
    private var lastUpdateTime: Long = 0

    fun updatePosition(linearAcceleration: FloatArray, currentTime: Long): FloatArray {
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

    fun reset() {
        velocity = FloatArray(3) { 0f }
        position = FloatArray(3) { 0f }
        lastUpdateTime = 0
    }



}
