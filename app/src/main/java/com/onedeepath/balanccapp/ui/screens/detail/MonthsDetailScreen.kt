package com.onedeepath.balanccapp.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.ui.presentation.model.BalanceModel
import com.onedeepath.balanccapp.ui.screens.AppScreens
import com.onedeepath.balanccapp.ui.presentation.model.TabItem
import com.onedeepath.balanccapp.ui.presentation.viewmodel.BalanceViewModel
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.math.exp
import kotlin.math.max

@Composable
fun MonthsDetailScreen(
    navController: NavController,
    balanceViewModel: BalanceViewModel = hiltViewModel(),
    yearMonthViewModel: YearMonthViewModel
    ) {

    val year by yearMonthViewModel.selectedYear.collectAsState()
    val month by yearMonthViewModel.selectedMonthIndex.collectAsState()

    LaunchedEffect(Unit) {
        balanceViewModel.getIncomeBalance(year, month)
        balanceViewModel.getExpenseBalance(year, month)
    }

    val incomes by balanceViewModel.incomeBalance.collectAsState()
    val expenses by balanceViewModel.expenseBalance.collectAsState()
    val totalBalance = balanceViewModel.getTotalBalance()

    Box (
        modifier =  Modifier.fillMaxSize().padding(top=16.dp),
    ) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text(text = month, fontSize = 45.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(8.dp))
            Text(text = year, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(8.dp))

            Card (
                modifier = Modifier.padding(16.dp).height(80.dp).width(250.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray)
            ) {
                Column(
                    Modifier.padding(8.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if(totalBalance > 0) {
                            totalBalance.toString()}
                        else if (totalBalance < 0){
                            "-" + totalBalance.toString()
                        } else{
                            totalBalance.toString()
                        },
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (totalBalance > 0) {Color.Green} else if(totalBalance < 0) {Color.Red} else {Color.Black},
                        modifier = Modifier)
                    Spacer(Modifier.height(8.dp))
                }
            }
            TabRowIncomesExpenses(incomes = incomes , expenses =  expenses, viewModel = balanceViewModel)
        }
    }
    AddIncomeOrExpenseFAB(navController)
}

@Composable
fun AddIncomeOrExpenseFAB(navController: NavController) {

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ){
        FloatingActionButton(
            onClick = {
                navController.navigate(AppScreens.AddIncomeOrExpenseScreen.route)
            },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }
    }
}

@Composable
fun TabRowIncomesExpenses(incomes: List<BalanceModel>, expenses: List<BalanceModel>, viewModel: BalanceViewModel) {

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    val tabItems = listOf(
        TabItem("Incomes", Icons.Outlined.KeyboardArrowUp, Icons.Filled.KeyboardArrowUp),
        TabItem("Expenses", Icons.Outlined.KeyboardArrowDown, Icons.Filled.KeyboardArrowDown)
    )

    val pagerState = rememberPagerState {
        tabItems.size
    }

    // LaunchedEffect only works if the key is changed. We used this twice to synchronize the pager and the tab.
    LaunchedEffect(selectedTabIndex) { // if the user touch a window, the pager slide on it window
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage) { // if the user slide a window, the pager change on it window
        selectedTabIndex = pagerState.currentPage
    }


    TabRow (
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.background(color = Color.White).clip(RoundedCornerShape(50)).padding(1.dp)
    ) {
        tabItems.forEachIndexed { index, item ->
            val selected = selectedTabIndex  == index
            Tab(
                modifier = if (selected) Modifier.
                    clip(RoundedCornerShape(50))
                    .background(Color.White)
                else Modifier.
                    clip(RoundedCornerShape(50))
                    .background(Color(0xff1E76DA)),
                selected = selected,     //index == selectedTabIndex
                onClick = {
                    selectedTabIndex = index
                },
                text = { Text(text = item.title, color = Color(0xff6FAAEE)) },
            )
        }
    }
    Spacer(Modifier.padding(8.dp))
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth().height(500.dp)
    ) { index ->
        when (index) {
                0 -> IncomePage(incomes, viewModel)
                1 -> ExpensePage(expenses, viewModel)
        }
    }
}

