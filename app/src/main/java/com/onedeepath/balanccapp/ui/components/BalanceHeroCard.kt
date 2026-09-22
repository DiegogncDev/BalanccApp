package com.onedeepath.balanccapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.core.formatCurrency
import com.onedeepath.balanccapp.ui.theme.BalanccCornerRadius
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing
import com.onedeepath.balanccapp.ui.theme.BalanceHeroGradientEnd
import com.onedeepath.balanccapp.ui.theme.BalanceHeroGradientStart

/**
 * Gradient hero card with the current month balance and the variation
 * versus the previous month. [balanceChangePercent] is hidden when null.
 */
@Composable
fun BalanceHeroCard(
    balance: Double,
    balanceChangePercent: Int?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BalanccCornerRadius.container))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BalanceHeroGradientStart, BalanceHeroGradientEnd),
                ),
            ),
    ) {
        // Decorative translucent circle (top-end), like a subtle glow.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(BalanccSpacing.standard)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                ),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(BalanccSpacing.major),
        ) {
            Text(
                text = stringResource(R.string.current_balance),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.85f),
                ),
            )

            Spacer(Modifier.height(BalanccSpacing.micro))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$${formatCurrency(balance)}",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 34.sp,
                        color = Color.White,
                    ),
                )

                if (balanceChangePercent != null && balanceChangePercent != 0) {
                    Spacer(Modifier.width(BalanccSpacing.small))
                    Icon(
                        imageVector = if (balanceChangePercent > 0) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            if (balanceChangePercent != null) {
                Spacer(Modifier.height(BalanccSpacing.micro))

                val sign = if (balanceChangePercent > 0) "+" else ""
                Text(
                    text = "$sign$balanceChangePercent% ${stringResource(R.string.vs_previous_month)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                    ),
                )
            }
        }
    }
}
