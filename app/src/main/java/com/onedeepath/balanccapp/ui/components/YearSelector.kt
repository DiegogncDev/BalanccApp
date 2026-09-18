package com.onedeepath.balanccapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.ui.theme.BalanccCornerRadius
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing
import com.onedeepath.balanccapp.ui.theme.financialColors

@Composable
fun YearSelector(
    selectedYear: String,
    onYearSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    startYear: Int = 2020,
    endYear: Int = 2060,
) {
    var showDialog by remember { mutableStateOf(false) }
    val years = remember(startYear, endYear) { (startYear..endYear).toList() }

    Surface(
        modifier = modifier.clickable { showDialog = true },
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(BalanccCornerRadius.control),
        border = BorderStroke(1.dp, MaterialTheme.financialColors.border),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = BalanccSpacing.compact,
                vertical = BalanccSpacing.small,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(BalanccSpacing.small))
            Text(
                text = selectedYear,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.width(BalanccSpacing.micro))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.financialColors.textSecondary,
            )
        }
    }

    if (showDialog) {
        SelectionDialog(
            title = stringResource(R.string.select_year),
            options = years,
            optionLabel = { it.toString() },
            onOptionSelected = { year ->
                onYearSelected(year.toString())
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}
