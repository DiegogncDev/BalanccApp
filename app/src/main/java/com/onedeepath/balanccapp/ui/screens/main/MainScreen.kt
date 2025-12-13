package com.onedeepath.balanccapp.ui.screens.main

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.onedeepath.balanccapp.core.formatCurrency
import com.onedeepath.balanccapp.ui.presentation.model.BalanceByMonthEntity
import com.onedeepath.balanccapp.ui.presentation.model.MonthsCardModel
import com.onedeepath.balanccapp.ui.presentation.viewmodel.BalanceViewModel
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.AppScreens


@Composable
fun MainScreen(
    navController: NavController,
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
            .background(color = Color(0xFFD8D3EF)),

        ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "BalanccApp",
            fontSize = 45.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "SortBy", fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp)
        )
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
            }, navController = navController
        )
        Spacer(Modifier.height(32.dp))
    }

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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF7B6DD5))
    ) {
        Column(
            Modifier.padding(16.dp)
        ) {
            Text(
                text = item.monthName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Income", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    formatCurrency(item.incomeAmount),
                    color = Color.Green,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Expenses", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    formatCurrency(item.expenseAmount),
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Balance", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = formatCurrency(balance),
                    color = if (balance >= 0) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

            }
        }
    }
}

@Composable
fun MonthsCards(
    navController: NavController,
    year: String,
    balances: List<BalanceByMonthEntity>,
    onMonthClick: (Int) -> Unit
) {

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        itemsIndexed(months) { index, month ->

            val income = balances.filter { it.month == month && it.type == "income" }
                .sumOf { it.total }

            val expense = balances.filter { it.month == month && it.type == "expense" }
                .sumOf { it.total }


            BalanceCardItem(
                item = MonthsCardModel(
                    monthName = month,
                    incomeAmount = income,
                    expenseAmount = expense
                ),
                onClick = { onMonthClick(index) },
                navController = navController
            )

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearFilter(selectedYear: String, onYearSelected: (String) -> Unit) {

    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp).clip(RoundedCornerShape(50))
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp).clip(RoundedCornerShape(50)),
            value = selectedYear,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF7B6DD5),
                unfocusedContainerColor = Color(0xFF7B6DD5),
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
            onYearSelected = {
                onYearSelected(it.toString())
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
    val currentYear = remember { java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) }
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select a year",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .height(280.dp)
                            .padding(horizontal = 8.dp)
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


//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Selecciona un año") },
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(300.dp),
//        text = {
//            LazyColumn {
//                items(years) { year ->
//                    Text(
//                        text = year.toString(),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
//                                onYearSelected(year)
//                                onDismiss()
//                            }
//                            .padding(8.dp)
//                    )
//                }
//            }
//        },
//        confirmButton = {}
//    )
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
                color = Color(0xFF444444)
            )
        )
    }
}