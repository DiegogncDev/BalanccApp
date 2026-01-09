package com.onedeepath.balanccapp.ui.screens.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
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
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.core.formatCurrency
import com.onedeepath.balanccapp.domain.model.BalanceModel
import com.onedeepath.balanccapp.ui.navigation.AppScreens
import com.onedeepath.balanccapp.ui.presentation.model.TabItem
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.detail.model.MonthsChartUiState
import com.onedeepath.balanccapp.ui.screens.detail.model.MonthsDetailUiState
import com.onedeepath.balanccapp.ui.screens.detail.model.MyMonthsChartUiState
import com.onedeepath.balanccapp.ui.screens.detail.model.PieChartData
import com.onedeepath.balanccapp.ui.screens.detail.viewmodel.MonthsDetailViewModel

enum class ChartType{
    INCOME,
    EXPENSE
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MonthsDetailScreen(
    navController: NavController,
    viewModel: MonthsDetailViewModel = hiltViewModel(),
    yearMonthViewModel: YearMonthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val year by yearMonthViewModel.selectedYear.collectAsState()
    val month by yearMonthViewModel.selectedMonthIndex.collectAsState()

    LaunchedEffect(year, month) {
        viewModel.load(year = year, monthName = month)
    }

    Scaffold (
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primary,
        floatingActionButton = {
            yearMonthViewModel.setIsFastAddBalance(false)
            AddIncomeOrExpenseFAB(navController = navController) }
    ) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            HeaderDate(uiState = uiState)
            MonthBalance(uiState = uiState)

            //------- Section 2

            TabRowIncomesExpenses(
                incomes = uiState.incomes,
                expenses = uiState.expenses,
                incomeChart = uiState.incomeChart,
                expenseChart = uiState.expenseChart,
                onDelete = viewModel::deleteBalance
                )
        }
    }
}

@Composable
fun HeaderDate(uiState: MonthsDetailUiState) {
    Spacer(Modifier.padding(top = 64.dp))
    Text(
        text = uiState.month,
        fontSize = 35.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Spacer(Modifier.height(4.dp))
    Text(text = uiState.year, fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(16.dp))
}

@Composable
fun MonthBalance(uiState: MonthsDetailUiState) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = uiState.totalBalanceFormatted,
            fontSize = 35.sp,
            fontWeight = Bold,
            color = uiState.totalBalanceColor
        )
        Spacer(Modifier.padding(bottom = 24.dp))
    }
}

@Composable
fun TabRowIncomesExpenses(
    incomes: List<BalanceModel>,
    expenses: List<BalanceModel>,
    incomeChart: MyMonthsChartUiState,
    expenseChart: MyMonthsChartUiState,
    onDelete: (Int) -> Unit
) {

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    val tabItems = listOf(
        TabItem(stringResource(R.string.incomes_tab),
            Icons.Outlined.KeyboardArrowUp,
            Icons.Filled.KeyboardArrowUp),
        TabItem(stringResource(R.string.expenses_tab),
            Icons.Outlined.KeyboardArrowDown,
            Icons.Filled.KeyboardArrowDown)
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


     Card(
         modifier = Modifier
             .fillMaxSize()
             .background(
                 color = MaterialTheme.colorScheme.surface,
                 shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
             .padding(8.dp),
         shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
     ){
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .height(56.dp)
                .background(MaterialTheme.colorScheme.surface),
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
                        .background(MaterialTheme.colorScheme.surface),
                    selected = selected,     //index == selectedTabIndex
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {
                        Text(
                            text = item.title,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                )
            }
        }
         HorizontalPager(
             state = pagerState,
             modifier = Modifier
                 .weight(1f)
                 .background(MaterialTheme.colorScheme.surface)
         ) { index ->
             when (index) {
                 0 -> IncomePage(incomes, incomeChart, onDelete)
                 1 -> ExpensePage(expenses, expenseChart, onDelete)
             }
         }
     }
}

@Composable
fun DonutChart(
    entries: List<PieChartData>,
    colors: List<Int>,
    centerText: String,
    type: ChartType,
    nestedHeight: Dp// Aquí pasas el total formateado
) {
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(nestedHeight),
        factory = { context ->
            PieChart(context).apply {
                // --- Configuración del donut ---
                description.isEnabled = false
                setUsePercentValues(true)

                isDrawHoleEnabled = true
                holeRadius = 80f            // Tamaño del agujero → donut
                transparentCircleRadius = 65f

                setHoleColor(surfaceColor)

                setDrawEntryLabels(false)
                setEntryLabelColor(onSurfaceColor)

                // Texto en el centro
                setDrawCenterText(true)
                centerTextRadiusPercent = 100f
                setCenterTextColor(
                    if (type == ChartType.INCOME) Color(0xFF2E7D32).toArgb()
                    else Color(0xFFE53757).toArgb())
                setCenterTextSize(20f)
                setCenterTextTypeface(android.graphics.Typeface.DEFAULT_BOLD)


                legend.isEnabled = false
                legend.textColor = onSurfaceColor

                animateY(900)
            }
        },
        update = { chart ->

            val dataSet = PieDataSet(entries.map { PieEntry(it.value, "") }, "")
            dataSet.setDrawValues(false)
            dataSet.colors = colors
            dataSet.sliceSpace = 4f
            // Opcional: para que los valores no salgan sobre el donut

            val data = PieData(dataSet)

            // Asignar datos al chart
            chart.data = data
            chart.centerText = centerText
            chart.invalidate()
        },

        )
}

