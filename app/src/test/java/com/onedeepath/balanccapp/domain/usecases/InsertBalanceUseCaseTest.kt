package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import io.mockk.MockKAnnotations
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class InsertBalanceUseCaseTest {

    @RelaxedMockK
    private lateinit var repository: BalanceRepository

    private lateinit var insertBalanceUseCase: InsertBalanceUseCase

    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        insertBalanceUseCase = InsertBalanceUseCase(repository)
    }

    @Test
    fun `invoke calls repository insertBalance method`() = runTest {
        //Given
        val balanceModelMock = mockk<BalanceModel>()

        coJustRun { repository.insertBalance(balanceModelMock) }

        //When
        insertBalanceUseCase(balanceModelMock)

        //Then
        coVerify(exactly = 1) { repository.insertBalance(balanceModelMock) }
    }



}