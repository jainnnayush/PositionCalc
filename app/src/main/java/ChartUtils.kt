package com.example.positioncalculator

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

fun setupChart(chart: LineChart, title: String) {
    chart.apply {
        description = Description().apply { text = title }
        setTouchEnabled(true)
        isDragEnabled = true
        setScaleEnabled(true)
        setDrawGridBackground(false)
        axisLeft.setDrawGridLines(false)
        axisRight.setDrawGridLines(false)
        xAxis.setDrawGridLines(false)
        legend.isEnabled = false
    }
}

fun updateChart(chart: LineChart, value: Float, entryCount: Int) {
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

fun resetChart(chart: LineChart) {
    chart.data = null
    chart.notifyDataSetChanged()
    chart.invalidate()
}
