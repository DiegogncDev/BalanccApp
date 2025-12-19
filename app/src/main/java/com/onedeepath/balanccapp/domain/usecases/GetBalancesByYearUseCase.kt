package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.model.BalanceByMonth
import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBalancesByYearUseCase @Inject constructor(
    private val repository: BalanceRepository
) {

    operator fun invoke(year: String) : Flow<List<BalanceByMonth>> {
        return repository.getBalanceByYear(year)
    }

}