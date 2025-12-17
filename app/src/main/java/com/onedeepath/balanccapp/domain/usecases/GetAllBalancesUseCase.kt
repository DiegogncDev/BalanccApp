package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.data.repository.BalanceRepository
import com.onedeepath.balanccapp.domain.model.BalanceModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBalancesUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke() : Flow<List<BalanceModel>> = repository.getAllBalancesFromDatabase()


}