package com.onedeepath.balanccapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.onedeepath.balanccapp.core.formatCurrency
import com.onedeepath.balanccapp.ui.theme.financialColors

@Composable
fun FinancialAmount(
    amount: Double,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    isIncome: Boolean? = null,
    showSign: Boolean = false,
    colorOverride: Color? = null,
) {
    val targetColor = colorOverride ?: when {
        isIncome == true -> MaterialTheme.financialColors.income
        isIncome == false -> MaterialTheme.financialColors.expense
        amount > 0 -> MaterialTheme.financialColors.income
        amount < 0 -> MaterialTheme.financialColors.expense
        else -> MaterialTheme.colorScheme.onSurface
    }

    val formattedAmount = formatCurrency(amount)
    val textToDisplay = if (showSign && amount > 0) "+$formattedAmount" else formattedAmount

    Text(
        text = textToDisplay,
        style = style,
        color = targetColor,
        modifier = modifier,
    )
}
