package com.onedeepath.balanccapp.ui.screens.addbalance

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.onedeepath.balanccapp.core.ENGLISH_MONTHS
import com.onedeepath.balanccapp.core.cleanAmountForStorage
import com.onedeepath.balanccapp.core.formatAmountForDisplay
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.ui.components.BalanccTopBar
import com.onedeepath.balanccapp.ui.components.CategorySelectionDialog
import com.onedeepath.balanccapp.ui.components.SelectionDialog
import com.onedeepath.balanccapp.ui.presentation.mapper.getDisplayNameRes
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.addbalance.viewmodel.AddBalanceViewModel
import com.onedeepath.balanccapp.ui.theme.BrandGreen
import com.onedeepath.balanccapp.ui.theme.BrandPurple
import com.onedeepath.balanccapp.ui.theme.BrandRed
import com.onedeepath.balanccapp.ui.theme.financialColors
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle as DateTextStyle
import java.util.Locale

private val IconContainerColor = Color(0xFFEDE9FE)
private val AmountTextColor = Color(0xFF6D7993)

@Composable
fun AddIncomeOrExpenseScreen(
    yearMonthViewModel: YearMonthViewModel,
    navController: NavController? = null,
    viewModel: AddBalanceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val balanceAddedMessage = stringResource(R.string.balance_added)

    LaunchedEffect(uiState.saveSuccess, uiState.error) {
        if (uiState.saveSuccess) {
            Toast.makeText(context, balanceAddedMessage, Toast.LENGTH_SHORT).show()
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
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // Segmented Switcher (Ingreso / Gasto)
            AddTypeSegmentedControl(
                isIncome = uiState.isIncome,
                onCheckedChange = viewModel::onTypeChange,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(18.dp))

            // Card 1: Monto
            AddAmountCard(
                amount = uiState.amount,
                onAmountChange = viewModel::onAmountChange,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(14.dp))

            // Card 2: Categoría
            AddCategoryCard(
                category = uiState.category,
                onClick = { showCategoryDialog = true },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(14.dp))

            // Card 3: Fecha
            AddDateCard(
                selectedYear = selectedYear.toIntOrNull() ?: LocalDate.now().year,
                selectedMonth = currentSelectedMonth,
                selectedDay = uiState.selectedDay,
                onDaySelected = { date -> viewModel.onDaySelected(date.dayOfMonth.toString()) },
                onMonthSelected = { selectedMonth ->
                    if (isFastAddBalance) {
                        yearMonthViewModel.setMonth(selectedMonth)
                    } else {
                        yearMonthViewModel.setMonthIndex(ENGLISH_MONTHS.indexOf(selectedMonth))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(14.dp))

            // Card 4: Descripción (opcional)
            AddDescriptionCard(
                value = uiState.details,
                onValueChange = viewModel::onDetailsChange,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(28.dp))

            // Bottom Save Button
            Button(
                onClick = {
                    viewModel.save(
                        year = selectedYear,
                        month = currentSelectedMonth,
                    )
                },
                enabled = uiState.isValid && !uiState.isSaving,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPurple,
                    contentColor = Color.White,
                    disabledContainerColor = BrandPurple.copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp),
                    )
                } else {
                    Text(
                        text = stringResource(R.string.save),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
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

/**
 * Segmented pill control matching the mockup:
 * "Ingreso" with green rounded pill background, "Gasto" with red/coral rounded pill background.
 */
@Composable
fun AddTypeSegmentedControl(
    isIncome: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerBg = MaterialTheme.colorScheme.surfaceVariant

    Surface(
        modifier = modifier.height(52.dp),
        shape = CircleShape,
        color = containerBg,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Option 1: Ingreso
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(if (isIncome) BrandGreen else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onCheckedChange(true) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.income_singular),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp,
                    ),
                    color = if (isIncome) Color.White else MaterialTheme.financialColors.textSecondary,
                )
            }

            // Option 2: Gasto
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(if (!isIncome) BrandRed else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onCheckedChange(false) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.expense_singular),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (!isIncome) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp,
                    ),
                    color = if (!isIncome) Color.White else MaterialTheme.financialColors.textSecondary,
                )
            }
        }
    }
}

/**
 * Card 1: Monto ($ 0.00)
 */
