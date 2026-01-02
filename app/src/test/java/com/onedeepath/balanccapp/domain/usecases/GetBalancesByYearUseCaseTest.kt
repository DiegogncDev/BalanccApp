package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.model.BalanceByMonth
import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetBalancesByYearUseCaseTest {

    @RelaxedMockK
    private lateinit var repository: BalanceRepository
    private lateinit var getBalancesByYearUseCase: GetBalancesByYearUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        getBalancesByYearUseCase = GetBalancesByYearUseCase(repository)
    }

    @Test
    fun `when useCase is invoked then return the balances by year from repository`() = runTest {
        // Given
        val year = "2025"
        val balanceModelMockked = mockk<BalanceByMonth>()
        val expectedList = listOf(balanceModelMockked)

        every { repository.getBalanceByYear(year) } returns flowOf(expectedList)

        // When
        val result = getBalancesByYearUseCase(year).first()

        //Then
        assert(result == expectedList)
        verify(exactly = 1) { repository.getBalanceByYear(year) }
    }

    @Test
    fun `when repository is empty then return empty list of balances by year from repository`() = runTest {
        // Given
        val year = "2025"
        every { repository.getBalanceByYear(year) } returns flowOf(emptyList())

        // When
        val response = getBalancesByYearUseCase(year).first()

        // Then
        assert(response.isEmpty())
        verify(exactly = 1) { repository.getBalanceByYear(year) }

    }

    @Test
    fun `invoke with empty year returns empty list and does not interact with repository`() = runTest {
        //Given
        val emptyYear = ""

        //When
        val result = getBalancesByYearUseCase(emptyYear).first()

        //Then
        assertTrue(result.isEmpty())
        verify(exactly = 0) { repository.getBalanceByYear(emptyYear) }
    }

}