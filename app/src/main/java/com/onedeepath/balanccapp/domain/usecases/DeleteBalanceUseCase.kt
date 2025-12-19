package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import javax.inject.Inject

class DeleteBalanceUseCase @Inject constructor(
    private val repository: BalanceRepository
) {

    suspend operator fun invoke(id: Int) {
        repository.deleteBalance(id)
    }


}