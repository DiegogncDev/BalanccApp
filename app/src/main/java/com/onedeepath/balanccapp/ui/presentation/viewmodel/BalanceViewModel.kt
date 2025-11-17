package com.onedeepath.balanccapp.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onedeepath.balanccapp.data.database.entity.BalanceEntity
import com.onedeepath.balanccapp.domain.DeleteBalanceUseCase
import com.onedeepath.balanccapp.domain.GetAllBalancesUseCase
import com.onedeepath.balanccapp.domain.GetBalanceByExpense
import com.onedeepath.balanccapp.domain.GetBalanceByIncome
import com.onedeepath.balanccapp.domain.GetBalancesByYearUseCase
import com.onedeepath.balanccapp.domain.InsertBalanceUseCase
import com.onedeepath.balanccapp.ui.presentation.model.BalanceByMonthEntity
import com.onedeepath.balanccapp.ui.presentation.model.BalanceModel
import com.onedeepath.balanccapp.ui.presentation.model.Categories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.annotation.meta.When
import javax.inject.Inject


@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val getAllBalancesUseCase: GetAllBalancesUseCase,
    private val getBalanceByIncomeUseCase: GetBalanceByIncome,
    private val getBalanceByExpenseUseCase: GetBalanceByExpense,
    private val insertBalanceUseCase: InsertBalanceUseCase,
    private val deleteBalanceUseCase: DeleteBalanceUseCase,
    private val getBalanceByYearUseCase: GetBalancesByYearUseCase

) : ViewModel() {

    private var _isLoadingExpense = MutableStateFlow<Boolean>(false)
    val isLoadingExpense: StateFlow<Boolean> = _isLoadingExpense

    private var _isLoadingIncome = MutableStateFlow<Boolean>(false)
    val isLoadingIncome: StateFlow<Boolean> = _isLoadingIncome

    private var _incomeBalance = MutableStateFlow<List<BalanceModel>>(emptyList())
    val incomeBalance: StateFlow<List<BalanceModel>> = _incomeBalance

    private var _totalIncomes = MutableStateFlow<Double>(0.0)
    val totalIncomes: StateFlow<Double> = _totalIncomes

    private var _expenseBalance = MutableStateFlow<List<BalanceModel>>(emptyList())
    val expenseBalance: StateFlow<List<BalanceModel>> = _expenseBalance

    private var _totalExpenses = MutableStateFlow<Double>(0.0)
    val totalExpenses: StateFlow<Double> = _totalExpenses

    private var _balancesByYear = MutableStateFlow<List<BalanceByMonthEntity>>(emptyList())
    val balancesByYear: StateFlow<List<BalanceByMonthEntity>> = _balancesByYear

    fun getBalanceByYear(year: String) {

        viewModelScope.launch {
            getBalanceByYearUseCase(year).collect{ balances ->
                _balancesByYear.value = balances
            }
        }
    }

    fun getTotalBalance() : Double {
        return totalIncomes.value - totalExpenses.value
    }

    fun getColorCategory(balance: BalanceModel) : Categories {

        return when(balance.category) {
            "Investments" -> Categories.Investment
            "Work" -> Categories.Work
            "Gift" -> Categories.Gift
            "Grocery" -> Categories.Grocery
            "Entertainment" -> Categories.Entertainment
            "Transport" -> Categories.Transport
            "Utilities" -> Categories.Utilities
            "Rent" -> Categories.Rent
            "Health" -> Categories.Health
            "Travel" -> Categories.Travel
            "Food" -> Categories.Food
            "Education" -> Categories.Education
            "Pet" -> Categories.Pet
            "Other" -> Categories.Other
            else -> Categories.Other
        }



    }

    fun getExpenseBalance(year: String, month: String) {

        viewModelScope.launch {
            _isLoadingExpense.value = true
            getBalanceByExpenseUseCase.getExpenses(year = year, month = month).collect { expenses ->
                _expenseBalance.value = expenses
                _totalExpenses.value = expenses.sumOf { it.amount }
                _isLoadingExpense.value = false
            }
        }
    }

    fun getIncomeBalance(year: String, month: String) {

        viewModelScope.launch {
            _isLoadingIncome.value = true
            getBalanceByIncomeUseCase.getIncomes(year = year, month = month).collect { incomes ->
                _incomeBalance.value = incomes
                _totalIncomes.value = incomes.sumOf { it.amount }
                _isLoadingIncome.value = false
            }
        }
    }


    fun deleteBalance(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteBalanceUseCase(id)
        }
    }

    fun addBalance(type: String,
                   amount: Double,
                   day: String,
                   month: String,
                   year: String,
                   category: String,
                   description: String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            insertBalanceUseCase(
            BalanceEntity(
                type = type,
                amount = amount,
                day = day,
                month = month,
                year = year,
                category = category,
                description = description
                )
            )
        }
    }
}