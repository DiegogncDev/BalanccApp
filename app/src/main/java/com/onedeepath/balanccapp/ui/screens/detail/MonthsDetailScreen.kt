package com.onedeepath.balanccapp.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.onedeepath.balanccapp.core.formatCurrency
import com.onedeepath.balanccapp.ui.presentation.model.BalanceModel
import com.onedeepath.balanccapp.ui.presentation.model.TabItem
import com.onedeepath.balanccapp.ui.presentation.viewmodel.BalanceViewModel
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.AppScreens

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
    //  val totalBalance = balanceViewModel.getTotalBalance()

    val totalBalance = remember(incomes, expenses) {
        val totalInc = incomes.sumOf { it.amount }
        val totalExp = expenses.sumOf { it.amount }
        totalInc - totalExp
    }

    var donutChartHeight by remember {
        mutableStateOf(200.dp)
    }


    val connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y
            val newHeight = donutChartHeight + delta.dp
            val oldHeight = donutChartHeight
            donutChartHeight = newHeight.coerceIn(0.dp, 200.dp)
            val consumed = donutChartHeight - oldHeight

            return Offset(0f, consumed.value)
        }
    }


    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFBBF3BE)),
        containerColor = Color(0xFF88199A),
        floatingActionButton = { AddIncomeOrExpenseFAB(navController = navController) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .nestedScroll(connection),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.padding(top = 64.dp))
            Text(
                text = month,
                fontSize = 35.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
            )
            Spacer(Modifier.height(4.dp))
            Text(text = year, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Spacer(Modifier.height(16.dp))
            //---------- Section 1
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val sign = if (totalBalance > 0) "+" else if (totalBalance < 0) "-" else ""
                Text(
                    text = sign + formatCurrency(totalBalance),
                    fontSize = 35.sp,
                    fontWeight = Bold,
                    color = if (totalBalance > 0) Color.Green else if (totalBalance < 0) Color.Red else Color.Black
                )
                Spacer(Modifier.padding(bottom = 24.dp))
            }
            //------- Section 2
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color(0xFFD8D3EF),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .padding(8.dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                TabRowIncomesExpenses(
                    incomes = incomes,
                    expenses = expenses,
                    viewModel = balanceViewModel,
                    donutChartHeight
                )
            }
            AddIncomeOrExpenseFAB(navController = navController)
        }
    }
}

@Composable
fun DonutChart(
    entries: List<PieEntry>,
    colors: List<Int>,
    centerText: String   // Aquí pasas el total formateado
) {
    AndroidView(
        factory = { context ->
            PieChart(context).apply {

                // --- Configuración del donut ---
                description.isEnabled = false
                setUsePercentValues(true)

                isDrawHoleEnabled = true
                holeRadius = 60f            // Tamaño del agujero → donut
                transparentCircleRadius = 65f

                setHoleColor(android.graphics.Color.WHITE)

                // Texto en el centro
                setDrawCenterText(true)
                centerTextRadiusPercent = 100f
                setCenterTextColor(android.graphics.Color.BLACK)
                setCenterTextSize(20f)

                legend.isEnabled = false

                animateY(900)
            }
        },
        update = { chart ->

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = colors
            dataSet.sliceSpace = 2f

            // Opcional: para que los valores no salgan sobre el donut
            dataSet.setDrawValues(false)

            val data = PieData(dataSet)

            // Asignar datos al chart
            chart.data = data
            chart.centerText = centerText
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    )
}

@Composable
fun TabRowIncomesExpenses(
    incomes: List<BalanceModel>,
    expenses: List<BalanceModel>,
    viewModel: BalanceViewModel,
    donutChartHeight: Dp
) {

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


    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.height(45.dp),
        indicator = {},
        divider = {}
    ) {
        tabItems.forEachIndexed { index, item ->
            val selected = selectedTabIndex == index
            Tab(
                modifier = if (selected) Modifier
                    .clip(RoundedCornerShape(20))
                    .background(Color(0xFF88199A))
                else Modifier
                    .clip(RoundedCornerShape(20))
                    .background(Color.White),
                selected = selected,     //index == selectedTabIndex
                onClick = {
                    selectedTabIndex = index
                },
                text = { Text(text = item.title, color = Color(0xE98A64D2)) },

                )
        }
    }
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) { index ->
        when (index) {
            0 -> IncomePage(incomes, viewModel, donutChartHeight)
            1 -> ExpensePage(expenses, viewModel, donutChartHeight)
        }
    }


}

