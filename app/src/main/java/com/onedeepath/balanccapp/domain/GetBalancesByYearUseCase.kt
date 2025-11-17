package com.onedeepath.balanccapp.domain

import com.onedeepath.balanccapp.data.repository.BalanceRepository
import com.onedeepath.balanccapp.ui.presentation.model.BalanceByMonthEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBalancesByYearUseCase @Inject constructor(
    private val repository: BalanceRepository
) {

    operator fun invoke(year: String) : Flow<List<BalanceByMonthEntity>> {
        return repository.getBalanceByYear(year)
    }

}