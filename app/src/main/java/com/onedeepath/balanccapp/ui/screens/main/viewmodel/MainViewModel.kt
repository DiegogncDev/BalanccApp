package com.onedeepath.balanccapp.ui.screens.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onedeepath.balanccapp.domain.model.BalanceByMonth
import com.onedeepath.balanccapp.domain.usecases.GetBalancesByYearUseCase
import com.onedeepath.balanccapp.ui.screens.main.model.MainUiState
import com.onedeepath.balanccapp.ui.screens.main.model.MonthsBalanceUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBalancesByYearUseCase: GetBalancesByYearUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        val currentYear = "2025"
        onYearSelected(currentYear)
    }

    fun onYearSelected(year: String) {
        _uiState.update {
            it.copy(
                selectedYear = year,
                isLoading = true
            )
        }

        viewModelScope.launch {
            delay(2000)
            getBalancesByYearUseCase(year).collect { balances ->
                _uiState.update {
                    it.copy(
                        months = balances.toMonthsBalanceUi(),
                        isLoading = false
                    )
                }
            }

            }

        }

    fun List<BalanceByMonth>.toMonthsBalanceUi(): List<MonthsBalanceUi> {
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        return months.mapIndexed { index, month ->
            val income = filter { it.month == month && it.type == "income" }
                .sumOf { it.total }

            val expense = filter { it.month == month && it.type == "expense" }
                .sumOf { it.total }

            MonthsBalanceUi(
                monthIndex = index,
                monthName = month,
                income = income,
                expense = expense,
                balance = income - expense
            )

        }
    }

}



