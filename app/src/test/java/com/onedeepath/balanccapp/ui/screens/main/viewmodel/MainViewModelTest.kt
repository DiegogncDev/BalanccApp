package com.onedeepath.balanccapp.ui.screens.main.viewmodel

import com.onedeepath.balanccapp.domain.model.BalanceByMonth
import com.onedeepath.balanccapp.domain.usecases.GetBalancesByYearUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MainViewModelTest {
    private val testDispatcher = StandardTestDispatcher()


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when useCase emits balances then uiState updates months and isLoading false`() = runTest {
        //Given
        val year = "2025"
        val fakeBalanceModel = listOf<BalanceByMonth>(
            BalanceByMonth(
                month = "January",
                type = "income",
                total = 1000.0
            ),
            BalanceByMonth(
                month = "February",
                type = "expense",
                total = 500.0
            )
        )
        val flow = flowOf(fakeBalanceModel)

        val useCase = mockk<GetBalancesByYearUseCase>()

        every { useCase(year) } returns flow

        val vm = MainViewModel(
            getBalancesByYearUseCase = useCase,
            ioDispatcher = testDispatcher,
            defaultYearProvider = {year})

        // When
        advanceUntilIdle() // advance coroutines that launched in init/onYearSelected

        // Then
        assertEquals(year, vm.uiState.value.selectedYear)
        assertFalse(vm.uiState.value.isLoading)
        assertTrue(vm.uiState.value.months.isNotEmpty())
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `when usecase throws then uiState contains error and isLoading false`() = runTest {
        // Given
        val year = "year"
        val exception = RuntimeException("boom")
        val flow = flow<List<BalanceByMonth>> {throw exception}
        val usecase = mockk<GetBalancesByYearUseCase>()

        every { usecase(year) } returns flow

        val vm = MainViewModel(
            getBalancesByYearUseCase = usecase,
            ioDispatcher = testDispatcher,
            defaultYearProvider = {year})

        // When
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
        assertEquals("boom", vm.uiState.value.error)

    }

}

