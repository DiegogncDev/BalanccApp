package com.onedeepath.balanccapp.ui.screens.detail.model

import com.github.mikephil.charting.data.PieEntry

data class MonthsChartUiState(
    val entries: List<PieEntry> = emptyList(),
    val colors: List<Int> = emptyList(),
    val centerText: String = "",
    )