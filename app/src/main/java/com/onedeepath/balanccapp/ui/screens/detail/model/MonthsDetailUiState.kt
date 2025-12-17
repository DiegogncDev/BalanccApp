package com.onedeepath.balanccapp.ui.screens.detail.model

import androidx.compose.ui.graphics.Color
import com.onedeepath.balanccapp.domain.model.BalanceModel

data class MonthsDetailUiState(
    val year: String = "",
    val month: String = "",

    val totalBalanceFormatted: String = "",
    val totalBalanceColor: Color = Color.Unspecified,

    val incomes: List<BalanceModel> = emptyList(),
    val expenses: List<BalanceModel> = emptyList(),

    val incomeChart: MonthsChartUiState = MonthsChartUiState(),
    val expenseChart: MonthsChartUiState = MonthsChartUiState(),

    val isLoadingIncome: Boolean = false,
    val isLoadingExpense: Boolean = false,

    )