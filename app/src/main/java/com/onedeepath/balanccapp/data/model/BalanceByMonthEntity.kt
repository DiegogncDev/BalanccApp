package com.onedeepath.balanccapp.data.model

import com.onedeepath.balanccapp.domain.model.BalanceByMonth

data class BalanceByMonthEntity(
    val month: String,
    val type: String,
    val total: Double
)

fun BalanceByMonthEntity.toDomain() = BalanceByMonth(
    month = month,
    type = type,
    total = total
)