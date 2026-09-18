package com.onedeepath.balanccapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.ui.theme.BalanccCornerRadius
import com.onedeepath.balanccapp.ui.theme.BalanccElevation
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing
import com.onedeepath.balanccapp.ui.theme.financialColors

@Composable
fun FinancialSummaryCard(
    monthName: String,
    income: Double,
    expense: Double,
    balance: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(BalanccCornerRadius.card),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.financialColors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = BalanccElevation.card),
    ) {
        Column(
            modifier = Modifier.padding(BalanccSpacing.section),
        ) {
            Text(
                text = monthName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(BalanccSpacing.standard))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.incomes),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.financialColors.textSecondary,
                    )
                    Spacer(Modifier.height(BalanccSpacing.micro))
                    FinancialAmount(
                        amount = income,
                        isIncome = true,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.expenses),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.financialColors.textSecondary,
                    )
                    Spacer(Modifier.height(BalanccSpacing.micro))
                    FinancialAmount(
                        amount = expense,
                        isIncome = false,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = BalanccSpacing.compact),
                thickness = 1.dp,
                color = MaterialTheme.financialColors.border,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.balance),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.financialColors.textSecondary,
                    fontWeight = FontWeight.Medium,
                )
                FinancialAmount(
                    amount = balance,
                    showSign = true,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}
