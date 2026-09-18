package com.onedeepath.balanccapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.ui.presentation.mapper.getDisplayNameRes
import com.onedeepath.balanccapp.ui.theme.BalanccCornerRadius
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing
import com.onedeepath.balanccapp.ui.theme.financialColors

@Composable
fun CategorySelectionDialog(
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    categories: List<Category> = Category.entries,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(BalanccCornerRadius.container),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(1.dp, MaterialTheme.financialColors.border),
        ) {
            Column(
                modifier = Modifier.padding(vertical = BalanccSpacing.standard),
            ) {
                Text(
                    text = stringResource(R.string.select_category),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(
                        horizontal = BalanccSpacing.standard,
                        vertical = BalanccSpacing.small,
                    ),
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 380.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = BalanccSpacing.small,
                        vertical = BalanccSpacing.micro,
                    ),
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        val backgroundColor = if (isSelected) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            androidx.compose.ui.graphics.Color.Transparent
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .clip(RoundedCornerShape(BalanccCornerRadius.control))
                                .background(backgroundColor)
                                .clickable {
                                    onCategorySelected(category)
                                    onDismiss()
                                }
                                .padding(
                                    horizontal = BalanccSpacing.compact,
                                    vertical = BalanccSpacing.small,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CategoryIcon(category = category)
                            Spacer(Modifier.width(BalanccSpacing.compact))
                            Text(
                                text = stringResource(category.getDisplayNameRes()),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f),
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
