package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBalancesUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke() : Flow<List<BalanceModel>> = repository.getAllBalances()


}