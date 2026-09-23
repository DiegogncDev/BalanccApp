package com.onedeepath.balanccapp.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.core.ENGLISH_MONTHS
import com.onedeepath.balanccapp.core.formatCurrency
import com.onedeepath.balanccapp.core.shiftedMonth
import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.ui.components.BalanccFab
import com.onedeepath.balanccapp.ui.components.BalanccTopBar
import com.onedeepath.balanccapp.ui.components.EmptyState
import com.onedeepath.balanccapp.ui.components.FinancialAmount
import com.onedeepath.balanccapp.ui.components.FinancialDonutChart
import com.onedeepath.balanccapp.ui.components.MonthYearPickerRow
import com.onedeepath.balanccapp.ui.components.SelectionDialog
import com.onedeepath.balanccapp.ui.components.TransactionCard
import com.onedeepath.balanccapp.ui.navigation.AppScreens
import com.onedeepath.balanccapp.ui.presentation.mapper.getColor
import com.onedeepath.balanccapp.ui.presentation.mapper.getDisplayNameRes
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.detail.model.MonthsDetailUiState
import com.onedeepath.balanccapp.ui.screens.detail.model.PieChartData
import com.onedeepath.balanccapp.ui.screens.detail.viewmodel.MonthsDetailViewModel
import com.onedeepath.balanccapp.ui.theme.BrandGreen
import com.onedeepath.balanccapp.ui.theme.BrandRed
import com.onedeepath.balanccapp.ui.theme.financialColors

data class CategoryBreakdownItem(
    val category: Category,
    val amount: Double,
    val percentage: Int,
)

@Composable
fun MonthsDetailScreen(
    navController: NavController,
    yearMonthViewModel: YearMonthViewModel,
    viewModel: MonthsDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val year by yearMonthViewModel.selectedYear.collectAsState()
    val month by yearMonthViewModel.selectedMonthIndex.collectAsState()

    var showMonthPicker by remember { mutableStateOf(false) }

    val monthResourceIds = listOf(
        R.string.january, R.string.february, R.string.march, R.string.april,
        R.string.may, R.string.june, R.string.july, R.string.august,
        R.string.september, R.string.october, R.string.november, R.string.december,
    )

    val localizedMonthNames = monthResourceIds.map { stringResource(it) }
    val currentMonthIndex = ENGLISH_MONTHS.indexOf(month).coerceAtLeast(0)
    val localizedMonth = localizedMonthNames.getOrElse(currentMonthIndex) { month }

    LaunchedEffect(year, month) {
        viewModel.load(year = year, monthName = month)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BalanccTopBar(
                title = stringResource(R.string.statistics),
                onNavigateBack = { navController.popBackStack() },
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            BalanccFab(
                onClick = {
                    yearMonthViewModel.setIsFastAddBalance(false)
                    navController.navigate(AppScreens.AddIncomeOrExpenseScreen.route)
                },
                contentDescription = stringResource(R.string.add),
            )
        },
    ) { padding ->
        DetailBodyContent(
            uiState = uiState,
            localizedMonth = localizedMonth,
            year = year,
            onMonthPickerClick = { showMonthPicker = true },
            onPreviousMonth = {
                val (index, newYear) = shiftedMonth(currentMonthIndex, year, -1)
                yearMonthViewModel.setMonthIndex(index)
                yearMonthViewModel.setYear(newYear)
            },
            onNextMonth = {
                val (index, newYear) = shiftedMonth(currentMonthIndex, year, 1)
                yearMonthViewModel.setMonthIndex(index)
                yearMonthViewModel.setYear(newYear)
            },
            onDelete = viewModel::deleteBalance,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }

    if (showMonthPicker) {
        SelectionDialog(
            title = stringResource(R.string.select_month),
            options = localizedMonthNames,
            optionLabel = { it },
            onOptionSelected = { selected ->
                val index = localizedMonthNames.indexOf(selected)
                if (index >= 0) {
                    yearMonthViewModel.setMonthIndex(index)
                }
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false },
        )
    }
}

@Composable
fun DetailBodyContent(
    uiState: MonthsDetailUiState,
    localizedMonth: String,
    year: String,
    onMonthPickerClick: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) } // 0 = Gastos, 1 = Ingresos

    val currentTransactions = if (selectedTab == 0) uiState.expenses else uiState.incomes
    val isIncome = selectedTab == 1

    val categoryBreakdown = remember(currentTransactions) {
        val total = currentTransactions.sumOf { it.amount }
        currentTransactions.groupBy { it.category }
            .map { (cat, list) ->
                val catTotal = list.sumOf { it.amount }
                val percentage = if (total > 0) ((catTotal / total) * 100).toInt() else 0
                CategoryBreakdownItem(
                    category = cat,
                    amount = catTotal,
                    percentage = percentage,
                )
            }
            .sortedByDescending { it.amount }
    }

    val chartEntries = remember(categoryBreakdown) {
        categoryBreakdown.map { PieChartData(it.amount.toFloat(), it.category) }
    }
    val chartColors = remember(categoryBreakdown) {
        categoryBreakdown.map { it.category.getColor().toArgb() }
    }

    val totalCurrentAmount = currentTransactions.sumOf { it.amount }
    val totalAmountFormatted = "$${formatCurrency(totalCurrentAmount)}"
    val subtitleText = stringResource(if (isIncome) R.string.total_incomes else R.string.total_expenses)

    val sortedTransactions = remember(currentTransactions) {
        currentTransactions.sortedByDescending {
            it.day.substringAfterLast("-").toIntOrNull() ?: 0
        }
    }
    val groupedTransactions = remember(sortedTransactions) {
        sortedTransactions.groupBy { it.day }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // 1. Switcher (Gastos / Ingresos)
        item {
            DetailTypeSwitcher(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // 2. Month-Year Selector Row
        item {
            MonthYearPickerRow(
                monthName = localizedMonth,
                year = year,
                onClick = onMonthPickerClick,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // 3. Harmonious Total Balance Card
        item {
            MonthBalanceCard(
                totalBalance = uiState.totalBalance,
                totalIncome = uiState.totalIncome,
                totalExpense = uiState.totalExpense,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // 4. Donut Chart + Category Breakdown Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.financialColors.border.copy(alpha = 0.5f)),
                shadowElevation = 0.5.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (currentTransactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            EmptyState(
                                title = stringResource(if (isIncome) R.string.no_incomes else R.string.no_expenses),
                            )
                        }
                    } else {
                        // Donut Chart with sharp Compose Center Overlay
                        FinancialDonutChart(
                            entries = chartEntries,
                            colors = chartColors,
                            amountText = totalAmountFormatted,
                            subtitleText = subtitleText,
                            isIncome = isIncome,
                            modifier = Modifier
                                .size(220.dp)
                                .padding(vertical = 8.dp),
                        )

                        Spacer(Modifier.height(18.dp))

                        // Category Breakdown List
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            categoryBreakdown.forEach { item ->
                                CategoryBreakdownRow(item = item)
                            }
                        }
                    }
                }
            }
        }

        // 5. Movements / Transactions Section
        if (currentTransactions.isNotEmpty()) {
            groupedTransactions.forEach { (rawDay, dayTransactions) ->
                item {
                    val cleanDay = rawDay.substringAfterLast("-")
                    val dayMonthName = dayTransactions.firstOrNull()?.month.orEmpty()
                    Text(
                        text = "$cleanDay $dayMonthName",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.financialColors.textSecondary,
                        ),
                        modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                    )
                }

                items(
                    items = dayTransactions,
                    key = { it.id },
                ) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        isIncome = isIncome,
                        onDelete = { onDelete(transaction.id) },
                    )
                }
            }
        }
    }
}

