package com.onedeepath.balanccapp.ui.screens.addbalance.viewmodel

import app.cash.turbine.test
import com.onedeepath.balanccapp.domain.usecases.InsertBalanceUseCase
import com.onedeepath.balanccapp.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddBalanceViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val insertBalanceUseCase: InsertBalanceUseCase = mockk(relaxed = true)
    private lateinit var viewModel: AddBalanceViewModel

    @Before
    fun setup() {
        viewModel = AddBalanceViewModel(insertBalanceUseCase)
    }

    @Test
    fun `when onAmountChange is called, then state is updated and valid`() = runTest {
        // Given
        viewModel.uiState.test {
            // Initial state
            val initialState = awaitItem()
            assertFalse(initialState.isValid)

            //When
            viewModel.onAmountChange("100.0")

            //Then
            val updatedState = awaitItem()
            assertEquals("100.0", updatedState.amount)
            assertTrue(updatedState.isValid)
        }
    }

    @Test
    fun `When save is success, then call insertBalanceUseCase with the correct data`() = runTest {
        // GIVEN
        viewModel.onAmountChange("50.0")
        viewModel.onDaySelected("15")

        // WHEN
        viewModel.save("2025", "05")

        // THEN
        coVerify {
            insertBalanceUseCase(match {
                it.amount == 50.0 && it.year == "2025" && it.day == "15"
            })
        }
    }

    @Test
    fun `when insertBalanceUseCase throws an exception, then the state show it and leave the loading`() = runTest {
        // GIVEN
        val errorMessage = "Database Error"
        coEvery { insertBalanceUseCase(any()) } throws Exception(errorMessage)

        viewModel.onAmountChange("100")
        viewModel.onDaySelected("10")

        // WHEN
        viewModel.save("2025", "January")

        // THEN
        viewModel.uiState.test {
            val finalState = expectMostRecentItem()
            assertEquals(errorMessage, finalState.error)
            assertFalse(finalState.isSaving)
            assertFalse(finalState.saveSuccess)
        }
    }

    @Test
    fun `When data are invalid, then don't call insertBalanceUseCase, and show the error`() = runTest {
        // GIVEN: Amount invalid and Day are not selected
        viewModel.onAmountChange("")

        // WHEN
        viewModel.save("2025", "January")

        // THEN
        coVerify(exactly = 0) { insertBalanceUseCase(any()) }

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals("Invalid data", state.error)
        }
    }

    @Test
    fun `When save is called, then isSaving should be true`() = runTest {
        // GIVEN
        viewModel.onAmountChange("100")
        viewModel.onDaySelected("10")

        // WHEN
        viewModel.uiState.test {
            //Initial state
            awaitItem()

            // When
            viewModel.save("2025", "January")

            //Then
            val loadingState = awaitItem()
            assertTrue("State should be loading", loadingState.isSaving)

            val finalState = awaitItem()
            assertFalse("State should not be loading after save", finalState.isSaving)
            assertTrue(finalState.saveSuccess)

        }
    }

    @Test
    fun `When call resetSaveEvent,then saveSuccess and error return to null o false`() = runTest {
        // GIVEN: A state with success and error before
        viewModel.onAmountChange("100")
        viewModel.onDaySelected("10")
        viewModel.save("2025", "January") // result in success

        // WHEN
        viewModel.resetSaveEvent()

        // THEN
        val state = viewModel.uiState.value
        assertFalse(state.saveSuccess)
        assertNull(state.error)
    }

    @Test
    fun `When onTypeChange is true, the model sent to insertBalanceUseCase is income type`() = runTest {
        // GIVEN
        viewModel.onTypeChange(true) // is income
        viewModel.onAmountChange("100")
        viewModel.onDaySelected("1")

        // WHEN
        viewModel.save("2025", "01")

        // THEN
        coVerify {
            insertBalanceUseCase(match { it.type == "income" })
        }
    }


}