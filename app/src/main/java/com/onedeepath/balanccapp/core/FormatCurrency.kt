package com.onedeepath.balanccapp.core

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val customFormatSymbols = DecimalFormatSymbols(Locale.getDefault()).apply {
    groupingSeparator = '.'
    decimalSeparator = ','
}

private val decimalFormat = DecimalFormat("#,##0", customFormatSymbols)

fun formatAmountForDisplay(unformattedValue: String): String {
    val cleanedValue = unformattedValue.replace(Regex("[.,]"), "")
    if (cleanedValue.isEmpty()) return ""

    return try {
        val number = cleanedValue.toLong()
        decimalFormat.format(number)
    } catch (e: NumberFormatException) {
        unformattedValue
    }
}

fun cleanAmountForStorage(formattedValue: String): String {
    return formattedValue.replace(".", "")
}



fun formatCurrency(amount: Double): String {

    val symbols = DecimalFormatSymbols(Locale.US)
    symbols.groupingSeparator = '.'
    symbols.decimalSeparator = ','

    val decimalFormat = DecimalFormat("#,###.##", symbols)
    return decimalFormat.format(amount)
}

