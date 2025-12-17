package com.onedeepath.balanccapp.ui.screens.detail.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.PieEntry
import com.onedeepath.balanccapp.core.formatCurrency
import com.onedeepath.balanccapp.data.repository.BalanceRepository
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByExpense
import com.onedeepath.balanccapp.domain.usecases.GetBalanceByIncome
import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.ui.presentation.model.Categories
import com.onedeepath.balanccapp.ui.screens.detail.model.MonthsChartUiState
import com.onedeepath.balanccapp.ui.screens.detail.model.MonthsDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthsDetailViewModel @Inject constructor(
    private val repository: BalanceRepository,
    private val getBalanceByIncomeUseCase: GetBalanceByIncome,
    private val getBalanceByExpenseUseCase: GetBalanceByExpense,
) : ViewModel() {

    private var _incomesList: List<BalanceModel> = emptyList()
    private var _expensesList: List<BalanceModel> = emptyList()
    private val _uiState = MutableStateFlow(MonthsDetailUiState())
    val uiState: StateFlow<MonthsDetailUiState> = _uiState.asStateFlow()

    fun load(year: String, monthName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingIncome = true, isLoadingExpense = true) }

            getBalanceByIncomeUseCase.getIncomes(year, monthName).collect { incomes ->
                _incomesList = incomes
            }
            getBalanceByExpenseUseCase.getExpenses(year, monthName).collect { expenses ->
                _expensesList = expenses
            }

            _uiState.value = MonthsDetailUiState(
                year = year,
                month = monthName,
                incomes = _incomesList,
                expenses = _expensesList,
                totalBalanceFormatted = buildTotalBalance(_incomesList, _expensesList).first,
                totalBalanceColor = buildTotalBalance(_incomesList, _expensesList).second,
                incomeChart = buildIncomeChart(_incomesList),
                expenseChart = buildExpenseChart(_expensesList),
                isLoadingIncome = false,
                isLoadingExpense = false
            )
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
        val grouped = incomes.groupBy { it.category }

        return MonthsChartUiState(
            entries = grouped.map { (category, items) ->
                PieEntry(items.sumOf { it.amount }.toFloat(), category)
            },
            colors = grouped.keys.map { getColorCategoryByName(it).color.toArgb() },
            centerText = formatCurrency(incomes.sumOf { it.amount })
        )
    }

    private fun buildExpenseChart(expenses: List<BalanceModel>): MonthsChartUiState {
        val grouped = expenses.groupBy { it.category }

        return MonthsChartUiState(
            entries = grouped.map { (category, items) ->
                PieEntry(items.sumOf { it.amount }.toFloat(), category)
            },
            colors = grouped.keys.map { getColorCategoryByName(it).color.toArgb() },
            centerText = formatCurrency(expenses.sumOf { it.amount })
        )
    }

    fun deleteBalance(id: Int) {
        viewModelScope.launch {
            repository.deleteBalance(id)
        }
    }

    fun getColorCategoryByName(category: Category) : Categories {

        return when(category) {
            Category.INVESTMENT -> Categories.Investment
            Category.WORK -> Categories.Work
            Category.GIFT -> Categories.Gift
            Category.GROCERY -> Categories.Grocery
            Category.ENTERTAINMENT -> Categories.Entertainment
            Category.TRANSPORT -> Categories.Transport
            Category.UTILITIES -> Categories.Utilities
            Category.RENT -> Categories.Rent
            Category.HEALTH -> Categories.Health
            Category.TRAVEL -> Categories.Travel
            Category.FOOD -> Categories.Food
            Category.EDUCATION -> Categories.Education
            Category.PET -> Categories.Pet
            Category.OTHER -> Categories.Other
        }

    }
}




