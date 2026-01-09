package com.onedeepath.balanccapp.core

interface CurrencyHelper {
    fun formatCurrency(amount: Double): String
    fun formatAmountForDisplay(unformattedValue: String): String
    fun cleanAmountForStorage(formattedValue: String): String
}
