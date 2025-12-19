package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import javax.inject.Inject

class GetBalanceByIncome @Inject constructor(
    private val repository: BalanceRepository,
) {

    fun getIncomes(year: String, month: String) = repository.getIncomeByMonth(year = year, month = month)

}