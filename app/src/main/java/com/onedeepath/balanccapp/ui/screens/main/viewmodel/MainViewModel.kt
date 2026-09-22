package com.onedeepath.balanccapp.ui.screens.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onedeepath.balanccapp.core.ENGLISH_MONTHS
import com.onedeepath.balanccapp.di.IoDispatcher
import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByExpense
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByIncome
import com.onedeepath.balanccapp.ui.screens.main.model.HomeCategoryItem
import com.onedeepath.balanccapp.ui.screens.main.model.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBalanceByIncomeUseCase: GetBalanceByIncome,
    private val getBalanceByExpenseUseCase: GetBalanceByExpense,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun onErrorShown() {
        _uiState.update { it.copy(error = null) }
    }

    fun load(year: String, month: String) {
        loadJob?.cancel()
        _uiState.update { it.copy(isLoading = true, error = null) }

        val (previousMonth, previousYear) = previousMonthYear(month = month, year = year)

        loadJob = viewModelScope.launch {
            combine(
                getBalanceByIncomeUseCase.getIncomes(year = year, month = month).flowOn(ioDispatcher),
                getBalanceByExpenseUseCase.getExpenses(year = year, month = month).flowOn(ioDispatcher),
                getBalanceByIncomeUseCase.getIncomes(year = previousYear, month = previousMonth).flowOn(ioDispatcher),
                getBalanceByExpenseUseCase.getExpenses(year = previousYear, month = previousMonth).flowOn(ioDispatcher),
            ) { incomes, expenses, previousIncomes, previousExpenses ->
                val totalIncome = incomes.sumOf { it.amount }
                val totalExpense = expenses.sumOf { it.amount }
                val balance = totalIncome - totalExpense
                val previousBalance = previousIncomes.sumOf { it.amount } - previousExpenses.sumOf { it.amount }

                MainUiState(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    balanceChangePercent = balanceChangePercent(balance = balance, previousBalance = previousBalance),
                    expenseBreakdown = expenses.toExpenseBreakdown(),
                    isLoading = false,
                )
            }
                .catch { throwable ->
                    _uiState.update { it.copy(isLoading = false, error = throwable.message) }
                }
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }

    private fun previousMonthYear(month: String, year: String): Pair<String, String> {
        val monthIndex = ENGLISH_MONTHS.indexOf(month).takeIf { it >= 0 } ?: 0
        return if (monthIndex == 0) {
            ENGLISH_MONTHS.last() to ((year.toIntOrNull() ?: 0) - 1).toString()
        } else {
            ENGLISH_MONTHS[monthIndex - 1] to year
        }
    }

    private fun balanceChangePercent(balance: Double, previousBalance: Double): Int? {
        if (previousBalance == 0.0) return null
        return (((balance - previousBalance) / abs(previousBalance)) * 100).roundToInt()
    }

    private fun List<BalanceModel>.toExpenseBreakdown(): List<HomeCategoryItem> {
        val total = sumOf { it.amount }
        return groupBy { it.category }
            .map { (category, balances) ->
                val categoryTotal = balances.sumOf { it.amount }
                HomeCategoryItem(
                    category = category,
                    amount = categoryTotal,
                    percentage = if (total > 0) ((categoryTotal / total) * 100).toInt() else 0,
                )
            }
            .sortedByDescending { it.amount }
    }
}
