package com.onedeepath.balanccapp.ui.screens.addbalance.model

import com.onedeepath.balanccapp.domain.model.Category

data class AddBalanceUiState(
    val isIncome: Boolean = true,
    val amount: String = "",
    val category: Category = Category.WORK,
    val details: String = "",
    val selectedDay: String = "",
    val isValid: Boolean = false
)