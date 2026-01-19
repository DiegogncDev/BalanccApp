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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
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
import java.time.Month
import kotlin.collections.component1
import kotlin.collections.component2

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
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            HeaderSection(uiState)

            MainContentSheet(uiState = uiState, onDelete = viewModel::deleteBalance)

        }
    }
}

@Composable
fun MainContentSheet(uiState: MonthsDetailUiState, onDelete: (Int) -> Unit) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabItems = listOf("Ingresos", "Gastos")
    val pagerState = rememberPagerState { tabItems.size }

    LaunchedEffect(selectedTabIndex) { pagerState.animateScrollToPage(selectedTabIndex) }
    LaunchedEffect(pagerState.currentPage) { selectedTabIndex = pagerState.currentPage }

    // Superficie blanca redondeada que contiene
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            // TabRow Estilo Pill
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = {}, // Eliminamos el indicador de línea por defecto
                divider = {},
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            ) {
                tabItems.forEachIndexed { index, title ->
                    val selected = selectedTabIndex == index
                    Tab(
                        selected = selected,
                        onClick = { selectedTabIndex = index },
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent),
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { index ->
                when (index) {
                    0 -> IncomePage(uiState.incomes, uiState.incomeChart, onDelete)
                    1 -> ExpensePage(uiState.expenses, uiState.expenseChart, onDelete)
                }
            }
        }
    }
}

@Composable
fun HeaderSection(uiState: MonthsDetailUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${uiState.month} ${uiState.year}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = uiState.totalBalanceFormatted,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            ),
            color = uiState.totalBalanceColor
        )
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
fun DateHeader(date: String) {
    Text(
        text = date,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary, // O usa OutlineLight de tu paleta
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
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

                val sortedIncomes = incomes.sortedByDescending {
                    it.day.substringAfterLast("-").toIntOrNull() ?: 0
                }

                val groupedIncomes = sortedIncomes.groupBy { it.day }
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    groupedIncomes.forEach { (rawDay, incomesList) ->

                        item {
                            val cleanDay = rawDay.substringAfterLast("-")
                            val monthName = incomesList.first().month
                            DateHeader(date = "$cleanDay $monthName")
                        }

                        items(incomesList.size) { income ->
                            IncomeCard(incomesList[income], onDelete)
                        }
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
            val sortedIncomes = expenses.sortedByDescending {
                it.day.substringAfterLast("-").toIntOrNull() ?: 0
            }

            val groupedExpenses = sortedIncomes.groupBy { it.day }

            LazyColumn(
                Modifier.fillMaxWidth()) {
                groupedExpenses.forEach { (rawDay, ExpensesList) ->
                    item {
                        val cleanDay = rawDay.substringAfterLast("-")
                        val monthName = ExpensesList.first().month
                        DateHeader(date = "$cleanDay $monthName")
                    }
                    items(ExpensesList.size) { income ->
                        IncomeCard(ExpensesList[income], onDelete)
                    }
                }
            }
        }
    }
}

@Composable
fun IncomeCard(income: BalanceModel, onDelete: (Int) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = income.category.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo para el icono
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.5f)
            ) {
                Icon(
                    painter = painterResource(income.category.icon),
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = Color.DarkGray
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = income.category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                if (income.description.isNotEmpty()) {
                    Text(
                        text = income.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = formatCurrency(income.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2E7D32)
            )

            IconButton(onClick = { onDelete(income.id) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = "Borrar",
                    modifier = Modifier.size(20.dp),
                    tint = Color.DarkGray.copy(alpha = 0.6f)
                )
            }
        }
    }

}

@Composable
fun ExpenseCard(expense: BalanceModel, onDelete: (Int) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = expense.category.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo para el icono
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.5f)
            ) {
                Icon(
                    painter = painterResource(expense.category.icon),
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = Color.DarkGray
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                if (expense.description.isNotEmpty()) {
                    Text(
                        text = expense.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = formatCurrency(expense.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2E7D32)
            )

            IconButton(onClick = { onDelete(expense.id) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = "Borrar",
                    modifier = Modifier.size(20.dp),
                    tint = Color.DarkGray.copy(alpha = 0.6f)
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