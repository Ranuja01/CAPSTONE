package com.example.apptest

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.*
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.tan
import kotlin.math.exp

// Class for graphing current
class GraphActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private val handler = Handler()
    private val dataEntries = mutableListOf<Entry>()
    private var xValue = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        val tcpClient = TcpClient()

        // Call the function to read graph data from the microcontroller
        tcpClient.getGraphData("graph", "192.168.1.111", 50000)

        // Define the line chart
        lineChart = findViewById(R.id.lineChart)

        // Configure line chart
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.axisRight.isEnabled = false

        // Customize X-axis
        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelCount = 5 // Number of labels on the X-axis
        xAxis.granularity = 1f // Minimum axis-step (interval) for the X-axis
        xAxis.textColor = Color.WHITE
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return "${value.toInt()}s" // Customize X-axis labels
            }
        }

        // Customize Y-axis
        val yAxis: YAxis = lineChart.axisLeft
        yAxis.labelCount = 5 // Number of labels on the Y-axis
        yAxis.granularity = 1f // Minimum axis-step (interval) for the Y-axis
        yAxis.textColor = Color.WHITE
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString() // Display numeric values directly
            }
        }

        // Initialize and schedule the task to update the graph
        val updateGraphTask = object : Runnable {
            override fun run() {
                updateGraph()
                handler.postDelayed(this, 50) // Update every 500 milliseconds
            }
        }
        handler.post(updateGraphTask)
    }

    private fun updateGraph() {
        // Update xValue and calculate yValue for sin(x)
        xValue += 0.1f
        //val yValue = tan(tan(tan(xValue.toDouble()))).toFloat()
        //val yValue = exp(xValue.toDouble() * 0.05).toFloat()* sin(xValue.toDouble()).toFloat()
        var yValue = 0.0.toFloat()
        while(dataObject.isNull()){

        }
        yValue = dataObject.curData?.toFloat() ?: 0.0f
        dataObject.reset()
        // Add the new data entry
        dataEntries.add(Entry(xValue, yValue))

        // Limit the number of entries to prevent memory issues
        if (dataEntries.size > 600) {
            dataEntries.removeAt(0)
        }

        // Update the line chart data
        val dataSet = LineDataSet(dataEntries, "Current")
        dataSet.color = Color.BLUE
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Refresh the chart
        lineChart.invalidate()
    }
}
