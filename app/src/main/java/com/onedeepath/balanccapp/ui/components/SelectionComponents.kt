package com.onedeepath.balanccapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.ui.presentation.mapper.getColor
import com.onedeepath.balanccapp.ui.presentation.mapper.getIconRes
import com.onedeepath.balanccapp.ui.theme.BalanccCornerRadius
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing
import com.onedeepath.balanccapp.ui.theme.financialColors

@Composable
fun SelectionField(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(BalanccCornerRadius.input),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.financialColors.border),
    ) {
        Row(
            modifier = Modifier.padding(BalanccSpacing.standard),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingContent?.invoke()
            if (leadingContent != null) Spacer(Modifier.width(BalanccSpacing.compact))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, style = MaterialTheme.typography.titleSmall)
                supportingText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.financialColors.textSecondary,
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.financialColors.textSecondary,
            )
        }
    }
}

@Composable
fun CategoryIcon(
    category: Category,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Surface(
        modifier = modifier.size(40.dp),
        shape = RoundedCornerShape(BalanccCornerRadius.control),
        color = category.getColor().copy(alpha = 0.16f),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(category.getIconRes()),
                contentDescription = contentDescription,
                tint = category.getColor(),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        modifier = modifier,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.financialColors.textSecondary,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
fun EmptyState(
    title: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    icon: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        icon?.invoke()
        if (icon != null) Spacer(Modifier.size(BalanccSpacing.standard))
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        supportingText?.let {
            Spacer(Modifier.size(BalanccSpacing.small))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.financialColors.textSecondary,
            )
        }
    }
}