@Composable
fun IncomePage(incomes: List<BalanceModel>,
               incomeChart: MyMonthsChartUiState,
               onDelete: (Int) -> Unit) {

    var donutChartHeight by remember {
        mutableStateOf(200.dp)
    }

    val connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            donutChartHeight = (donutChartHeight + available.y.dp).coerceIn(80.dp, 200.dp)

            return Offset.Zero
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(top = 8.dp)
            .nestedScroll(connection = connection),
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.padding(vertical = 4.dp))
        if (incomes.isNotEmpty() && donutChartHeight > 90.dp ) {
            DonutChart(
                entries = incomeChart.entries,
                colors = incomeChart.colors,
                centerText = incomeChart.centerText,
                type = ChartType.INCOME,
                nestedHeight = donutChartHeight
            )
        }

        Spacer(Modifier.padding(8.dp))
        Text(
            text = stringResource(R.string.incomes),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = Bold,
            fontSize = 25.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
        IncomeList(incomes, onDelete)
    }
}

@Composable
fun ExpensePage(expenses: List<BalanceModel>,
                expenseChart: MyMonthsChartUiState,
                onDelete: (Int) -> Unit) {

    var donutChartHeight by remember {
        mutableStateOf(200.dp)
    }

    val connection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            donutChartHeight = (donutChartHeight + available.y.dp).coerceIn(80.dp, 200.dp)

            return Offset.Zero
        }
    }


    Column(
        Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(top = 8.dp)
            .nestedScroll(
                connection = connection
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.padding(vertical = 4.dp))
        if (expenses.isNotEmpty() && donutChartHeight > 90.dp ) {
            DonutChart(
                entries = expenseChart.entries,
                colors = expenseChart.colors,
                centerText = expenseChart.centerText,
                type = ChartType.EXPENSE,
                nestedHeight = donutChartHeight
            )
        }
        Spacer(Modifier.padding(8.dp))

        Text(
            text = stringResource(R.string.expenses),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 25.sp,
            fontWeight = Bold,
            modifier = Modifier.padding(start = 16.dp)
        )
        ExpenseList(expenses, onDelete)
    }
}

@Composable
fun IncomeList(incomes: List<BalanceModel>, onDelete: (Int) -> Unit) {

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
            if (incomes.isEmpty()) {
                Text(text = stringResource(R.string.no_incomes),
                    fontSize = 35.sp,
                    fontWeight = Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 64.dp)
                        ,
                    textAlign = TextAlign.Center)
            } else {
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    items(incomes.size) { income ->
                        IncomeCard(incomes[income], onDelete)
                    }
                }

        }
    }
}

@Composable
fun ExpenseList(expenses: List<BalanceModel>, onDelete: (Int) -> Unit) {

    Column(
        Modifier.fillMaxSize()
    ) {
        if (expenses.isEmpty()) {
            Text(text = stringResource(R.string.no_expenses),
                fontSize = 35.sp,
                fontWeight = Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 64.dp)
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center)
        } else {
            LazyColumn(
                Modifier.fillMaxWidth()) {
                    items(expenses.size) { expense ->
                    ExpenseCard(expenses[expense], onDelete)
                }
            }
        }
    }
}

@Composable
fun IncomeCard(income: BalanceModel, onDelete: (Int) -> Unit) {


    Card(
        onClick = {
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = income.category.color)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(income.category.icon),
                tint = Color.Black,
                contentDescription = "Add",
                modifier = Modifier.size(38.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(
                Modifier.weight(1f)
            ) {
                Text(
                    text = income.category.name,
                    color = Color.Black,
                    fontWeight = Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = income.description,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = Color.Black
                )
            }

            Text(
                text = formatCurrency(income.amount),
                fontSize = 18.sp,
                fontWeight = Bold,
                color = Color(0xFF2E7D32),
            )
            Spacer(Modifier.width(16.dp))
            IconButton(
                onClick = { onDelete(income.id) },
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxSize()
                        .size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ExpenseCard(expense: BalanceModel, onDelete: (Int) -> Unit) {


    Card(
        onClick = {
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = expense.category.color)
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(expense.category.icon),
                tint = Color.Black, contentDescription = "",
                modifier = Modifier.size(38.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(
                Modifier
                    .width(150.dp)
            ) {
                Text(
                    text = expense.category.name,
                    color = Color.Black,
                    fontWeight = Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = expense.description,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = Color.Black
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = formatCurrency(expense.amount),
                fontSize = 18.sp,
                fontWeight = Bold,
                color = Color.Red
                )
            Spacer(Modifier.width(16.dp))
            IconButton(
                onClick = { onDelete(expense.id) },
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(com.onedeepath.balanccapp.R.drawable.ic_trash),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxSize()
                        .size(20.dp)
                )
            }
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