package com.onedeepath.balanccapp.ui.presentation.model

import com.onedeepath.balanccapp.data.database.entity.BalanceEntity

data class BalanceModel(
    val id: Int,
    val type: String,
    val category: String,
    val description: String,
    val amount: Double,
    val day: String,
    val month: String,
    val year: String,
)

fun BalanceEntity.toDomain() = BalanceModel(id = id,type = type, category = category, description = description, amount = amount, day =  day, month = month, year = year)