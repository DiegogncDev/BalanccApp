package com.onedeepath.balanccapp.ui.screens.main.mapper

import com.onedeepath.balanccapp.domain.model.BalanceByMonth
import com.onedeepath.balanccapp.ui.screens.main.model.MonthsBalanceUi

fun List<BalanceByMonth>.toMonthsBalanceUi(): List<MonthsBalanceUi> {
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    return months.mapIndexed { index, month ->
        val income = filter { it.month == month && it.type == "income" }
            .sumOf { it.total }

        val expense = filter { it.month == month && it.type == "expense" }
            .sumOf { it.total }

        MonthsBalanceUi(
            monthIndex = index,
            monthName = month,
            income = income,
            expense = expense,
            balance = income - expense
        )

    }
}