package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetBalanceByIncome @Inject constructor(
    private val repository: BalanceRepository,
) {

    fun getIncomes(year: String, month: String) : Flow<List<BalanceModel>> {
        if (year.isBlank() && month.isBlank()) {
            return flowOf(emptyList())
        }
        return repository.getIncomeByMonth(year = year, month = month)
    }

}