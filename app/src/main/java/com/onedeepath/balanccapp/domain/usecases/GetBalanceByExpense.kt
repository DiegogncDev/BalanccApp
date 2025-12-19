package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import javax.inject.Inject

class GetBalanceByExpense @Inject constructor(
    private val repository: BalanceRepository,
) {

   fun getExpenses(year: String, month: String) = repository.getExpenseByMonth(year = year, month = month)

}