package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.data.repository.BalanceRepository
import javax.inject.Inject

class GetBalanceByIncome @Inject constructor(
    private val repository: BalanceRepository,
) {

    fun getIncomes(year: String, month: String) = repository.getBalanceByIncome(year = year, month = month)

}