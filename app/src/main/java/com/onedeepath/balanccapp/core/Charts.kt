package com.onedeepath.balanccapp.core

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet


data class ChartValue(
    var pieDataName: String?,
    var value: Float?
)

val getPieChartData = listOf(
    ChartValue("Income",25.0f),
    ChartValue("Income",25.0f),
    ChartValue("Income",25.0f),
    ChartValue("Income",25.0f)
)

@Composable
fun CreatePieChart(){

    Column(
        Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.padding(18.dp).size(320.dp) ,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Crossfade(targetState = getPieChartData, label = "") { ChartValue ->

                    AndroidView( factory = { context ->
                        PieChart(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            this.description.isEnabled = false
                            this.isDrawHoleEnabled = false
                            this.legend.isEnabled = true
                            this.legend.textSize = 14f
                            this.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

                        }

                    },
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(5.dp), update = { view ->
                                updatePieChartWithData(view, ChartValue)
                        }
                    )
                }
            }
        }
    }
}

fun updatePieChartWithData(
    chart: PieChart,
    data: List<ChartValue>
){
    var entries = ArrayList< com.github.mikephil.charting.data.PieEntry>()

    for (indices in data.indices){
        val item = data[indices]
        entries.add(com.github.mikephil.charting.data.PieEntry(item.value ?: 0.0f, item.pieDataName ?: ""))

    }

    val ds = PieDataSet(entries, "")

    ds.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
    ds.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
    ds.sliceSpace = 2f

    ds.valueTextSize = 18f
    ds.valueTypeface = android.graphics.Typeface.DEFAULT_BOLD
    val data = PieData(ds)
    chart.data = data
    chart.invalidate()



}