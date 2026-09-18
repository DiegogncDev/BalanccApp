package com.onedeepath.balanccapp.ui.screens.main

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.ui.components.BalanccFab
import com.onedeepath.balanccapp.ui.components.EmptyState
import com.onedeepath.balanccapp.ui.components.FinancialSummaryCard
import com.onedeepath.balanccapp.ui.components.YearSelector
import com.onedeepath.balanccapp.ui.navigation.AppScreens
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.main.model.MonthsBalanceUi
import com.onedeepath.balanccapp.ui.screens.main.viewmodel.MainViewModel
import com.onedeepath.balanccapp.ui.theme.BalanccSpacing

@Composable
fun MainScreen(
    navController: NavController,
    yearMonthViewModel: YearMonthViewModel,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { message ->
            snackBarHostState.showSnackbar(message = message)
            viewModel.onErrorShown()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            AddBalanceFAB(
                navController = navController,
                onFastAddBalance = yearMonthViewModel::setIsFastAddBalance,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            HeaderBalanccApp(
                selectedYear = state.selectedYear,
                navController = navController,
                onYearSelected = { newYear ->
                    viewModel.onYearSelected(newYear)
                    yearMonthViewModel.setYear(newYear)
                },
            )

            Spacer(Modifier.height(BalanccSpacing.compact))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                state.months.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(BalanccSpacing.large),
                        contentAlignment = Alignment.Center,
                    ) {
                        EmptyState(
                            title = stringResource(R.string.no_movements_in_year, state.selectedYear),
                        )
                    }
                }
                else -> {
                    MonthsCards(
                        balances = state.months,
                        onMonthClick = { monthIndex ->
                            yearMonthViewModel.setMonthIndex(monthIndex)
                            navController.navigate(AppScreens.DetailScreen.route)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun AddBalanceFAB(
    navController: NavController,
    onFastAddBalance: (isFastAddBalance: Boolean) -> Unit,
) {
    BalanccFab(
        onClick = {
            onFastAddBalance(true)
            navController.navigate(AppScreens.AddIncomeOrExpenseScreen.route)
        },
        contentDescription = stringResource(R.string.add),
    )
}

@Composable
fun HeaderBalanccApp(
    selectedYear: String,
    navController: NavController,
    onYearSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = BalanccSpacing.standard, vertical = BalanccSpacing.small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            IconButton(
                onClick = { navController.navigate(AppScreens.SettingsScreen.route) },
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(BalanccSpacing.small))

        YearSelector(
            selectedYear = selectedYear,
            onYearSelected = onYearSelected,
        )
    }
}

@Composable
fun MonthsCards(
    balances: List<MonthsBalanceUi>,
    onMonthClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = BalanccSpacing.standard,
            end = BalanccSpacing.standard,
            top = BalanccSpacing.micro,
            bottom = 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(BalanccSpacing.compact),
    ) {
        items(
            items = balances,
            key = { it.monthIndex },
        ) { balance ->
            FinancialSummaryCard(
                monthName = balance.monthName,
                income = balance.income,
                expense = balance.expense,
                balance = balance.balance,
                onClick = { onMonthClick(balance.monthIndex) },
            )
        }
    }
}
