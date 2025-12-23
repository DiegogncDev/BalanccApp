package com.onedeepath.balanccapp.ui.screens.addbalance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.domain.usecases.InsertBalanceUseCase
import com.onedeepath.balanccapp.ui.screens.addbalance.model.AddBalanceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBalanceViewModel @Inject constructor(
    private val insertBalanceUseCase: InsertBalanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBalanceUiState())
    val uiState: StateFlow<AddBalanceUiState> = _uiState

    fun onAmountChange(value: String) {
        _uiState.update {
            it.copy(
                amount = value,
                isValid = value.isNotBlank()
            )
        }
    }

    fun onCategoryChange(category: Category) {
        _uiState.update { it.copy(category = category) }
    }

    fun onTypeChange(isIncome: Boolean) {
        _uiState.update { it.copy(isIncome = isIncome) }
    }

    fun onDaySelected(day: String) {
        _uiState.update { it.copy(selectedDay = day) }
    }


    fun onDetailsChange(details: String) {
        _uiState.update { it.copy(details = details) }
    }

    fun save(
        year: String,
        month: String,
    ): Result<Unit> {
        val state = _uiState.value

        if (!state.isValid || state.selectedDay == null) {
            return Result.failure(IllegalArgumentException(""))
        }

        viewModelScope.launch {
            insertBalanceUseCase(
                BalanceModel(
                    type = if (state.isIncome) "income" else "expense",
                    amount = state.amount.toDouble(),
                    day = state.selectedDay,
                    month = month,
                    year = year,
                    category = state.category,
                    description = state.details
                )
            )
        }
        return Result.success(Unit)
    }

}