@Composable
fun AddAmountCard(
    amount: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cleanedAmount = cleanAmountForStorage(amount)
    val formattedAmount = formatAmountForDisplay(cleanedAmount)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.financialColors.border.copy(alpha = 0.5f)),
        shadowElevation = 0.5.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Text(
                text = stringResource(R.string.amount),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = MaterialTheme.financialColors.textSecondary,
            )

            Spacer(Modifier.height(8.dp))

            BasicTextField(
                value = formattedAmount,
                onValueChange = { newValue ->
                    val newCleanedValue = newValue.replace(Regex("[^0-9]"), "")
                    if (newCleanedValue.length <= 12) onAmountChange(newCleanedValue)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmountTextColor,
                ),
                cursorBrush = SolidColor(BrandPurple),
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "$ ",
                            style = TextStyle(
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmountTextColor,
                            ),
                        )
                        if (amount.isEmpty()) {
                            Text(
                                text = "0.00",
                                style = TextStyle(
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmountTextColor,
                                ),
                            )
                        } else {
                            innerTextField()
                        }
                    }
                },
            )
        }
    }
}

/**
 * Card 2: Categoría
 */
@Composable
fun AddCategoryCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.financialColors.border.copy(alpha = 0.5f)),
        shadowElevation = 0.5.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon container badge
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = IconContainerColor,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(category.icon),
                        contentDescription = null,
                        tint = BrandPurple,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.category),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = MaterialTheme.financialColors.textSecondary,
                    ),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(category.getDisplayNameRes()),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.financialColors.textTertiary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Card 3: Fecha
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDateCard(
    selectedYear: Int,
    selectedMonth: String,
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

    val localizedMonthNames = monthResourceIds.map { stringResource(it) }
    val selectedMonthIndex = ENGLISH_MONTHS.indexOf(selectedMonth).coerceAtLeast(0)
    val localizedSelectedMonth = localizedMonthNames.getOrElse(selectedMonthIndex) { selectedMonth }

    val startDate = LocalDate.of(selectedYear, selectedMonthIndex + 1, 1)
    val endDate = YearMonth.of(selectedYear, selectedMonthIndex + 1).atEndOfMonth()

    val calendarTheme = MaterialTheme.colorScheme.copy(
        surface = MaterialTheme.colorScheme.surface,
        onSurface = MaterialTheme.colorScheme.onSurface,
        primary = BrandPurple,
        onPrimary = Color.White,
        secondaryContainer = IconContainerColor,
        onSecondaryContainer = BrandPurple,
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

    // Short month name (e.g., "ene", "feb", "mar", ...)
    val shortMonthName = try {
        val monthEnum = Month.of(selectedMonthIndex + 1)
        monthEnum.getDisplayName(DateTextStyle.SHORT, Locale.getDefault()).replace(".", "")
    } catch (e: Exception) {
        selectedMonth.take(3)
    }

    val formattedDateText = if (selectedDay.isNotBlank()) {
        "$selectedDay $shortMonthName $selectedYear"
    } else {
        "$localizedSelectedMonth $selectedYear"
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { calendarState.show() },
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.financialColors.border.copy(alpha = 0.5f)),
        shadowElevation = 0.5.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon container badge
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = IconContainerColor,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = BrandPurple,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.date),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = MaterialTheme.financialColors.textSecondary,
                    ),
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formattedDateText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }

            Surface(
                onClick = { showMonthDialog = true },
                shape = RoundedCornerShape(12.dp),
                color = IconContainerColor,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = localizedSelectedMonth,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = BrandPurple,
                        ),
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = BrandPurple,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }

    if (showMonthDialog) {
        SelectionDialog(
            title = stringResource(R.string.select_month),
            options = localizedMonthNames,
            optionLabel = { it },
            onOptionSelected = { month ->
                val index = localizedMonthNames.indexOf(month)
                if (index >= 0) {
                    onMonthSelected(ENGLISH_MONTHS[index])
                }
                showMonthDialog = false
            },
            onDismiss = { showMonthDialog = false },
        )
    }
}

/**
 * Card 4: Descripción (opcional)
 */
@Composable
fun AddDescriptionCard(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.financialColors.border.copy(alpha = 0.5f)),
        shadowElevation = 0.5.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon container badge
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = IconContainerColor,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = BrandPurple,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.description_optional),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = MaterialTheme.financialColors.textSecondary,
                    ),
                )
                Spacer(Modifier.height(2.dp))

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    cursorBrush = SolidColor(BrandPurple),
                    maxLines = 3,
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(R.string.add_note_placeholder),
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.financialColors.textTertiary,
                                ),
                            )
                        }
                        innerTextField()
                    },
                )
            }
        }
    }
}
