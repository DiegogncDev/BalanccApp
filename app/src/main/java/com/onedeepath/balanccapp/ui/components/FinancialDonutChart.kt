package com.onedeepath.balanccapp.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    amountText: String,
    subtitleText: String,
    isIncome: Boolean,
    modifier: Modifier = Modifier,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PieChart(context).apply {
                    description.isEnabled = false
                    setUsePercentValues(true)
                    isDrawHoleEnabled = true
                    holeRadius = 72f
                    transparentCircleRadius = 0f
                    setHoleColor(surfaceColor)
                    setDrawEntryLabels(false)
                    setDrawCenterText(false)
                    legend.isEnabled = false
                    animateY(600)
                }
            },
            update = { chart ->
                chart.setHoleColor(surfaceColor)

                val pieEntries = entries.map { PieEntry(it.value, "") }
                val dataSet = PieDataSet(pieEntries, "").apply {
                    setDrawValues(false)
                    this.colors = colors
                    sliceSpace = 2.5f
                }

                chart.data = PieData(dataSet)
                chart.invalidate()
            },
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = amountText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitleText,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    color = MaterialTheme.financialColors.textSecondary,
                ),
            )
        }
    }
}

