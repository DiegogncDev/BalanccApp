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
import org.junit.Before
import org.junit.Test

class GetAllBalancesUseCaseTest {

    @RelaxedMockK
    private lateinit var repository: BalanceRepository

    private lateinit var getAllBalancesUseCase: GetAllBalancesUseCase

    @Before
    fun onBefore(){
        MockKAnnotations.init(this)
        getAllBalancesUseCase = GetAllBalancesUseCase(repository)

    }

    @Test
    fun `when useCase is invoked then return list of balances from repository`() = runBlocking {
        //Given
        val balanceModelMock = mockk<BalanceModel>()
        val expectedList = listOf(balanceModelMock)

        every { repository.getAllBalances() } returns flowOf(expectedList)

        //When
        val result = getAllBalancesUseCase().first()

        //Then
        assert(result == expectedList)
        verify(exactly = 1) { repository.getAllBalances() }

    }

    @Test
    fun `when repository is empty then return empty list of balances from repository`() = runBlocking {
        //Given
        every { repository.getAllBalances() } returns flowOf(emptyList())

        //When
        val response = getAllBalancesUseCase().first()

        //Then
        assert(response.isEmpty())
        verify(exactly = 1) { repository.getAllBalances() }
    }

}

