package com.onedeepath.balanccapp.ui.screens.detail

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.ui.components.BalanccFab
import com.onedeepath.balanccapp.ui.components.BalanccTopBar
import com.onedeepath.balanccapp.ui.components.EmptyState
import com.onedeepath.balanccapp.ui.components.FinancialAmount
import com.onedeepath.balanccapp.ui.components.FinancialDonutChart
import com.onedeepath.balanccapp.ui.components.FinancialSegmentedControl
import com.onedeepath.balanccapp.ui.components.TransactionCard
import com.onedeepath.balanccapp.ui.navigation.AppScreens
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.detail.model.MonthsDetailUiState
import com.onedeepath.balanccapp.ui.screens.detail.model.MyMonthsChartUiState
import com.onedeepath.balanccapp.ui.screens.detail.viewmodel.MonthsDetailViewModel
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing
import com.onedeepath.balanccapp.ui.theme.financialColors

@Composable
fun MonthsDetailScreen(
    navController: NavController,
    yearMonthViewModel: YearMonthViewModel,
    viewModel: MonthsDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val year by yearMonthViewModel.selectedYear.collectAsState()
    val month by yearMonthViewModel.selectedMonthIndex.collectAsState()

    LaunchedEffect(year, month) {
        viewModel.load(year = year, monthName = month)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            BalanccTopBar(
                title = "${uiState.month} ${uiState.year}",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            HeaderSummary(uiState = uiState)

            Spacer(Modifier.height(BalanccSpacing.small))

            DetailContent(uiState = uiState, onDelete = viewModel::deleteBalance)
        }
    }
}

@Composable
fun HeaderSummary(uiState: MonthsDetailUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BalanccSpacing.standard, vertical = BalanccSpacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.balance),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.financialColors.textSecondary,
        )

        Spacer(Modifier.height(BalanccSpacing.micro))

        FinancialAmount(
            amount = uiState.totalBalance,
            showSign = true,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(Modifier.height(BalanccSpacing.compact))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.incomes),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.financialColors.textSecondary,
                )
                Spacer(Modifier.height(BalanccSpacing.micro))
                FinancialAmount(
                    amount = uiState.totalIncome,
                    isIncome = true,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.expenses),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.financialColors.textSecondary,
                )
                Spacer(Modifier.height(BalanccSpacing.micro))
                FinancialAmount(
                    amount = uiState.totalExpense,
                    isIncome = false,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
fun DetailContent(uiState: MonthsDetailUiState, onDelete: (Int) -> Unit) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    Column(modifier = Modifier.fillMaxSize()) {
        FinancialSegmentedControl(
            isIncome = selectedTabIndex == 0,
            onCheckedChange = { isIncome -> selectedTabIndex = if (isIncome) 0 else 1 },
            modifier = Modifier.padding(horizontal = BalanccSpacing.standard, vertical = BalanccSpacing.micro),
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
        ) { page ->
            when (page) {
                0 -> TransactionTabPage(
                    transactions = uiState.incomes,
                    chartState = uiState.incomeChart,
                    isIncome = true,
                    onDelete = onDelete,
                )
                1 -> TransactionTabPage(
                    transactions = uiState.expenses,
                    chartState = uiState.expenseChart,
                    isIncome = false,
                    onDelete = onDelete,
                )
            }
        }
    }
}

@Composable
fun TransactionTabPage(
    transactions: List<BalanceModel>,
    chartState: MyMonthsChartUiState,
    isIncome: Boolean,
    onDelete: (Int) -> Unit,
) {
    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(BalanccSpacing.large),
            contentAlignment = Alignment.Center,
        ) {
            EmptyState(
                title = stringResource(if (isIncome) R.string.no_incomes else R.string.no_expenses),
            )
        }
    } else {
        val sortedTransactions = transactions.sortedByDescending {
            it.day.substringAfterLast("-").toIntOrNull() ?: 0
        }
        val groupedTransactions = sortedTransactions.groupBy { it.day }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = BalanccSpacing.standard,
                end = BalanccSpacing.standard,
                top = BalanccSpacing.small,
                bottom = 88.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(BalanccSpacing.small),
        ) {
            item {
                FinancialDonutChart(
                    entries = chartState.entries,
                    colors = chartState.colors,
                    centerText = chartState.centerText,
                    isIncome = isIncome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(vertical = BalanccSpacing.small),
                )
            }

            groupedTransactions.forEach { (rawDay, dayTransactions) ->
                item {
                    val cleanDay = rawDay.substringAfterLast("-")
                    val monthName = dayTransactions.firstOrNull()?.month.orEmpty()
                    Text(
                        text = "$cleanDay $monthName",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.financialColors.textSecondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = BalanccSpacing.small, bottom = BalanccSpacing.micro),
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
