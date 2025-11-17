package com.onedeepath.balanccapp.domain

import com.onedeepath.balanccapp.data.repository.BalanceRepository
import javax.inject.Inject

class GetBalanceByExpense @Inject constructor(
    private val repository: BalanceRepository,
) {

   fun getExpenses(year: String, month: String) = repository.getBalanceByExpense(year = year, month = month)

}