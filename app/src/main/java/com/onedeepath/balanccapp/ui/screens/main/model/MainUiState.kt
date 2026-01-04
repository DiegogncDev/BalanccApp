package com.onedeepath.balanccapp.ui.screens.main.model

data class MainUiState(
    val selectedYear: String = "",
    val months: List<MonthsBalanceUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
