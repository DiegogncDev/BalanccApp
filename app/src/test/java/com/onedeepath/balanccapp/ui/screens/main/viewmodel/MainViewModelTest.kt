package com.onedeepath.balanccapp.ui.screens.main.viewmodel

import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByExpense
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByIncome
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

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getBalanceByIncomeUseCase: GetBalanceByIncome = mockk()
    private val getBalanceByExpenseUseCase: GetBalanceByExpense = mockk()

    private val year = "2025"
    private val month = "February"
    private val previousYear = "2025"
    private val previousMonth = "January"

    private val expenses = listOf(
        BalanceModel(
            type = "expense",
            amount = 30.0,
            category = Category.FOOD,
            description = "",
            day = "01",
            month = month,
            year = year,
        ),
        BalanceModel(
            type = "expense",
            amount = 10.0,
            category = Category.TRANSPORT,
            description = "",
            day = "02",
            month = month,
            year = year,
        ),
    )

    private val incomes = listOf(
        BalanceModel(
            type = "income",
            amount = 1000.0,
            category = Category.WORK,
            description = "",
            day = "01",
            month = month,
            year = year,
        ),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = MainViewModel(
        getBalanceByIncomeUseCase = getBalanceByIncomeUseCase,
        getBalanceByExpenseUseCase = getBalanceByExpenseUseCase,
        ioDispatcher = testDispatcher,
    )

    @Test
    fun `when use cases emit balances then uiState updates totals and breakdown`() = runTest {
        // Given
        every { getBalanceByIncomeUseCase.getIncomes(year, month) } returns flowOf(incomes)
        every { getBalanceByExpenseUseCase.getExpenses(year, month) } returns flowOf(expenses)
        every { getBalanceByIncomeUseCase.getIncomes(previousYear, previousMonth) } returns flowOf(emptyList())
        every { getBalanceByExpenseUseCase.getExpenses(previousYear, previousMonth) } returns flowOf(emptyList())
        val viewModel = buildViewModel()

        // When
        viewModel.load(year = year, month = month)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1000.0, state.totalIncome, 0.001)
        assertEquals(40.0, state.totalExpense, 0.001)
        assertEquals(960.0, state.totalBalance, 0.001)
        assertEquals(2, state.expenseBreakdown.size)
        assertEquals(Category.FOOD, state.expenseBreakdown.first().category)
        assertEquals(75, state.expenseBreakdown.first().percentage)
        assertEquals(25, state.expenseBreakdown.last().percentage)
        assertNull(state.error)
    }

    @Test
    fun `when previous month has no movements then balanceChangePercent is null`() = runTest {
        // Given
        every { getBalanceByIncomeUseCase.getIncomes(year, month) } returns flowOf(incomes)
        every { getBalanceByExpenseUseCase.getExpenses(year, month) } returns flowOf(expenses)
        every { getBalanceByIncomeUseCase.getIncomes(previousYear, previousMonth) } returns flowOf(emptyList())
        every { getBalanceByExpenseUseCase.getExpenses(previousYear, previousMonth) } returns flowOf(emptyList())
        val viewModel = buildViewModel()

        // When
        viewModel.load(year = year, month = month)
        advanceUntilIdle()

        // Then
        assertNull(viewModel.uiState.value.balanceChangePercent)
    }

    @Test
    fun `when previous month has balance then uiState computes percentage vs previous month`() = runTest {
        // Given: previous balance (January 2025) = 500, current balance (February) = 1000 - 40 = 960
        // percentage = ((960 - 500) / 500) * 100 = 92
        val previousIncomes = incomes.map { it.copy(month = previousMonth) }
        every { getBalanceByIncomeUseCase.getIncomes(year, month) } returns flowOf(incomes)
        every { getBalanceByExpenseUseCase.getExpenses(year, month) } returns flowOf(expenses)
        every { getBalanceByIncomeUseCase.getIncomes(previousYear, previousMonth) } returns flowOf(
            previousIncomes.map { it.copy(amount = 500.0) },
        )
        every { getBalanceByExpenseUseCase.getExpenses(previousYear, previousMonth) } returns flowOf(emptyList())
        val viewModel = buildViewModel()

        // When
        viewModel.load(year = year, month = month)
        advanceUntilIdle()

        // Then
        assertEquals(92, viewModel.uiState.value.balanceChangePercent)
    }

    @Test
    fun `when january is selected then previous month is december of previous year`() = runTest {
        // Given
        every { getBalanceByIncomeUseCase.getIncomes("2025", "January") } returns flowOf(emptyList())
        every { getBalanceByExpenseUseCase.getExpenses("2025", "January") } returns flowOf(emptyList())
        every { getBalanceByIncomeUseCase.getIncomes("2024", "December") } returns flowOf(
            listOf(incomes.first().copy(year = "2024", month = "December")),
        )
        every { getBalanceByExpenseUseCase.getExpenses("2024", "December") } returns flowOf(emptyList())
        val viewModel = buildViewModel()

        // When
        viewModel.load(year = "2025", month = "January")
        advanceUntilIdle()

        // Then: previous balance = 1000 -> percentage = ((0 - 1000) / 1000) * 100 = -100
        assertEquals(-100, viewModel.uiState.value.balanceChangePercent)
    }

    @Test
    fun `when flows throw then uiState contains error and isLoading false`() = runTest {
        // Given
        val exception = RuntimeException("boom")
        every { getBalanceByIncomeUseCase.getIncomes(year, month) } returns flow { throw exception }
        every { getBalanceByExpenseUseCase.getExpenses(year, month) } returns flowOf(expenses)
        every { getBalanceByIncomeUseCase.getIncomes(previousYear, previousMonth) } returns flowOf(emptyList())
        every { getBalanceByExpenseUseCase.getExpenses(previousYear, previousMonth) } returns flowOf(emptyList())
        val viewModel = buildViewModel()

        // When
        viewModel.load(year = year, month = month)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("boom", viewModel.uiState.value.error)
    }

    @Test
    fun `when onErrorShown is called then error is cleared`() = runTest {
        // Given
        val exception = RuntimeException("boom")
        every { getBalanceByIncomeUseCase.getIncomes(year, month) } returns flow { throw exception }
        every { getBalanceByExpenseUseCase.getExpenses(year, month) } returns flowOf(expenses)
        every { getBalanceByIncomeUseCase.getIncomes(previousYear, previousMonth) } returns flowOf(emptyList())
        every { getBalanceByExpenseUseCase.getExpenses(previousYear, previousMonth) } returns flowOf(emptyList())
        val viewModel = buildViewModel()
        viewModel.load(year = year, month = month)
        advanceUntilIdle()

        // When
        viewModel.onErrorShown()

        // Then
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `when loading starts then previous error is cleared and isLoading true`() = runTest {
        // Given
        every { getBalanceByIncomeUseCase.getIncomes(year, month) } returns flowOf(incomes)
        every { getBalanceByExpenseUseCase.getExpenses(year, month) } returns flowOf(expenses)
        every { getBalanceByIncomeUseCase.getIncomes(previousYear, previousMonth) } returns flowOf(emptyList())
        every { getBalanceByExpenseUseCase.getExpenses(previousYear, previousMonth) } returns flowOf(emptyList())
        val viewModel = buildViewModel()

        // When
        viewModel.load(year = year, month = month)

        // Then (before advancing, load is pending)
        assertTrue(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }
}
