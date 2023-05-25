package com.example.odh_project_1.Chart

import android.util.Log
import com.example.odh_project_1.DataClass.TrendResponse
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class ChartDrawer {
    fun drawLineChart(lineChart: LineChart,trendResponse: TrendResponse,query: String) {
        val values = ArrayList<Entry>()
        for ((index, periodData) in trendResponse.results[0].data.withIndex()) {
            values.add(Entry(index.toFloat(), periodData.ratio))
            Log.d("asd","${periodData.ratio}")
        }

        val set1 = LineDataSet(values,  query)
        set1.setDrawFilled(false)  // Fill under the line is not drawn
        set1.fillAlpha = 110

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1)

        val data = LineData(dataSets)

        lineChart.data = data

        val months = arrayOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM  // Set XAxis at the bottom
        xAxis.valueFormatter = IndexAxisValueFormatter(months)
        xAxis.setDrawGridLines(false)  // Remove grid lines
    }
}
