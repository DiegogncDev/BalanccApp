package com.onedeepath.balanccapp.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.onedeepath.balanccapp.ui.presentation.model.BalanceByMonthEntity
import com.onedeepath.balanccapp.ui.presentation.model.MonthsCardModel
import com.onedeepath.balanccapp.ui.presentation.viewmodel.BalanceViewModel
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.AppScreens


@Composable
fun MainScreen(navController: NavController,
               yearMonthViewModel: YearMonthViewModel,
               balanceViewModel: BalanceViewModel
) {

    val selectedYear by yearMonthViewModel.selectedYear.collectAsState()
    val balancesByYear by balanceViewModel.balancesByYear.collectAsState()

    LaunchedEffect(selectedYear) {
        balanceViewModel.getBalanceByYear(selectedYear)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "BalanccApp", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(32.dp))
        Text("SortBy", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(Modifier.height(32.dp))
        YearFilter(
            selectedYear = selectedYear,
            onYearSelected = { yearMonthViewModel.setYear(it) }
        )
        Spacer(Modifier.height(16.dp))
        MonthsCards(
            balances = balancesByYear,
            year = selectedYear,
            onMonthClick = { index ->
                yearMonthViewModel.setMonthIndex(index)
            }
            ,navController = navController)
        Spacer(Modifier.height(32.dp))
    }

}


@Composable
fun YearPickerDialog(
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = remember { java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) }
    val years = remember { (currentYear..(currentYear + 1000)).toList() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona un año") },
        modifier = Modifier.fillMaxWidth().height(300.dp),
        text = {
            LazyColumn {
                items(years) {year ->
                    Text(
                        text = year.toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onYearSelected(year)
                                onDismiss()
                            }
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun BalanceCardItem(item: MonthsCardModel, onClick: () -> Unit, navController: NavController) {

    val balance = item.incomeAmount - item.expenseAmount

    Card(
        onClick = {
            onClick()
            navController.navigate(AppScreens.DetailScreen.route)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column (
            Modifier.padding(16.dp)
        ) {
            Text(
                text = item.monthName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Income")
                Text("${item.incomeAmount}", color = Color.Green)
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Expenses")
                Text("${item.expenseAmount}", color = Color.Red)
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Balance")
                Text(text = "${balance}",
                    color = if (balance >= 0) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold)

            }
        }
    }
}

@Composable
fun MonthsCards(
    navController: NavController,
    year: String,
    balances: List<BalanceByMonthEntity>,
    onMonthClick: (Int) -> Unit) {

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
    ) {
        itemsIndexed(months) {index, month ->

            val income = balances.
                        filter { it.month == month && it.type == "income"}
                        .sumOf { it.total }

            val expense = balances.
                        filter { it.month == month && it.type == "expense"}
                        .sumOf { it.total }


            BalanceCardItem(
                item = MonthsCardModel(monthName = month, incomeAmount = income, expenseAmount = expense),
                onClick = { onMonthClick(index) },
                navController = navController
            )

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearFilter(selectedYear: String, onYearSelected: (String) -> Unit) {

    var showDialog by remember {mutableStateOf(false)}

    TextField(
        value = selectedYear,
        onValueChange = {},
        readOnly = true,
        enabled = false,
        colors = ExposedDropdownMenuDefaults.textFieldColors(),
        modifier = Modifier.fillMaxWidth().clickable { showDialog = true }
    )

    if (showDialog) {
        YearPickerDialog(
            onYearSelected = {
                onYearSelected(it.toString())
                showDialog = false
            },
            onDismiss = {showDialog = false})
    }

}



