package com.onedeepath.balanccapp.domain.model

data class BalanceModel(
    val id: Int = 0,
    val type: String,
    val category: Category,
    val description: String,
    val amount: Double,
    val day: String,
    val month: String,
    val year: String,
)
