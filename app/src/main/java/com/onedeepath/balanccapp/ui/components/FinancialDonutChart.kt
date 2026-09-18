package com.onedeepath.balanccapp.ui.components

import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.onedeepath.balanccapp.ui.screens.detail.model.PieChartData
import com.onedeepath.balanccapp.ui.theme.financialColors

@Composable
fun FinancialDonutChart(
    entries: List<PieChartData>,
    colors: List<Int>,
    centerText: String,
    isIncome: Boolean,
    modifier: Modifier = Modifier,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
    val centerTextColor = if (isIncome) {
        MaterialTheme.financialColors.income.toArgb()
    } else {
        MaterialTheme.financialColors.expense.toArgb()
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                setUsePercentValues(true)
                isDrawHoleEnabled = true
                holeRadius = 80f
                transparentCircleRadius = 0f
                setHoleColor(surfaceColor)
                setDrawEntryLabels(false)
                setDrawCenterText(true)
                centerTextRadiusPercent = 100f
                setCenterTextColor(centerTextColor)
                setCenterTextSize(20f)
                setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                legend.isEnabled = false
                animateY(600)
            }
        },
        update = { chart ->
            chart.setHoleColor(surfaceColor)
            chart.setCenterTextColor(centerTextColor)

            val pieEntries = entries.map { PieEntry(it.value, "") }
            val dataSet = PieDataSet(pieEntries, "").apply {
                setDrawValues(false)
                this.colors = colors
                sliceSpace = 3f
            }

            chart.data = PieData(dataSet)
            chart.centerText = centerText
            chart.invalidate()
        },
    )
}
