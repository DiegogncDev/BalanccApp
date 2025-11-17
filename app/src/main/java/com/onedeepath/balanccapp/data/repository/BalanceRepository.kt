package com.onedeepath.balanccapp.data.repository

import com.onedeepath.balanccapp.data.database.dao.BalanceDao
import com.onedeepath.balanccapp.data.database.entity.BalanceEntity
import com.onedeepath.balanccapp.ui.presentation.model.BalanceByMonthEntity
import com.onedeepath.balanccapp.ui.presentation.model.BalanceModel
import com.onedeepath.balanccapp.ui.presentation.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BalanceRepository @Inject constructor(
    private val balanceDao: BalanceDao
) {
    fun getAllBalancesFromDatabase(): Flow<List<BalanceModel>> {
        val response = balanceDao.getAllBalances()
        return response.map {list -> list.map {it.toDomain()} } ?: flowOf(emptyList())
    }

    fun getBalanceByIncome(year: String, month: String): Flow<List<BalanceModel>> {
        val response = balanceDao.getBalanceByIncome(year = year, month = month)
        return response.map {list -> list.map { it.toDomain()} } ?: flowOf(emptyList())
    }

    fun getBalanceByExpense(year: String, month: String): Flow<List<BalanceModel>> {
        val response = balanceDao.getBalanceByExpense(year = year, month = month)
        return response.map {list -> list.map { it.toDomain()}} ?: flowOf(emptyList())
    }

    fun getBalanceByYear(year: String) : Flow<List<BalanceByMonthEntity>> {
        return balanceDao.getBalancesByYear(year)
    }

    suspend fun insertBalance(balances: BalanceEntity) {
        balanceDao.insertAll(balances)
    }

    suspend fun deleteBalance(id: Int) {
        balanceDao.deleteBalance(id)
    }


}