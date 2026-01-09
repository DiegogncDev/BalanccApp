package com.onedeepath.balanccapp.ui.screens.detail.model

import androidx.compose.ui.graphics.Color
import com.onedeepath.balanccapp.domain.model.BalanceModel

data class MonthsDetailUiState(
    val year: String = "2025",
    val month: String = "January",

    val totalBalanceFormatted: String = "0.0",
    val totalBalanceColor: Color = Color.Unspecified,

    val incomes: List<BalanceModel> = emptyList(),
    val expenses: List<BalanceModel> = emptyList(),

    val incomeChart: MyMonthsChartUiState = MyMonthsChartUiState(),
    val expenseChart: MyMonthsChartUiState = MyMonthsChartUiState(),

    val isLoadingIncome: Boolean = false,
    val isLoadingExpense: Boolean = false,

    )