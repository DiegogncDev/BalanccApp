package com.onedeepath.balanccapp.ui.screens.main

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.core.formatCurrency
import com.onedeepath.balanccapp.ui.navigation.AppScreens
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.main.model.MonthsBalanceUi
import com.onedeepath.balanccapp.ui.screens.main.viewmodel.MainViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavController,
    yearMonthViewModel: YearMonthViewModel,
    viewModel: MainViewModel = hiltViewModel()
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
        floatingActionButton = { AddBalanceFAB(
            navController = navController,
            onFastAddBalance = yearMonthViewModel::setIsFastAddBalance)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
            ) {
                HeaderBalanccApp(
                    selectedYear = state.selectedYear,
                    onYearSelected = { newYear ->
                        viewModel.onYearSelected(newYear)
                        yearMonthViewModel.setYear(newYear)
                    }
                )

                if (state.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    MonthsCards(
                        balances = state.months,
                        onMonthClick = { index -> yearMonthViewModel.setMonthIndex(index) },
                        navController = navController
                    )
                }
        }
    }

}

@Composable
fun AddBalanceFAB(navController: NavController, onFastAddBalance: (isFastAddBalance: Boolean) -> Unit) {

    FloatingActionButton(
        onClick = {
            onFastAddBalance(true)
            navController.navigate(AppScreens.AddIncomeOrExpenseScreen.route)
        },
        containerColor = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(25)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(32.dp)

        )
    }
}

@Composable
fun HeaderBalanccApp(
    selectedYear: String,
    onYearSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.app_name), // app_name
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Selector de año estilo "Chip"
        YearFilterChip(selectedYear, onYearSelected)
    }
}


@Composable
fun BalanceCardItem(balance: MonthsBalanceUi, onClick: () -> Unit, navController: NavController) {

    Card(
        onClick = {
            onClick()
            navController.navigate(AppScreens.DetailScreen.route)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = balance.monthName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(Modifier.height(16.dp))


            Row(Modifier.fillMaxWidth()) {
                BalanceStat(
                    label = stringResource(R.string.incomes),
                    value = balance.income,
                    color = Color(0xFF4CAF50), // Un verde más amigable
                    modifier = Modifier.weight(1f)
                )
                BalanceStat(
                    label = stringResource(R.string.expenses),
                    value = balance.expense,
                    color = Color(0xFFE57373), // Un rojo coral
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Balance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (balance.balance > 0) "+${formatCurrency(balance.balance)}" else formatCurrency(balance.balance),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = if (balance.balance >= 0) Color(0xFF4CAF50) else Color(0xFFE57373)
                )
            }
        }
    }

}

@Composable
fun BalanceStat(label: String, value: Double, color: Color, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        Text(formatCurrency(value), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = color)
    }
}

@Composable
fun MonthsCards(
    navController: NavController,
    balances: List<MonthsBalanceUi>,
    onMonthClick: (Int) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        items(balances) {  balance ->

            BalanceCardItem(
                balance = balance,
                onClick = { onMonthClick(balance.monthIndex) },
                navController = navController
            )

        }
    }

}

@Composable
fun YearFilterChip(selectedYear: String, onYearSelected: (String) -> Unit) {

    var showDialog by remember { mutableStateOf(false) }

    Surface(
        onClick = { showDialog = true },
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(selectedYear, fontWeight = FontWeight.Medium)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
    }

    if (showDialog) {
        YearPickerDialog(
            onYearSelected = { year ->
                onYearSelected(year.toString())
                showDialog = false
            },
            onDismiss = { showDialog = false })
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearFilter(selectedYear: String, onYearSelected: (String) -> Unit) {

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(50))
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(50)),
            value = selectedYear,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface

            ),
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.primary,
                unfocusedContainerColor = MaterialTheme.colorScheme.primary,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(50))
                .clickable { showDialog = true }

        )
    }


    if (showDialog) {
        YearPickerDialog(
            onYearSelected = { year ->
                onYearSelected(year.toString())
                showDialog = false
            },
            onDismiss = { showDialog = false })
    }
}

@Composable
fun YearPickerDialog(
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = remember { 2025 }
    val years = remember { (currentYear..(currentYear + 200)).toList() }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor =MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.select_year),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .height(280.dp)
                            .padding(horizontal = 8.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {

                        items(years) { year ->
                            YearItem(
                                year = year,
                                onClick = {
                                    onYearSelected(year)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun YearItem(
    year: Int,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFFEDEDED) else Color.Transparent,
        animationSpec = tween(150), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(backgroundColor, shape = RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                isPressed = true
                onClick()
            }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = year.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}