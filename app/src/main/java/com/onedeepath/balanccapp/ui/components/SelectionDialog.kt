package com.onedeepath.balanccapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import com.onedeepath.balanccapp.ui.theme.BalanccCornerRadius
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing

@Composable
fun <T> SelectionDialog(
    title: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onOptionSelected: (T) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(BalanccCornerRadius.container),
            colors = CardDefaults.cardColors(),
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(
                    start = BalanccSpacing.major,
                    end = BalanccSpacing.major,
                    top = BalanccSpacing.major,
                    bottom = BalanccSpacing.compact,
                ),
                style = MaterialTheme.typography.titleLarge,
            )
            LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                items(options) { option ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(
                                horizontal = BalanccSpacing.major,
                                vertical = BalanccSpacing.standard,
                            ),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = optionLabel(option),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}
