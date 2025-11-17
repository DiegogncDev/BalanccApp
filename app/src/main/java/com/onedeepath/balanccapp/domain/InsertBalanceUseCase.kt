package com.onedeepath.balanccapp.domain

import com.onedeepath.balanccapp.data.repository.BalanceRepository
import com.onedeepath.balanccapp.data.database.entity.BalanceEntity
import javax.inject.Inject

class InsertBalanceUseCase @Inject constructor(
    private val repository: BalanceRepository
) {
    suspend operator fun invoke(balance: BalanceEntity) {
        repository.insertBalance(balance)
    }
}