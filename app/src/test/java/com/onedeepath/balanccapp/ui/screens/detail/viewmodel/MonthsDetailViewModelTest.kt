package com.onedeepath.balanccapp.ui.screens.detail.viewmodel

import app.cash.turbine.test
import com.onedeepath.balanccapp.core.CurrencyHelper
import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.domain.usecases.DeleteBalanceUseCase
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByExpense
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByIncome
import com.onedeepath.balanccapp.ui.screens.detail.model.BalanceSign
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class MonthsDetailViewModelTest {
    private val getBalanceByIncomeUseCase = mockk<GetBalanceByIncome>()
    private val getBalanceByExpenseUseCase = mockk<GetBalanceByExpense>()
    private val deleteBalanceUseCase = mockk<DeleteBalanceUseCase>()
    private val currencyHelper = mockk<CurrencyHelper>()

    // Dispatcher para pruebas
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: MonthsDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Inicializamos el ViewModel pasando el testDispatcher como el ioDispatcher
        viewModel = MonthsDetailViewModel(
            getBalanceByIncomeUseCase,
            getBalanceByExpenseUseCase,
            deleteBalanceUseCase,
            currencyHelper,
            testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `load should update uiState with correct data and formatted balance`() = runTest {
        // GIVEN: Datos de prueba
        val year = "2025"
        val month = "January"
        val mockIncomes = listOf(
            BalanceModel(
                id = 1,
                amount = 1000.0,
                category = Category.EDUCATION,
                description = "Salary",
                day = "01",
                month = "January",
                year = "2025",
                type = "income"
            )
        )
        val mockExpenses = listOf(BalanceModel(id = 2, amount = 400.0,
            category = Category.EDUCATION,
            description = "Books",
            day = "01",
            month = "January",
            year = "2025",
            type = "expense"))

        every { getBalanceByIncomeUseCase.getIncomes(year, month) } returns flowOf(mockIncomes)
        every { getBalanceByExpenseUseCase.getExpenses(year, month) } returns flowOf(mockExpenses)
        // Define for each amount because the test iterate all amount of the mock
        every { currencyHelper.formatCurrency(600.0) } returns "$600.00"
        every { currencyHelper.formatCurrency(1000.0) } returns "$1000.00"
        every { currencyHelper.formatCurrency(400.0) } returns "$400.00"

        // WHEN:
        viewModel.load(year, month)

        // THEN: Verificamos el estado final
        viewModel.uiState.test {
            // First state collect data before the upload, need to predefine in MonthsDetailUi
            val state = awaitItem()
            assertEquals(year, state.year)
            assertEquals(month, state.month)
            assertEquals(mockIncomes, state.incomes)
            assertEquals(mockExpenses, state.expenses)
            assertEquals("+$600.00", state.totalBalanceFormatted)
            assertEquals(BalanceSign.POSITIVE, state.totalBalanceSign)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteBalance should call deleteBalanceUseCase`() = runTest {
        // GIVEN
        val idToDelete = 10
        coEvery { deleteBalanceUseCase(idToDelete) } returns Unit

        // WHEN
        viewModel.deleteBalance(idToDelete)

        // THEN
        coVerify(exactly = 1) { deleteBalanceUseCase(idToDelete) }
    }

    @Test
    fun `load with multiple items of same category should group them in chart`() = runTest {
        val year = "2025"
        val month = "January"

        // GIVEN: Dos ingresos de la misma categoría
        val mockIncomes = listOf(
            BalanceModel(id = 1, amount = 1000.0, category = Category.EDUCATION, month = month, day = "January", year = year, type = "income", description = ""),
            BalanceModel(id = 2, amount = 500.0, category = Category.EDUCATION, month = month, day = "January", year = year, type = "income", description = "")
        )
        // Gastos mayores para probar el balance negativo
        val mockExpenses = listOf(
            BalanceModel(id = 3, amount = 2000.0, category = Category.FOOD, month = month, day = "January", year = year, type = "expense", description = "")
        )

        every { getBalanceByIncomeUseCase.getIncomes(any(), any()) } returns flowOf(mockIncomes)
        every { getBalanceByExpenseUseCase.getExpenses(any(), any()) } returns flowOf(mockExpenses)

        // Mocks para los totales: 1500 (income), 2000 (expense), -500 (total)
        every { currencyHelper.formatCurrency(1500.0) } returns "$1,500.00"
        every { currencyHelper.formatCurrency(2000.0) } returns "$2,000.00"
        every { currencyHelper.formatCurrency(-500.0) } returns "$500.00"

        // WHEN
        viewModel.load(year, month)

        // THEN
        viewModel.uiState.test {
            val state = expectMostRecentItem()

            // Verificar signo negativo
            assertEquals(BalanceSign.NEGATIVE, state.totalBalanceSign)

            // Verificar que el chart de ingresos solo tenga 1 entrada (agrupada)
            assertEquals(1, state.incomeChart.entries.size)
            assertEquals(1500f, state.incomeChart.entries[0].value)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when amount is negative then total balance should be red`() = runTest {
        val year = "2025"
        val month = "January"

        // GIVEN: Dos ingresos de la misma categoría
        val mockIncomes = listOf(
            BalanceModel(id = 1, amount = 1000.0, category = Category.EDUCATION, month = month, day = "January", year = year, type = "income", description = ""),
            BalanceModel(id = 2, amount = 500.0, category = Category.EDUCATION, month = month, day = "January", year = year, type = "income", description = "")
        )
        // Gastos mayores para probar balance negativo
        val mockExpenses = listOf(
            BalanceModel(id = 3, amount = 2000.0, category = Category.FOOD, month = month, day = "January", year = year, type = "expense", description = "")
        )

        every { getBalanceByIncomeUseCase.getIncomes(any(), any()) } returns flowOf(mockIncomes)
        every { getBalanceByExpenseUseCase.getExpenses(any(), any()) } returns flowOf(mockExpenses)

        // Mocks for total result: 1500 (income), 2000 (expense), -500 (total)
        every { currencyHelper.formatCurrency(1500.0) } returns "$1,500.00"
        every { currencyHelper.formatCurrency(2000.0) } returns "$2,000.00"
        // Result is -500
        every { currencyHelper.formatCurrency(-500.0) } returns "$500.00"

        // WHEN
        viewModel.load(year, month)

        // THEN
        viewModel.uiState.test {
            val state = expectMostRecentItem()

            // Verificar signo negativo
            assertEquals(BalanceSign.NEGATIVE, state.totalBalanceSign)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when amount is positive then total balance should be green`() = runTest {
        val year = "2025"
        val month = "January"

        // GIVEN: Dos ingresos de la misma categoría
        val mockIncomes = listOf(
            BalanceModel(id = 1, amount = 1000.0, category = Category.EDUCATION, month = month, day = "January", year = year, type = "income", description = ""),
            BalanceModel(id = 2, amount = 500.0, category = Category.EDUCATION, month = month, day = "January", year = year, type = "income", description = "")
        )
        // Gastos menores para probar balance positivo
        val mockExpenses = listOf(
            BalanceModel(id = 3, amount = 1000.0, category = Category.FOOD, month = month, day = "January", year = year, type = "expense", description = "")
        )

        every { getBalanceByIncomeUseCase.getIncomes(any(), any()) } returns flowOf(mockIncomes)
        every { getBalanceByExpenseUseCase.getExpenses(any(), any()) } returns flowOf(mockExpenses)

        // Mocks para los totales: 1500 (income), 2000 (expense), -500 (total)
        every { currencyHelper.formatCurrency(1500.0) } returns "$1,500.00"
        every { currencyHelper.formatCurrency(1000.0) } returns "$1,000.00"
        every { currencyHelper.formatCurrency(500.0) } returns "$500.00"

        // WHEN
        viewModel.load(year, month)

        // THEN
        viewModel.uiState.test {
            val state = expectMostRecentItem()
            // Verificar signo positivo
            assertEquals(BalanceSign.POSITIVE, state.totalBalanceSign)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when total balance is zero then sign is neutral`() = runTest {
        val year = "2025"
        val month = "January"

        // GIVEN: ingresos iguales a gastos (total = 0)
        val mockIncomes = listOf(
            BalanceModel(id = 1, amount = 500.0, category = Category.WORK, month = month, day = "01", year = year, type = "income", description = "")
        )
        val mockExpenses = listOf(
            BalanceModel(id = 2, amount = 500.0, category = Category.FOOD, month = month, day = "02", year = year, type = "expense", description = "")
        )

        every { getBalanceByIncomeUseCase.getIncomes(year, month) } returns flowOf(mockIncomes)
        every { getBalanceByExpenseUseCase.getExpenses(year, month) } returns flowOf(mockExpenses)

        every { currencyHelper.formatCurrency(0.0) } returns "$0.00"
        every { currencyHelper.formatCurrency(500.0) } returns "$500.00"

        // WHEN
        viewModel.load(year, month)

        // THEN
        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(BalanceSign.NEUTRAL, state.totalBalanceSign)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when no movements then lists and charts are empty and sign is neutral`() = runTest {
        val year = "2025"
        val month = "January"

        // GIVEN: mes sin movimientos
        every { getBalanceByIncomeUseCase.getIncomes(year, month) } returns flowOf(emptyList())
        every { getBalanceByExpenseUseCase.getExpenses(year, month) } returns flowOf(emptyList())
        every { currencyHelper.formatCurrency(0.0) } returns "$0.00"

        // WHEN
        viewModel.load(year, month)

        // THEN
        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(0, state.incomes.size)
            assertEquals(0, state.expenses.size)
            assertEquals(0, state.incomeChart.entries.size)
            assertEquals(0, state.expenseChart.entries.size)
            assertEquals(BalanceSign.NEUTRAL, state.totalBalanceSign)
            assertFalse(state.isLoadingIncome)
            assertFalse(state.isLoadingExpense)
            cancelAndIgnoreRemainingEvents()
        }
    }

}