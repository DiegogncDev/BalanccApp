package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetBalanceByExpense @Inject constructor(
    private val repository: BalanceRepository,
) {

   fun getExpenses(year: String, month: String) : Flow<List<BalanceModel>> {

       if (year.isBlank() || month.isBlank()) {
           return flowOf(emptyList())
       }
       return repository.getExpenseByMonth(year = year, month = month)
   }
}