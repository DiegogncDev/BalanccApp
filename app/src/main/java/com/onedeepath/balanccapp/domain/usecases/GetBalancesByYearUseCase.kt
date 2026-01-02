package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.model.BalanceByMonth
import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetBalancesByYearUseCase @Inject constructor(
    private val repository: BalanceRepository
) {

    operator fun invoke(year: String) : Flow<List<BalanceByMonth>> {
        if (year.isBlank()) {
            return flowOf(emptyList())
        }
        return repository.getBalanceByYear(year)

    }

}