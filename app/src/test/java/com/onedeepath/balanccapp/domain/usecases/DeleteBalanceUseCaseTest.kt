package com.onedeepath.balanccapp.domain.usecases

import com.onedeepath.balanccapp.domain.repository.BalanceRepository
import io.mockk.MockKAnnotations
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeleteBalanceUseCaseTest {

    @RelaxedMockK
    private lateinit var repository: BalanceRepository

    private lateinit var deleteBalanceUseCase: DeleteBalanceUseCase


    @Before
    fun onBefore() {
        MockKAnnotations.init(this)
        deleteBalanceUseCase = DeleteBalanceUseCase(repository)
    }

    @Test
    fun `invoke calls repository deleteBalance method`() = runTest {
        //GIVEN
        val id = 1

        coJustRun { repository.deleteBalance(id) }

        //WHEN
        deleteBalanceUseCase(id)

        //THEN
        coVerify(exactly = 1) { repository.deleteBalance(id) }
    }
    
}