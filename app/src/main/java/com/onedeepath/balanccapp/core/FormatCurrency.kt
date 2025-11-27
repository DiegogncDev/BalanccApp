package com.onedeepath.balanccapp.core

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun formatCurrency(amount: Double): String {

    val symbols = DecimalFormatSymbols(Locale.US)
    symbols.groupingSeparator = '.'
    symbols.decimalSeparator = ','

    val decimalFormat = DecimalFormat("#,###.##", symbols)
    return decimalFormat.format(amount)

}