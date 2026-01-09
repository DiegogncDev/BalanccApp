package com.onedeepath.balanccapp.ui.screens.detail.model

import com.github.mikephil.charting.data.PieEntry

data class MyMonthsChartUiState(
    val entries: List<PieChartData> = emptyList(),
    val colors: List<Int> = emptyList(),
    val centerText: String = "",
)