/**
 * Segmented switcher matching mockup: Gastos / Ingresos
 */
@Composable
fun DetailTypeSwitcher(
    selectedTab: Int, // 0 for Gastos, 1 for Ingresos
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(50.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Option 1: Gastos
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(if (selectedTab == 0) BrandRed else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onTabSelected(0) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.expenses_tab),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp,
                    ),
                    color = if (selectedTab == 0) Color.White else MaterialTheme.financialColors.textSecondary,
                )
            }

            // Option 2: Ingresos
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(if (selectedTab == 1) BrandGreen else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onTabSelected(1) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.incomes_tab),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 15.sp,
                    ),
                    color = if (selectedTab == 1) Color.White else MaterialTheme.financialColors.textSecondary,
                )
            }
        }
    }
}

/**
 * Harmonious Total Balance summary card
 */
@Composable
fun MonthBalanceCard(
    totalBalance: Double,
    totalIncome: Double,
    totalExpense: Double,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.financialColors.border.copy(alpha = 0.5f)),
        shadowElevation = 0.5.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.total_balance),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.financialColors.textSecondary,
                    ),
                )

                FinancialAmount(
                    amount = totalBalance,
                    showSign = true,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    ),
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.financialColors.income),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${stringResource(R.string.incomes)}: +$${formatCurrency(totalIncome)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.financialColors.textSecondary,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.financialColors.expense),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${stringResource(R.string.expenses)}: -$${formatCurrency(totalExpense)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.financialColors.textSecondary,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
        }
    }
}

/**
 * Category breakdown row with dot, name, percentage %, and formatted amount $
 */
@Composable
fun CategoryBreakdownRow(
    item: CategoryBreakdownItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Color dot
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(item.category.getColor()),
        )

        Spacer(Modifier.width(12.dp))

        // Category Name
        Text(
            text = stringResource(item.category.getDisplayNameRes()),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.weight(1f),
        )

        // Percentage %
        Text(
            text = "${item.percentage}%",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.financialColors.textSecondary,
            ),
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        // Amount $
        Text(
            text = "$${formatCurrency(item.amount)}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.financialColors.textSecondary,
            ),
        )
    }
}

