package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.collections.listOf

class GetBalanceByExpenseTest {

    @RelaxedMockK
    private lateinit var repository: BalanceRepository

    private lateinit var getBalanceByExpense: GetBalanceByExpense


    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getBalanceByExpense = GetBalanceByExpense(repository)
    }

    @Test
    fun `when getExpense is called, then return list of balances by expense from repository`() = runTest {
        // Given
        val balanceModelMock = mockk<BalanceModel>()
        val expectedList = listOf(balanceModelMock)
        val year = "2025"
        val month = "January"

        every { repository.getExpenseByMonth(year, month) } returns flowOf(expectedList)

        // WHEN
        val result = getBalanceByExpense.getExpenses(year, month).first()

        // THEN
        assert(result == expectedList)
        verify(exactly = 1) { repository.getExpenseByMonth(year, month) }
    }

    @Test
    fun `when getExpense is called and repository returns empty list, then return empty list`() = runTest {
        // GIVE
        val year = "2025"
        val month = "January"
        val emptyFlow = flowOf(emptyList<BalanceModel>())

        every { repository.getExpenseByMonth(year, month) } returns emptyFlow

        // WHEN
        val response = getBalanceByExpense.getExpenses(year, month).first()

        // THEN
        assert(response.isEmpty())
        verify(exactly = 1) { repository.getExpenseByMonth(year, month) }

    }

    @Test
    fun `when year or month are blank, then return empty list`() = runTest {
        // GIVEN
        val year = "2025"
        val month = ""

        // WHEN
        val result = getBalanceByExpense.getExpenses(year, month).first()

        // THEN
        assertTrue(result.isEmpty())
        verify(exactly = 0) { repository.getExpenseByMonth(year, month) }

    }


    @Test
    fun `when year and month are not provided, then return empty list`() = runTest {
        // GIVEN
        val year = ""
        val month = ""

        // WHEN
        val result = getBalanceByExpense.getExpenses(year, month).first()

        // THEN
        assertTrue(result.isEmpty())
        verify(exactly = 0) { repository.getExpenseByMonth(year, month) }

    }

}