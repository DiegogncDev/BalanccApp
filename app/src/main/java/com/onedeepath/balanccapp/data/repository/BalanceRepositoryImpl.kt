package com.onedeepath.balanccapp.data.repository

import com.onedeepath.balanccapp.data.database.dao.BalanceDao
import com.onedeepath.balanccapp.data.database.entity.toEntity
import com.onedeepath.balanccapp.domain.model.BalanceByMonth
import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.model.toDomain
import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import com.onedeepath.balanccapp.data.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BalanceRepositoryImpl @Inject constructor(
    private val balanceDao: BalanceDao
) : BalanceRepository {
    override fun getAllBalances(): Flow<List<BalanceModel>> {
        val response = balanceDao.getAllBalances()
        return response.map {list -> list.map {it.toDomain()} }
    }

    override fun getIncomeByMonth(year: String, month: String): Flow<List<BalanceModel>> {
        val response = balanceDao.getBalanceByIncome(year = year, month = month)
        return response.map {list -> list.map { it.toDomain()} }
    }

    override fun getExpenseByMonth(year: String, month: String): Flow<List<BalanceModel>> {
        val response = balanceDao.getBalanceByExpense(year = year, month = month)
        return response.map {list -> list.map { it.toDomain()}}
    }

    override fun getBalanceByYear(year: String
    ): Flow<List<BalanceByMonth>> = balanceDao.getBalancesByYear(year).map {
        list -> list.map { it.toDomain() }
    }

    override suspend fun insertBalance(balances: BalanceModel) {
        balanceDao.insertAll(balances.toEntity())
    }

    override suspend fun deleteBalance(id: Int) {
        balanceDao.deleteBalance(id)
    }

}