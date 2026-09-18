package com.onedeepath.balanccapp.ui.screens.addbalance

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.core.cleanAmountForStorage
import com.onedeepath.balanccapp.core.formatAmountForDisplay
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.ui.components.BalanccTopBar
import com.onedeepath.balanccapp.ui.components.CategoryIcon
import com.onedeepath.balanccapp.ui.components.CategorySelectionDialog
import com.onedeepath.balanccapp.ui.components.FinancialSegmentedControl
import com.onedeepath.balanccapp.ui.components.PrimaryButton
import com.onedeepath.balanccapp.ui.components.SelectionDialog
import com.onedeepath.balanccapp.ui.components.SelectionField
import com.onedeepath.balanccapp.ui.presentation.mapper.getDisplayNameRes
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.addbalance.viewmodel.AddBalanceViewModel
import com.onedeepath.balanccapp.ui.theme.BalanccCornerRadius
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing
import com.onedeepath.balanccapp.ui.theme.financialColors
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun AddIncomeOrExpenseScreen(
    yearMonthViewModel: YearMonthViewModel,
    navController: NavController? = null,
    viewModel: AddBalanceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.saveSuccess, uiState.error) {
        if (uiState.saveSuccess) {
            Toast.makeText(context, context.getString(R.string.balance_added), Toast.LENGTH_SHORT).show()
            viewModel.resetSaveEvent()
            navController?.popBackStack()
        }
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.resetSaveEvent()
        }
    }

    val selectedYear: String by yearMonthViewModel.selectedYear.collectAsState()
    val selectedMonthByIndex: String by yearMonthViewModel.selectedMonthIndex.collectAsState()
    val isFastAddBalance: Boolean by yearMonthViewModel.isFastAddBalance.collectAsState()
    val selectedMonthByFastAdd: String by yearMonthViewModel.selectedMonthByFastAdd.collectAsState()

    val currentSelectedMonth = if (isFastAddBalance) selectedMonthByFastAdd else selectedMonthByIndex

    var showCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            BalanccTopBar(
                title = stringResource(R.string.add),
                onNavigateBack = navController?.let { { it.popBackStack() } },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = BalanccSpacing.standard),
        ) {
            Spacer(Modifier.height(BalanccSpacing.small))

            FinancialSegmentedControl(
                isIncome = uiState.isIncome,
                onCheckedChange = viewModel::onTypeChange,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(BalanccSpacing.section))

            AddAmountField(
                amount = uiState.amount,
                onAmountChange = viewModel::onAmountChange,
            )

            Spacer(Modifier.height(BalanccSpacing.section))

            SelectionField(
                label = stringResource(uiState.category.getDisplayNameRes()),
                supportingText = stringResource(R.string.category),
                leadingContent = { CategoryIcon(category = uiState.category) },
                onClick = { showCategoryDialog = true },
            )

            Spacer(Modifier.height(BalanccSpacing.compact))

            AddDatePickerField(
                selectedYear = selectedYear.toIntOrNull() ?: 2026,
                selectedMonth = currentSelectedMonth,
                isFastAddBalance = isFastAddBalance,
                selectedDay = uiState.selectedDay,
                onDaySelected = { date -> viewModel.onDaySelected(date.dayOfMonth.toString()) },
                onMonthSelected = yearMonthViewModel::setMonth,
            )

            Spacer(Modifier.height(BalanccSpacing.compact))

            OutlinedTextField(
                value = uiState.details,
                onValueChange = viewModel::onDetailsChange,
                label = { Text(stringResource(R.string.details)) },
                placeholder = { Text(stringResource(R.string.enter_details)) },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                shape = RoundedCornerShape(BalanccCornerRadius.input),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                maxLines = 4,
                singleLine = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.financialColors.border,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.financialColors.textSecondary,
                    focusedPlaceholderColor = MaterialTheme.financialColors.textTertiary,
                    unfocusedPlaceholderColor = MaterialTheme.financialColors.textTertiary,
                ),
            )

            Spacer(Modifier.height(BalanccSpacing.major))

            PrimaryButton(
                text = stringResource(R.string.add),
                enabled = uiState.isValid,
                loading = uiState.isSaving,
                onClick = {
                    viewModel.save(
                        year = selectedYear,
                        month = currentSelectedMonth,
                    )
                },
            )

            Spacer(Modifier.height(BalanccSpacing.large))
        }
    }

    if (showCategoryDialog) {
        CategorySelectionDialog(
            selectedCategory = uiState.category,
            onCategorySelected = viewModel::onCategoryChange,
            onDismiss = { showCategoryDialog = false },
        )
    }
}

