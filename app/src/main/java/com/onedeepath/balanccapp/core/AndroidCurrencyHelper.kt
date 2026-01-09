package com.onedeepath.balanccapp.core

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

class AndroidCurrencyHelper @Inject constructor() : CurrencyHelper {

    // For formatAmountForDisplay (User Input)
    private val displaySymbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    private val displayFormat = DecimalFormat("#,##0", displaySymbols)

    // For formatCurrency (Show totals)
    private val currencySymbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }
    private val currencyFormat = DecimalFormat("#,###.##", currencySymbols)

    override fun formatAmountForDisplay(unformattedValue: String): String {
        val cleanedValue = unformattedValue.replace(Regex("[.,]"), "")
        if (cleanedValue.isEmpty()) return ""

        return try {
            val number = cleanedValue.toLong()
            displayFormat.format(number)
        } catch (e: NumberFormatException) {
            unformattedValue
        }
    }

    override fun cleanAmountForStorage(formattedValue: String): String {
        return formattedValue.replace(".", "")
    }

    override fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }

}