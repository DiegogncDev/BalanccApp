package com.onedeepath.balanccapp.ui.screens.main.model

import com.onedeepath.balanccapp.domain.model.Category

data class HomeCategoryItem(
    val category: Category,
    val amount: Double,
    val percentage: Int,
)

data class MainUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balanceChangePercent: Int? = null,
    val expenseBreakdown: List<HomeCategoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val totalBalance: Double get() = totalIncome - totalExpense
}
