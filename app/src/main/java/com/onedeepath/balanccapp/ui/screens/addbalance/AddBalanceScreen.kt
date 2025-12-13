package com.onedeepath.balanccapp.ui.screens.addbalance

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.onedeepath.balanccapp.core.cleanAmountForStorage
import com.onedeepath.balanccapp.core.formatAmountForDisplay
import com.onedeepath.balanccapp.core.formatCurrency
import com.onedeepath.balanccapp.ui.presentation.model.Categories
import com.onedeepath.balanccapp.ui.presentation.model.TabItem
import com.onedeepath.balanccapp.ui.presentation.viewmodel.BalanceViewModel
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import java.time.LocalDate
import java.time.YearMonth


private val categories = listOf(
    Categories.Investment,
    Categories.Work,
    Categories.Gift,
    Categories.Grocery,
    Categories.Entertainment,
    Categories.Transport,
    Categories.Utilities,
    Categories.Rent,
    Categories.Health,
    Categories.Travel,
    Categories.Food,
    Categories.Education,
    Categories.Other)

@Composable
fun AddIncomeOrExpenseScreen(
    balanceViewModel: BalanceViewModel = hiltViewModel(),
    yearMonthViewModel: YearMonthViewModel) {

    val selectedYear:String by yearMonthViewModel.selectedYear.collectAsState()
    val selectedMonth:String by yearMonthViewModel.selectedMonthIndex.collectAsState()


    val context = LocalContext.current

    var typeIncomeOrExpense by remember { mutableStateOf(true) }
    var amount by remember { mutableStateOf("") }
    var category by remember {mutableStateOf(categories[0]) }
    var details by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(32.dp))
        Text("Add", fontSize = 45.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        //IncomeExpenseRB(isIncome = typeIncomeOrExpense, onCheckedChange = {typeIncomeOrExpense = it})
        IncomeExpenseTabview(isIncome = typeIncomeOrExpense, onCheckedChange = {typeIncomeOrExpense = it})
        Spacer(Modifier.height(8.dp))

        AddAmountTF(amount = amount, onAmountChange = {amount = it})
        Spacer(Modifier.height(32.dp))

        AddCategorySelector(selectedCategory = category, onCategoryChange = {category = it})
        Spacer(Modifier.height(32.dp))

        DatePickerSelector(selectedYear.toInt(), selectedMonth) { date ->
            selectedDay = date.dayOfMonth.toString()
        }
        Spacer(Modifier.height(32.dp))

        DetailsTF(details = details, onDetailsChange = {details = it})
        Spacer(Modifier.height(32.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
            if (amount.isNotBlank()){
                balanceViewModel.addBalance(
                    type = if (typeIncomeOrExpense) "income" else "expense",
                    amount = amount.toDouble(),
                    day = selectedDay,
                    month = selectedMonth,
                    year = selectedYear,
                    category = category.name,
                    description = details
                    )
                Toast.makeText(context, "Balance Added Successfully", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show()
                 }
            }
        ) {
            Text("Add", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }

}



@Composable
fun DetailsTF(details: String, onDetailsChange: (String) -> Unit) {

    OutlinedTextField(
        value = details,
        onValueChange = onDetailsChange,
        label = { Text("Details") },
        placeholder = { Text("Enter more information...") },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp), // altura más grande para parecer un "text area"
        maxLines = 5,
        singleLine = false
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSelector(
    selectedYear: Int,
    selectedMonth: String,
    onDateSelected: (LocalDate) -> Unit
) {
    val selectedMonthMapped = when (selectedMonth) {
        "January" -> 0
        "February" -> 1
        "March" -> 2
        "April" -> 3
        "May" -> 4
        "June" -> 5
        "July" -> 6
        "August" -> 7
        "September" -> 8
        "October" -> 9
        "November" -> 10
        "December" -> 11
        else -> 0
    }

    var calendarState = UseCaseState()

    val startDate = LocalDate.of(selectedYear, selectedMonthMapped + 1, 1)
    val endDate = YearMonth.of(selectedYear, selectedMonthMapped + 1).atEndOfMonth()

    var currentDay by remember { mutableStateOf("") }

    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        CalendarDialog(
            state = calendarState,
            config = CalendarConfig(
                monthSelection = false,
                yearSelection = false,
                style = CalendarStyle.MONTH,
                boundary = startDate..endDate,
            ),
            selection = CalendarSelection.Date { date ->
                onDateSelected(date)
                currentDay = date.dayOfMonth.toString()
            }
        )

        Button(
            onClick = {
                    calendarState.show()
            }
        ) {
                Text(text = if (currentDay.isEmpty()) "Elegir día" else currentDay, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(16.dp))
        Text(text = selectedMonth, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(Modifier.width(16.dp))
        Text("$selectedYear", fontWeight = FontWeight.Bold, fontSize = 24.sp)

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategorySelector(selectedCategory: Categories, onCategoryChange: (Categories) -> Unit) {

    var isExpanded by remember { mutableStateOf(false) }

    Box(
        Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded},

        ) {
            TextField(
                value = selectedCategory.name,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)},
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(selectedCategory.icon),
                        contentDescription = "",
                        tint = selectedCategory.color
                    )
                }
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = {isExpanded = false}
            ) { categories.forEachIndexed { index, category ->
                DropdownMenuItem(
                    text = {Text(text = category.name, fontWeight = FontWeight.Bold, color = category.color)},
                    onClick = {
                        onCategoryChange(category)
                        isExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    leadingIcon =  { Icon(
                        painter = painterResource(id = category.icon),
                        contentDescription = "icons",
                        tint = category.color
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AddAmountTF(amount: String, onAmountChange: (String) -> Unit) {

    val cleanedAmount = cleanAmountForStorage(amount)

    val formattedAmount = formatAmountForDisplay(cleanedAmount)

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().height(90.dp),
        value = formattedAmount,
        singleLine = true,
        maxLines = 15,
        textStyle = TextStyle(fontSize = 35.sp, fontWeight = FontWeight.Bold),
        onValueChange = { newValue ->
            // Clean the input to only allow digits
            val newCleanedValue = newValue.replace(Regex("[^0-9]"), "")

            // Limit the input to 15 characters if the values are no digits
            if (newCleanedValue.length > 15) return@OutlinedTextField

            if (newCleanedValue != cleanedAmount) {
                onAmountChange(newCleanedValue)
            }

                        },
        label = {
            Text(
                "Amount", fontSize = 18.sp
            )
        },
        placeholder = {
            Text(text = "Enter Amount", fontWeight = FontWeight.Bold, fontSize = 35.sp)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        )
    )
}


@Composable
fun IncomeExpenseTabview(isIncome: Boolean, onCheckedChange: (Boolean) -> Unit) {

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    val tabItems = listOf(
        TabItem("Incomes", Icons.Outlined.KeyboardArrowUp, Icons.Filled.KeyboardArrowUp),
        TabItem("Expenses", Icons.Outlined.KeyboardArrowDown, Icons.Filled.KeyboardArrowDown)
    )

    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.height(120.dp),
        indicator = {},
        divider = {}
    ) {
        tabItems.forEachIndexed { index, item ->
            val selected = selectedTabIndex == index
            Tab(
                modifier = if (selected) Modifier
                    .clip(RoundedCornerShape(5))
                    .background(Color(0xff1E76DA))
                else Modifier
                    .clip(RoundedCornerShape(5))
                    .background(Color.White),
                selected = selected,     //index == selectedTabIndex
                onClick = {
                    selectedTabIndex = index
                    onCheckedChange(index == 0)
                }

            ) {
                Text(
                    text = item.title,
                    color = Color(0xff6FAAEE),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp)

            }
        }

    }
}