@Composable
fun AddAmountField(
    amount: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cleanedAmount = cleanAmountForStorage(amount)
    val formattedAmount = formatAmountForDisplay(cleanedAmount)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.amount),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.financialColors.textSecondary,
        )

        Spacer(Modifier.height(BalanccSpacing.micro))

        BasicTextField(
            value = formattedAmount,
            onValueChange = { newValue ->
                val newCleanedValue = newValue.replace(Regex("[^0-9]"), "")
                if (newCleanedValue.length <= 12) onAmountChange(newCleanedValue)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (amount.isEmpty()) {
                        Text(
                            text = "$ 0.00",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.financialColors.textDisabled,
                                textAlign = TextAlign.Center,
                            ),
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDatePickerField(
    selectedYear: Int,
    selectedMonth: String,
    isFastAddBalance: Boolean,
    selectedDay: String,
    onDaySelected: (LocalDate) -> Unit,
    onMonthSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMonthDialog by remember { mutableStateOf(false) }
    val calendarState = remember { UseCaseState() }

    val monthResourceIds = listOf(
        R.string.january, R.string.february, R.string.march, R.string.april,
        R.string.may, R.string.june, R.string.july, R.string.august,
        R.string.september, R.string.october, R.string.november, R.string.december,
    )

    val monthNames = monthResourceIds.map { stringResource(it) }
    val selectedMonthIndex = monthNames.indexOf(selectedMonth).coerceAtLeast(0)

    val startDate = LocalDate.of(selectedYear, selectedMonthIndex + 1, 1)
    val endDate = YearMonth.of(selectedYear, selectedMonthIndex + 1).atEndOfMonth()

    val calendarTheme = MaterialTheme.colorScheme.copy(
        surface = MaterialTheme.colorScheme.surface,
        onSurface = MaterialTheme.colorScheme.onSurface,
        primary = MaterialTheme.colorScheme.primary,
        onPrimary = MaterialTheme.colorScheme.onPrimary,
        secondaryContainer = MaterialTheme.colorScheme.primaryContainer,
        onSecondaryContainer = MaterialTheme.colorScheme.onPrimaryContainer,
    )

    MaterialTheme(colorScheme = calendarTheme) {
        CalendarDialog(
            state = calendarState,
            config = CalendarConfig(
                monthSelection = false,
                yearSelection = false,
                style = CalendarStyle.MONTH,
                boundary = startDate..endDate,
            ),
            selection = CalendarSelection.Date { date ->
                onDaySelected(date)
                calendarState.finish()
            },
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { calendarState.show() },
        shape = RoundedCornerShape(BalanccCornerRadius.input),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.financialColors.border),
    ) {
        Row(
            modifier = Modifier.padding(BalanccSpacing.standard),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(BalanccCornerRadius.control),
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(Modifier.width(BalanccSpacing.compact))

            Column(modifier = Modifier.weight(1f)) {
                val dateLabel = if (selectedDay.isNotBlank()) {
                    "$selectedDay $selectedMonth $selectedYear"
                } else {
                    "$selectedMonth $selectedYear"
                }

                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = if (selectedDay.isNotBlank()) {
                        stringResource(R.string.date)
                    } else {
                        stringResource(R.string.select_date)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.financialColors.textSecondary,
                )
            }

            if (isFastAddBalance) {
                Surface(
                    onClick = { showMonthDialog = true },
                    shape = RoundedCornerShape(BalanccCornerRadius.control),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = BalanccSpacing.small,
                            vertical = BalanccSpacing.micro,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = selectedMonth,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.financialColors.textSecondary,
                )
            }
        }
    }

    if (showMonthDialog) {
        SelectionDialog(
            title = stringResource(R.string.select_month),
            options = monthNames,
            optionLabel = { it },
            onOptionSelected = { month ->
                onMonthSelected(month)
                showMonthDialog = false
            },
            onDismiss = { showMonthDialog = false },
        )
    }
}
