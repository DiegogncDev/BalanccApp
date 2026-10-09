package com.onedeepath.balanccapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.ui.theme.BrandPurple
import com.onedeepath.balanccapp.ui.theme.financialColors

/**
 * Floating bottom navigation bar with rounded corners:
 * Inicio | circular centered add button | Estadísticas.
 */
@Composable
fun HomeBottomBar(
    selectedIndex: Int,
    onHomeClick: () -> Unit,
    onAddClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 20.dp, end = 20.dp, bottom = 12.dp, top = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            shape = RoundedCornerShape(34.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.financialColors.border.copy(alpha = 0.5f)),
            shadowElevation = 8.dp,
            tonalElevation = 3.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Tab 1: Inicio
                BottomBarItem(
                    label = stringResource(R.string.home),
                    selected = selectedIndex == 0,
                    onClick = onHomeClick,
                    modifier = Modifier.weight(1f),
                ) { tint ->
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(24.dp),
                    )
                }

                // Botón central de agregar (en el medio, circular, con su propio color BrandPurple)
                Surface(
                    onClick = onAddClick,
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    color = BrandPurple,
                    shadowElevation = 4.dp,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add),
                            tint = Color.White,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }

                // Tab 2: Estadísticas
                BottomBarItem(
                    label = stringResource(R.string.statistics),
                    selected = selectedIndex == 1,
                    onClick = onStatisticsClick,
                    modifier = Modifier.weight(1f),
                ) { tint ->
                    StatisticsBarsIcon(tint = tint, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (Color) -> Unit,
) {
    val tint = if (selected) BrandPurple else MaterialTheme.financialColors.textTertiary

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icon(tint)
        Spacer(Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = tint,
            ),
        )
    }
}

/**
 * Small bar-chart icon (3 ascending bars) drawn with Canvas because
 * material-icons-core does not ship a statistics icon.
 */
@Composable
private fun StatisticsBarsIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val barWidth = size.width / 5f
        val gap = barWidth / 2f
        val heights = listOf(0.45f, 0.7f, 1f)
        heights.forEachIndexed { index, factor ->
            val barHeight = size.height * factor
            val left = index * (barWidth + gap)
            drawRoundRect(
                color = tint,
                topLeft = Offset(x = left, y = size.height - barHeight),
                size = Size(width = barWidth, height = barHeight),
                cornerRadius = CornerRadius(x = 3f, y = 3f),
            )
        }
    }
}
