package com.onedeepath.balanccapp.ui.presentation.model

data class MonthsCardModel(
    val monthName: String,
    val incomeAmount: Double = 0.0,
    val expenseAmount: Double = 0.0
)