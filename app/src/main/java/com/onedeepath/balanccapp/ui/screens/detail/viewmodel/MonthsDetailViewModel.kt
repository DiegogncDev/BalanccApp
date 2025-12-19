package com.onedeepath.balanccapp.ui.screens.detail.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.PieEntry
import com.onedeepath.balanccapp.core.formatCurrency
import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.usecases.DeleteBalanceUseCase
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByExpense
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByIncome
import com.onedeepath.balanccapp.ui.screens.detail.model.MonthsChartUiState
import com.onedeepath.balanccapp.ui.screens.detail.model.MonthsDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthsDetailViewModel @Inject constructor(
    private val getBalanceByIncomeUseCase: GetBalanceByIncome,
    private val getBalanceByExpenseUseCase: GetBalanceByExpense,
    private val deleteBalanceUseCase: DeleteBalanceUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonthsDetailUiState())
    val uiState: StateFlow<MonthsDetailUiState> = _uiState.asStateFlow()

    fun load(year: String, monthName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingIncome = true) }

            combine(
                getBalanceByIncomeUseCase.getIncomes(year, monthName),
                        getBalanceByExpenseUseCase.getExpenses(year, monthName)

            ) { incomes, expenses ->

                MonthsDetailUiState(
                    year = year,
                    month = monthName,
                    incomes = incomes,
                    expenses = expenses,
                    totalBalanceFormatted = buildTotalBalance(incomes, expenses).first,
                    totalBalanceColor = buildTotalBalance(incomes, expenses).second,
                    incomeChart = buildIncomeChart(incomes),
                    expenseChart = buildExpenseChart(expenses),
                    isLoadingIncome = false,
                    isLoadingExpense = false
                )

            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun buildTotalBalance(
        incomes: List<BalanceModel>,
        expenses: List<BalanceModel>
    ): Pair<String, Color> {
        val total = incomes.sumOf { it.amount } - expenses.sumOf { it.amount }
        val sign = if (total > 0) "+" else ""
        val color = when {
            total > 0 -> Color(0xFF2E7D32)
            total < 0 -> Color(0xFFE53757)
            else -> Color.Black
        }
        return sign + formatCurrency(total) to color
    }

    private fun buildIncomeChart(incomes: List<BalanceModel>): MonthsChartUiState {
        val grouped = incomes.groupBy {balance -> balance.category }

        return MonthsChartUiState(
            entries = grouped.map { (category, balances) ->
                PieEntry(balances.sumOf {balance -> balance.amount }.toFloat(), category)
            },
            colors = grouped.keys.map { category -> category.color.toArgb() },
            centerText = formatCurrency(incomes.sumOf { balances -> balances.amount })
        )
    }

    private fun buildExpenseChart(expenses: List<BalanceModel>): MonthsChartUiState {
        val grouped = expenses.groupBy { balance ->  balance.category }

        return MonthsChartUiState(
            entries = grouped.map { (category, balances) ->
                PieEntry(balances.sumOf { balance ->  balance.amount }.toFloat(), category)
            },
            colors = grouped.keys.map { category ->  category.color.toArgb() },
            centerText = formatCurrency(expenses.sumOf { balance -> balance.amount })
        )
    }

    fun deleteBalance(id: Int) {
        viewModelScope.launch {
            deleteBalanceUseCase(id)
        }
    }
}




