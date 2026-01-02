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
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetBalanceByIncomeTest {

    @RelaxedMockK
    private lateinit var repository: BalanceRepository

    private lateinit var getBalanceByIncome: GetBalanceByIncome


    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getBalanceByIncome = GetBalanceByIncome(repository)
    }

    @Test
    fun `when getIncomes is called, then return list of balances by income from repository`() = runTest {
        // Given
        val balanceModelMock = mockk<BalanceModel>()
        val expectedList = listOf(balanceModelMock)
        val year = "2025"
        val month = "January"

        every { repository.getIncomeByMonth(year, month) } returns flowOf(expectedList)

        // When
        val result = getBalanceByIncome.getIncomes(year, month).first()

        // Then
        assert(result == expectedList)
        verify(exactly = 1) { repository.getIncomeByMonth(year, month) }

        }

    @Test
    fun `when getIncomes is called and repository returns empty list, then return empty list`() = runTest {
        //Given
        val year = "2025"
        val month = "January"
        val emptyFlow = flowOf(emptyList<BalanceModel>())

        every { repository.getIncomeByMonth(year, month) } returns emptyFlow

        // When
        val result = getBalanceByIncome.getIncomes(year, month).first()

        // Then
        assert(result.isEmpty())
        verify(exactly = 1) { repository.getIncomeByMonth(year, month) }

    }
    @Test
    fun `when year and month are not provided, then return empty list`() = runTest {
        // GIVEN
        val year = ""
        val month = ""

        // WHEN
        val result = getBalanceByIncome.getIncomes(year, month).first()

        // THEN
        assertTrue(result.isEmpty())
        verify(exactly = 0) { repository.getIncomeByMonth(year, month) }

    }

}