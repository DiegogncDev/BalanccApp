package com.onedeepath.balanccapp.domain

import com.onedeepath.balanccapp.data.repository.BalanceRepository
import javax.inject.Inject

class DeleteBalanceUseCase @Inject constructor(
    private val repository: BalanceRepository
) {

    suspend operator fun invoke(id: Int) {
        repository.deleteBalance(id)
    }


}