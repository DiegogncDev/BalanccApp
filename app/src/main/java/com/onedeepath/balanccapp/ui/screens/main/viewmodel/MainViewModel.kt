package com.onedeepath.balanccapp.ui.screens.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onedeepath.balanccapp.di.IoDispatcher
import com.onedeepath.balanccapp.domain.usecases.GetBalancesByYearUseCase
import com.onedeepath.balanccapp.ui.screens.main.mapper.toMonthsBalanceUi
import com.onedeepath.balanccapp.ui.screens.main.model.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBalancesByYearUseCase: GetBalancesByYearUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private var defaultYearProvider : () -> String
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        onYearSelected(defaultYearProvider())
    }

    fun onErrorShown() {
        _uiState.update { it.copy(error = null) }
    }

    fun onYearSelected(year: String) {
        _uiState.update {
            it.copy(
                selectedYear = year,
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            getBalancesByYearUseCase(year)
                .flowOn(ioDispatcher)
                .catch{ throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message
                        )
                    }
                }
                .collectLatest { balances ->
                _uiState.update {
                    it.copy(
                        months = balances.toMonthsBalanceUi(),
                        isLoading = false
                    )
                }
            }

            }

        }
}



