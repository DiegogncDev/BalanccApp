package com.onedeepath.balanccapp.domain.model

import com.onedeepath.balanccapp.data.model.BalanceByMonthEntity

data class BalanceByMonth(
    val month: String,
    val type: String,
    val total: Double
)

fun BalanceByMonth.toEntity() = BalanceByMonthEntity(
    month = month,
    type = type,
    total = total
)

