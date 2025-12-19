package com.onedeepath.balanccapp.domain.repository

import com.onedeepath.balanccapp.domain.model.BalanceByMonth
import com.onedeepath.balanccapp.domain.model.BalanceModel
import kotlinx.coroutines.flow.Flow

interface BalanceRepository {

    fun getAllBalances(): Flow<List<BalanceModel>>

    fun getIncomeByMonth(
        year: String,
        month: String
    ): Flow<List<BalanceModel>>

    fun getExpenseByMonth(
        year: String,
        month: String
    ): Flow<List<BalanceModel>>

    fun getBalanceByYear(year: String) : Flow<List<BalanceByMonth>>

    suspend fun insertBalance(balances: BalanceModel)

    suspend fun deleteBalance(id: Int)

}