@Composable
fun IncomePage(incomes: List<BalanceModel>, viewModel: BalanceViewModel) {

    val totalIncomes = viewModel.totalIncomes.collectAsState()

    Column(
        Modifier.fillMaxSize().background(Color.White, shape = RoundedCornerShape(16.dp)).padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            onClick = {},
            modifier = Modifier.width(250.dp).height(100.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Total incomes", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 25.sp)
                Text(text = totalIncomes.value.toString(), color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 30.sp)
            }

        }
        Spacer(Modifier.padding(8.dp))
        IncomeList(incomes, viewModel)
    }
}

@Composable
fun ExpensePage(expenses: List<BalanceModel>, viewModel: BalanceViewModel) {

    val totalExpenses = viewModel.totalExpenses.collectAsState()

    Column(
        Modifier.fillMaxSize().background(Color.White, shape = RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = totalExpenses.value.toString(), color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 35.sp)
                Text(text = "total expenses", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
        ExpenseList(expenses, viewModel)
    }
}

@Composable
fun IncomeList(incomes: List<BalanceModel>, viewModel: BalanceViewModel) {

    val isLoading by viewModel.isLoadingIncome.collectAsState()

    Column(
        Modifier.fillMaxSize(),
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else{
            if (incomes.isEmpty()) {
                Text("No incomes")
            } else {
                LazyColumn (
                    Modifier.fillMaxWidth()
                ) {
                    items(incomes.size) { income ->
                        IncomeCard(incomes[income], viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseList(expenses: List<BalanceModel>, viewModel: BalanceViewModel) {

    val isLoading by viewModel.isLoadingExpense.collectAsState()

    Column(
        Modifier.fillMaxWidth()
    ) {

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else{
            if (expenses.isEmpty()) {
                Text("No expenses")
            }else {
                LazyColumn (
                    Modifier.fillMaxWidth().background(Color.Red),
                ) {
                    items(expenses.size) { expense ->
                        ExpenseCard(expenses[expense], viewModel)
                    }
                }
            }
        }
    }
}
@Composable
fun IncomeCard(income: BalanceModel, viewModel: BalanceViewModel) {

    val categoryProperties = viewModel.getColorCategory(income)

    Card(
        onClick = {
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = categoryProperties.color)
    ) {
        Row (
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(categoryProperties.icon),
                tint = Color.Black,
                contentDescription = "Add",
                modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(8.dp))
            Column (
                Modifier.weight(1f)
            ) {
                Text(text = income.category, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(6.dp))
                Text(text = income.description, fontSize = 14.sp, maxLines = 1,  )
            }

            Text(text = income.amount.toString(), fontSize = 16.sp, fontWeight = Bold, color = Color.Black)
//            Button(
//                onClick = {
//                    viewModel.deleteBalance(income.id)
//                },
//                modifier = Modifier.size(18.dp)
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.ic_trash),
//                    contentDescription = "",
//                    modifier = Modifier.size(14.dp)
//                )
//            }
        }
    }
}

@Composable
fun ExpenseCard(expense: BalanceModel, viewModel: BalanceViewModel) {

    val categoryProperties = viewModel.getColorCategory(expense)

    Card(
        onClick = {
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = categoryProperties.color)
    ) {
        Row (
            Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(categoryProperties.icon), tint = Color.Black, contentDescription = "Add", modifier = Modifier.height(32.dp).width(32.dp))
            Spacer(Modifier.weight(1f))
            Column (
                Modifier.width(200.dp)
            ) {
                Text(text = expense.category, color = Color.Black)
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text(text = expense.description)
            }
            Spacer(Modifier.weight(1f))
            Text(text = expense.amount.toString())
            Button(
                onClick = {
                    viewModel.deleteBalance(expense.id)
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = ""
                )
            }
        }
    }
}
