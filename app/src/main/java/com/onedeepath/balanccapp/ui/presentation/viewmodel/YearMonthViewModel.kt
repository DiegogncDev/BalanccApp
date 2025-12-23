package com.onedeepath.balanccapp.ui.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class YearMonthViewModel @Inject constructor() : ViewModel() {

    private val _selectedMonthByFastAdd = MutableStateFlow("January")
    val selectedMonthByFastAdd: StateFlow<String> = _selectedMonthByFastAdd

    private val _selectedYear = MutableStateFlow("2025")
    val selectedYear: StateFlow<String> = _selectedYear
    private val _selectedMonthIndex = MutableStateFlow("January")
    val selectedMonthIndex: StateFlow<String> = _selectedMonthIndex

    private val _isFastAddBalance = MutableStateFlow(false)
    val isFastAddBalance: StateFlow<Boolean> = _isFastAddBalance


    fun setIsFastAddBalance(isFastAddBalance: Boolean) {
        _isFastAddBalance.value = isFastAddBalance
    }


    fun setYear(year: String) {
        _selectedYear.value = year

    }

    fun setMonth(month: String) {
        _selectedMonthByFastAdd.value = month
    }




    fun setMonthIndex(index: Int) {

        val month = when(index) {
            0 -> "January"
            1 -> "February"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "August"
            8 -> "September"
            9 -> "October"
            10 -> "November"
            11 -> "December"
            else -> "January"
        }

        _selectedMonthIndex.value = month
    }

}