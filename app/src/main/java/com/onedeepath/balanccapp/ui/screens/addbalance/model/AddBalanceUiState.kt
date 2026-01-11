package com.onedeepath.balanccapp.ui.screens.addbalance.model

import com.onedeepath.balanccapp.domain.model.Category

data class AddBalanceUiState(
    val isIncome: Boolean = true,
    val amount: String = "",
    val category: Category = Category.WORK,
    val details: String = "",
    val selectedDay: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
) {
    // Each change state calculate automatically
    val isValid: Boolean get() = amount.isNotBlank() && selectedDay != null
}