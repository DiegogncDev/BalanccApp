package com.onedeepath.balanccapp.data.repository

import app.cash.turbine.test
import com.onedeepath.balanccapp.data.database.dao.BalanceDao
import com.onedeepath.balanccapp.data.database.entity.BalanceEntity
import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.model.Category
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BalanceRepositoryImplTest {

    private val balanceDao: BalanceDao = mockk()
    private lateinit var repository: BalanceRepositoryImpl

    @Before
    fun setUp() {
        repository = BalanceRepositoryImpl(balanceDao)
    }

    @Test
    fun `getAllBalances should return mapped domain models`() = runTest {

        // GIVEN: Dao returns entities
        val entities = listOf(BalanceEntity(id = 1, amount = 1000.0, category = Category.EDUCATION, month = "January", day = "01", year = "2025", type = "income", description = "")) // Asumiendo tus nombres
        coEvery { balanceDao.getAllBalances() } returns flowOf(entities)

        // WHEN: Calling to repository
        repository.getAllBalances().test {

            // THEN: Take the balanced mapped to domain
            val result = awaitItem()
            assert(result[0].id == 1)
            assert(result is List<BalanceModel>)
            awaitComplete()
        }
    }

    @Test
    fun `insertBalance should call dao with converted entity`() = runTest {
        // GIVEN
        val domainModel = BalanceModel(id = 1, amount = 1000.0, category = Category.EDUCATION, month = "January", day = "01", year = "2025", type = "income", description = "")
        coEvery { balanceDao.insertAll(any()) } just Runs

        // WHEN
        repository.insertBalance(domainModel)

        // THEN
        coVerify { balanceDao.insertAll(match { it.id == 1 }) }
    }

    @Test
    fun `getAllBalances returns empty list when DAO is empty`() = runTest {
        // GIVEN: Dao emits empty list
        coEvery { balanceDao.getAllBalances() } returns flowOf(emptyList())

        // WHEN: Calling to repository
        repository.getAllBalances().test {
            val result = awaitItem()

            // THEN: List should be empty no null
            assert(result.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getIncomeByMonth returns empty list when no data matches year and month`() = runTest {
        // GIVEN
        val year = "2023"
        val month = "December"
        coEvery { balanceDao.getBalanceByIncome(year, month) } returns flowOf(emptyList())

        // WHEN
        repository.getIncomeByMonth(year, month).test {
            val result = awaitItem()

            // THEN
            assert(result.isEmpty())
            awaitComplete()
        }

        // Verify that the DAO method was called
        verify { balanceDao.getBalanceByIncome(year, month) }
    }

    @Test
    fun `getBalancesByYear returns empty list when year has no records`() = runTest {
        val year = "2025"
        coEvery { balanceDao.getBalancesByYear(year) } returns flowOf(emptyList())

        repository.getBalanceByYear(year).test {
            val result = awaitItem()
            assert(result.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `getAllBalances emits new data when database updates`() = runTest {
        val list1 = emptyList<BalanceEntity>()
        val list2 = listOf(BalanceEntity(id = 1, amount = 10.0, category = Category.EDUCATION, month = "January", day = "01", year = "2025", type = "income", description = ""))

        val flow = MutableStateFlow(list1)
        coEvery { balanceDao.getAllBalances() } returns flow

        repository.getAllBalances().test {
            // First emission should be empty
            assert(awaitItem().isEmpty())

            // Simulate add in db
            flow.value = list2

            // Repository should be emit the new list automatically
            val secondEmit = awaitItem()
            assert(secondEmit.size == 1)
            assert(secondEmit[0].amount == 10.0)
        }
    }
}