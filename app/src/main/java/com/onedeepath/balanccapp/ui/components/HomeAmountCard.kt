package com.onedeepath.balanccapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onedeepath.balanccapp.core.formatCurrency
import com.onedeepath.balanccapp.ui.theme.BalanccCornerRadius
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing
import com.onedeepath.balanccapp.ui.theme.financialColors

/**
 * Small summary card for incomes or expenses with a tinted container and a
 * direction icon next to the formatted amount.
 */
@Composable
fun HomeAmountCard(
    label: String,
    amount: Double,
    isIncome: Boolean,
    modifier: Modifier = Modifier,
) {
    val accent = if (isIncome) MaterialTheme.financialColors.income else MaterialTheme.financialColors.expense
    val container = if (isIncome) {
        MaterialTheme.financialColors.incomeContainer
    } else {
        MaterialTheme.financialColors.expenseContainer
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(BalanccCornerRadius.card),
        color = container,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = BalanccSpacing.standard,
                vertical = BalanccSpacing.compact,
            ),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.financialColors.textSecondary,
                ),
            )

            Spacer(Modifier.height(BalanccSpacing.micro))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$${formatCurrency(amount)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = accent,
                    ),
                )
                Spacer(Modifier.width(BalanccSpacing.micro))
                Icon(
                    imageVector = if (isIncome) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
