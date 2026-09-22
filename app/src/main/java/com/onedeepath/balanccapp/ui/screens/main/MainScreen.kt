package com.onedeepath.balanccapp.ui.screens.main

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.ui.components.BalanceHeroCard
import com.onedeepath.balanccapp.ui.components.EmptyState
import com.onedeepath.balanccapp.ui.components.FinancialDonutChart
import com.onedeepath.balanccapp.ui.components.HomeAmountCard
import com.onedeepath.balanccapp.ui.components.HomeBottomBar
import com.onedeepath.balanccapp.ui.components.MonthYearPickerRow
import com.onedeepath.balanccapp.ui.components.SelectionDialog
import com.onedeepath.balanccapp.ui.navigation.AppScreens
import com.onedeepath.balanccapp.ui.presentation.mapper.getDisplayNameRes
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.detail.model.PieChartData
import com.onedeepath.balanccapp.ui.screens.main.model.HomeCategoryItem
import com.onedeepath.balanccapp.ui.screens.main.viewmodel.MainViewModel
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing
import com.onedeepath.balanccapp.ui.theme.BrandPurple
import com.onedeepath.balanccapp.ui.theme.financialColors

private const val MAX_VISIBLE_CATEGORIES = 4

@Composable
fun MainScreen(
    navController: NavController,
    yearMonthViewModel: YearMonthViewModel,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val year by yearMonthViewModel.selectedYear.collectAsState()
    val month by yearMonthViewModel.selectedMonthIndex.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    var showMonthPicker by remember { mutableStateOf(false) }

    val monthResourceIds = listOf(
        R.string.january, R.string.february, R.string.march, R.string.april,
        R.string.may, R.string.june, R.string.july, R.string.august,
        R.string.september, R.string.october, R.string.november, R.string.december,
    )
    val localizedMonths = monthResourceIds.map { stringResource(it) }
    val currentMonthIndex = ENGLISH_MONTHS.indexOf(month).coerceAtLeast(0)
    val localizedMonth = localizedMonths.getOrElse(currentMonthIndex) { month }

    LaunchedEffect(year, month) {
        viewModel.load(year = year, month = month)
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackBarHostState.showSnackbar(message = message)
            viewModel.onErrorShown()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackBarHostState) },
        bottomBar = {
            HomeBottomBar(
                selectedIndex = 0,
                onHomeClick = { viewModel.load(year = year, month = month) },
                onAddClick = {
                    yearMonthViewModel.setIsFastAddBalance(false)
                    navController.navigate(AppScreens.AddIncomeOrExpenseScreen.route)
                },
                onStatisticsClick = {
                    navController.navigate(AppScreens.DetailScreen.route)
                },
            )
        },
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            HomeDashboardContent(
                totalBalance = uiState.totalBalance,
                balanceChangePercent = uiState.balanceChangePercent,
                totalIncome = uiState.totalIncome,
                totalExpense = uiState.totalExpense,
                expenseBreakdown = uiState.expenseBreakdown,
                localizedMonth = localizedMonth,
                year = year,
                onMonthPickerClick = { showMonthPicker = true },
                onSettingsClick = { navController.navigate(AppScreens.SettingsScreen.route) },
                onViewDetailClick = { navController.navigate(AppScreens.DetailScreen.route) },
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        }
    }

    if (showMonthPicker) {
        SelectionDialog(
            title = stringResource(R.string.select_month),
            options = localizedMonths,
            optionLabel = { it },
            onOptionSelected = { selected ->
                val index = localizedMonths.indexOf(selected)
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
private fun HomeDashboardContent(
    totalBalance: Double,
    balanceChangePercent: Int?,
    totalIncome: Double,
    totalExpense: Double,
    expenseBreakdown: List<HomeCategoryItem>,
    localizedMonth: String,
    year: String,
    onMonthPickerClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onViewDetailClick: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = BalanccSpacing.standard,
            end = BalanccSpacing.standard,
            top = BalanccSpacing.small,
            bottom = BalanccSpacing.large,
        ),
        verticalArrangement = Arrangement.spacedBy(BalanccSpacing.standard),
    ) {
        item {
            HomeHeader(onSettingsClick = onSettingsClick)
        }

        item {
            MonthYearPickerRow(
                monthName = localizedMonth,
                year = year,
                onClick = onMonthPickerClick,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
            )
        }

        item {
            BalanceHeroCard(
                balance = totalBalance,
                balanceChangePercent = balanceChangePercent,
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BalanccSpacing.compact),
            ) {
                HomeAmountCard(
                    label = stringResource(R.string.incomes),
                    amount = totalIncome,
                    isIncome = true,
                    modifier = Modifier.weight(1f),
                )
                HomeAmountCard(
                    label = stringResource(R.string.expenses),
                    amount = totalExpense,
                    isIncome = false,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.month_summary),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                ),
            )
        }

        item {
            if (expenseBreakdown.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyState(title = stringResource(R.string.no_expenses))
                }
            } else {
                MonthSummaryRow(
                    totalExpense = totalExpense,
                    expenseBreakdown = expenseBreakdown,
                )
            }
        }

        if (expenseBreakdown.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    TextButton(onClick = onViewDetailClick) {
                        Text(
                            text = stringResource(R.string.view_detail),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = BrandPurple,
                            ),
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = BrandPurple,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                ),
            )
        }

        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun MonthSummaryRow(
    totalExpense: Double,
    expenseBreakdown: List<HomeCategoryItem>,
    modifier: Modifier = Modifier,
) {
    val displayBreakdown = remember(expenseBreakdown) {
        groupBreakdownForHome(expenseBreakdown)
    }
    val othersLabel = stringResource(R.string.others)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FinancialDonutChart(
            entries = displayBreakdown.map { PieChartData(it.amount.toFloat(), it.category) },
            colors = displayBreakdown.map { it.category.color.toArgb() },
            amountText = "$${formatCurrency(totalExpense)}",
            subtitleText = stringResource(R.string.total_expenses),
            isIncome = false,
            holeColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.size(190.dp),
        )

        Spacer(Modifier.width(BalanccSpacing.standard))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(BalanccSpacing.small),
        ) {
            displayBreakdown.forEachIndexed { index, item ->
                val label = if (expenseBreakdown.size > MAX_VISIBLE_CATEGORIES &&
                    index == displayBreakdown.lastIndex
                ) {
                    othersLabel
                } else {
                    stringResource(item.category.getDisplayNameRes())
                }
                HomeCategoryLegendRow(
                    label = label,
                    color = item.category.color,
                    percentage = item.percentage,
                )
            }
        }
    }
}

/**
 * Keeps the top [MAX_VISIBLE_CATEGORIES] categories and merges the rest into
 * a single "Others" entry so the summary chart always fits on screen.
 */
private fun groupBreakdownForHome(breakdown: List<HomeCategoryItem>): List<HomeCategoryItem> {
    if (breakdown.size <= MAX_VISIBLE_CATEGORIES) return breakdown

    val topCategories = breakdown.take(MAX_VISIBLE_CATEGORIES)
    val remaining = breakdown.drop(MAX_VISIBLE_CATEGORIES)

    return topCategories + HomeCategoryItem(
        category = Category.OTHER,
        amount = remaining.sumOf { it.amount },
        percentage = remaining.sumOf { it.percentage },
    )
}

@Composable
private fun HomeCategoryLegendRow(
    label: String,
    color: Color,
    percentage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )

        Spacer(Modifier.width(BalanccSpacing.small))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.weight(1f),
        )

        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = MaterialTheme.financialColors.textSecondary,
            ),
        )
    }
}