@Composable
fun IncomePage(incomes: List<BalanceModel>, viewModel: BalanceViewModel, donutChartHeight: Dp) {

    val totalIncomes = viewModel.totalIncomes.collectAsState()

    val pieChartEntries = incomes.map {

        PieEntry(it.amount.toFloat(), it.category)

    }
    val pieColors = incomes.map {
      //  viewModel.getColorCategoryNew(it).color.toArgb()
        viewModel.getColorCategory(it).color.toArgb()
    }


    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.padding(vertical = 4.dp))
        Card(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(donutChartHeight)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(Color(0xFFBBF3BE))
        ) {
            DonutChart(
                entries = pieChartEntries,
                colors = pieColors,
                centerText = formatCurrency(totalIncomes.value)
            )

        }
        Spacer(Modifier.padding(8.dp))
        Text(
            text = "Incomes",
            color = Color.Black,
            fontWeight = Bold,
            fontSize = 25.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
        IncomeList(incomes, viewModel)
    }
}

@Composable
fun ExpensePage(expenses: List<BalanceModel>, viewModel: BalanceViewModel, donutChartHeight: Dp) {

    val totalExpenses = viewModel.totalExpenses.collectAsState()

    val pieChartEntries = expenses.map {

        PieEntry(it.amount.toFloat(), it.category)

    }
    val pieColors = expenses.map {
        //  viewModel.getColorCategoryNew(it).color.toArgb()
        viewModel.getColorCategory(it).color.toArgb()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.padding(vertical = 4.dp))
        Card(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(donutChartHeight)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(Color(0xFFF10E3C))
        ) {
            DonutChart(
                entries = pieChartEntries,
                colors = pieColors,
                centerText = formatCurrency(totalExpenses.value)
            )

        }
        Spacer(Modifier.padding(8.dp))

        Text(
            text = "Expenses",
            color = Color.Black,
            fontSize = 25.sp,
            fontWeight = Bold,
            modifier = Modifier.padding(start = 16.dp)
        )
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
        } else {
            if (incomes.isEmpty()) {
                Text("No incomes")
            } else {
                LazyColumn(
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
        Modifier.fillMaxSize()
    ) {

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            if (expenses.isEmpty()) {
                Text("No expenses")
            } else {
                LazyColumn(
                    Modifier.fillMaxWidth(),
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
            .height(120.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = categoryProperties.color)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(categoryProperties.icon),
                tint = Color.Black,
                contentDescription = "Add",
                modifier = Modifier.size(38.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(
                Modifier.weight(1f)
            ) {
                Text(
                    text = income.category,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = income.description,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            Text(
                text = formatCurrency(income.amount),
                fontSize = 18.sp,
                fontWeight = Bold,
                color = Color.Black,
            )
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
            .height(120.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = categoryProperties.color)
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(categoryProperties.icon),
                tint = Color.Black, contentDescription = "Add",
                modifier = Modifier.size(38.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(
                Modifier
                    .width(150.dp)
            ) {
                Text(
                    text = expense.category,
                    color = Color.Black,
                    fontWeight = Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = expense.description,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = formatCurrency(expense.amount),
                fontSize = 18.sp,
                fontWeight = Bold,
                color = Color.Black
            )
//            Button(
//                onClick = {
//                    viewModel.deleteBalance(expense.id)
//                }
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.ic_trash),
//                    contentDescription = ""
//                )
//            }
        }
    }
}

@Composable
fun AddIncomeOrExpenseFAB(navController: NavController) {

        FloatingActionButton(
            onClick = {
                navController.navigate(AppScreens.AddIncomeOrExpenseScreen.route)
            },
            containerColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(50)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(32.dp)

            )
        }

}