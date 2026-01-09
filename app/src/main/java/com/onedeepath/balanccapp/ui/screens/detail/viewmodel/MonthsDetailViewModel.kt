package com.onedeepath.balanccapp.ui.screens.detail.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onedeepath.balanccapp.core.CurrencyHelper
import com.onedeepath.balanccapp.di.IoDispatcher
import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.usecases.DeleteBalanceUseCase
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByExpense
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByIncome
import com.onedeepath.balanccapp.ui.screens.detail.model.MonthsDetailUiState
import com.onedeepath.balanccapp.ui.screens.detail.model.MyMonthsChartUiState
import com.onedeepath.balanccapp.ui.screens.detail.model.PieChartData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthsDetailViewModel @Inject constructor(
    private val getBalanceByIncomeUseCase: GetBalanceByIncome,
    private val getBalanceByExpenseUseCase: GetBalanceByExpense,
    private val deleteBalanceUseCase: DeleteBalanceUseCase,
    private val currencyHelper: CurrencyHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonthsDetailUiState())
    val uiState: StateFlow<MonthsDetailUiState> = _uiState.asStateFlow()

    fun load(year: String, monthName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingIncome = true) }

            combine(
                getBalanceByIncomeUseCase.getIncomes(year, monthName).flowOn(ioDispatcher),
                        getBalanceByExpenseUseCase.getExpenses(year, monthName).flowOn(ioDispatcher)

            ) { incomes, expenses ->

                val (formattedBalance, formattedColor) = buildTotalBalance(incomes, expenses)

                MonthsDetailUiState(
                    year = year,
                    month = monthName,
                    incomes = incomes,
                    expenses = expenses,
                    totalBalanceFormatted = formattedBalance,
                    totalBalanceColor = formattedColor,
                    incomeChart = MyBuildIncomeChart(incomes),
                    expenseChart = MyBuildExpenseChart(expenses),
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
        return sign + currencyHelper.formatCurrency(total) to color
    }

    fun deleteBalance(id: Int) {
        viewModelScope.launch {
            deleteBalanceUseCase(id)
        }
    }

    private fun MyBuildIncomeChart(incomes: List<BalanceModel>): MyMonthsChartUiState {
        val grouped = incomes.groupBy {balance -> balance.category }

        return MyMonthsChartUiState(
            entries = grouped.map { (category, balances) ->
                PieChartData(balances.sumOf { balance -> balance.amount }.toFloat(), category)
            },
            colors = grouped.keys.map { category -> category.color.toArgb() },
            centerText = currencyHelper.formatCurrency(incomes.sumOf { balances -> balances.amount })
        )
    }

    private fun MyBuildExpenseChart(expenses: List<BalanceModel>): MyMonthsChartUiState {
        val grouped = expenses.groupBy { balance ->  balance.category }

        return MyMonthsChartUiState(
            entries = grouped.map { (category, balances) ->
                PieChartData(balances.sumOf { balance ->  balance.amount }.toFloat(), category)
            },
            colors = grouped.keys.map { category ->  category.color.toArgb() },
            centerText = currencyHelper.formatCurrency(expenses.sumOf { balance -> balance.amount })
        )

    }
}

//private fun buildIncomeChart(incomes: List<BalanceModel>): MonthsChartUiState {
//        val grouped = incomes.groupBy {balance -> balance.category }
//
//        return MonthsChartUiState(
//            entries = grouped.map { (category, balances) ->
//                PieEntry(balances.sumOf {balance -> balance.amount }.toFloat(), category)
//            },
//            colors = grouped.keys.map { category -> category.color.toArgb() },
//            centerText = currencyHelper.formatCurrency(incomes.sumOf { balances -> balances.amount })
//        )
//    }
//
//    private fun buildExpenseChart(expenses: List<BalanceModel>): MonthsChartUiState {
//        val grouped = expenses.groupBy { balance ->  balance.category }
//
//        return MonthsChartUiState(
//            entries = grouped.map { (category, balances) ->
//                PieEntry(balances.sumOf { balance ->  balance.amount }.toFloat(), category)
//            },
//            colors = grouped.keys.map { category ->  category.color.toArgb() },
//            centerText = currencyHelper.formatCurrency(expenses.sumOf { balance -> balance.amount })
//        )
//    }




