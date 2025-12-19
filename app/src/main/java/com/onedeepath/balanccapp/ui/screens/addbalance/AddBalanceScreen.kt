package com.onedeepath.balanccapp.ui.screens.addbalance

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import com.onedeepath.balanccapp.R
import com.onedeepath.balanccapp.core.cleanAmountForStorage
import com.onedeepath.balanccapp.core.formatAmountForDisplay
import com.onedeepath.balanccapp.domain.model.Category
import com.onedeepath.balanccapp.ui.presentation.model.TabItem
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.addbalance.model.AddBalanceUiState
import com.onedeepath.balanccapp.ui.screens.addbalance.viewmodel.AddBalanceViewModel
import java.time.LocalDate
import java.time.YearMonth


@Composable
fun AddIncomeOrExpenseScreen(
    viewModel: AddBalanceViewModel = hiltViewModel(),
    yearMonthViewModel: YearMonthViewModel) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val selectedYear:String by yearMonthViewModel.selectedYear.collectAsState()
    val selectedMonth:String by yearMonthViewModel.selectedMonthIndex.collectAsState()


    Column(
        Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface)
            .padding(16.dp)

    ) {
        HeaderAdd()

        IncomeExpenseTabview(
            isIncome = uiState.isIncome,
            onCheckedChange = viewModel::onTypeChange)

        AddAmountTF(
            amount = uiState.amount,
            onAmountChange = viewModel::onAmountChange
        )
        Spacer(Modifier.height(32.dp))

        AddCategorySelector(
            selectedCategory = uiState.category,
            onCategoryChange = viewModel::onCategoryChange)
        Spacer(Modifier.height(32.dp))

        DatePickerSelector(selectedYear.toInt(), selectedMonth) { date ->
            viewModel.onDateSelected(date.dayOfMonth.toString())
        }
        Spacer(Modifier.height(32.dp))

        DetailsTF(
            details = uiState.details,
            onDetailsChange = viewModel::onDetailsChange
        )
        Spacer(Modifier.height(32.dp))

        AddBalanceButton(
            uiState =  uiState,
            selectedYear =  selectedYear.toInt(),
            selectedMonth = selectedMonth ,
            onSave = viewModel::save,
            context =  context,)
    }
}

@Composable
fun AddBalanceButton(
    uiState: AddBalanceUiState,
    selectedYear: Int,
    selectedMonth: String,
    onSave: (year: String, month: String) -> Result<Unit>,
    context: Context,) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        enabled = uiState.isValid,
        onClick = {
            val result = onSave(selectedYear.toString(), selectedMonth)

            if (result.isSuccess) {
                Toast.makeText(context, "Balance Added", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(context, "Invalid Data", Toast.LENGTH_SHORT).show()
            }
        }
    ) {
        Text(text = stringResource(R.string.add),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HeaderAdd(){
    Spacer(Modifier.height(32.dp))
    Text(text = stringResource(R.string.add),
        fontSize = 55.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(16.dp))
}


@Composable
fun DetailsTF(details: String, onDetailsChange: (String) -> Unit) {

    OutlinedTextField(
        value = details,
        onValueChange = onDetailsChange,
        textStyle = TextStyle(
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        label = { Text(stringResource(R.string.details), color = MaterialTheme.colorScheme.onSurface) },
        placeholder = { Text(stringResource(R.string.enter_details), color = MaterialTheme.colorScheme.onSurface) },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp), // altura más grande para parecer un "text area"
        maxLines = 5,
        singleLine = false,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        )
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
        stringResource(R.string.january) -> 0
        stringResource(R.string.february) -> 1
        stringResource(R.string.march) -> 2
        stringResource(R.string.april) -> 3
        stringResource(R.string.may) -> 4
        stringResource(R.string.june) -> 5
        stringResource(R.string.july) -> 6
        stringResource(R.string.august) -> 7
        stringResource(R.string.september) -> 8
        stringResource(R.string.october) -> 9
        stringResource(R.string.november) -> 10
        stringResource(R.string.december) -> 11
        else -> 0
    }

    var calendarState = UseCaseState()

    val startDate = LocalDate.of(selectedYear, selectedMonthMapped + 1, 1)
    val endDate = YearMonth.of(selectedYear, selectedMonthMapped + 1).atEndOfMonth()

    var currentDay by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth().height(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
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
                },
            )

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary ,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = {
                    calendarState.show()
                }
            ) {
                Text(
                    text = currentDay.ifEmpty { stringResource(R.string.day) },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.weight(1f))
            Text(text = selectedMonth,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.weight(1f))
            Text("$selectedYear",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategorySelector(selectedCategory: Category, onCategoryChange: (Category) -> Unit) {

    var isExpanded by remember { mutableStateOf(false) }

    val categories = Category.entries

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(50)),
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
                shape = RoundedCornerShape(50),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = selectedCategory.color
                ),
                trailingIcon = {ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)},
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                    ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(selectedCategory.icon),
                        contentDescription = "",
                        tint = selectedCategory.color
                    )
                },
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = {isExpanded = false}
            ) { categories.forEach { category ->
                DropdownMenuItem(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surfaceVariant),
                    text = {
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.Bold,
                            color = category.color)
                           },
                    onClick = {
                        onCategoryChange(category)
                        isExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    leadingIcon =  { Icon(
                        painter = painterResource(id = category.icon),
                        contentDescription = "",
                        tint = category.color
                            )
                        },
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

    Spacer(Modifier.height(16.dp))

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        value = formattedAmount,
        singleLine = true,
        maxLines = 15,
        textStyle = TextStyle(fontSize = 35.sp,
            color = MaterialTheme.colorScheme.onSurface),
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
                text = stringResource(R.string.amount), fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface
            )
        },
        placeholder = {
            Text(text = stringResource(R.string.enter_amount), fontSize = 35.sp, color = MaterialTheme.colorScheme.onSurface)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        )
    )
}


@Composable
fun IncomeExpenseTabview(isIncome: Boolean, onCheckedChange: (Boolean) -> Unit) {

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    val tabItems = listOf(
        TabItem(stringResource(R.string.incomes_tab), Icons.Outlined.KeyboardArrowUp, Icons.Filled.KeyboardArrowUp),
        TabItem(stringResource(R.string.expenses_tab), Icons.Outlined.KeyboardArrowDown, Icons.Filled.KeyboardArrowDown)
    )

    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier
            .fillMaxWidth(),
        indicator = {},
        divider = {}
    ) {

        tabItems.forEachIndexed { index, item ->
            val selected = selectedTabIndex == index
            Tab(
                modifier = if (selected) Modifier
                    .clip(RoundedCornerShape(25))
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxSize()
                else Modifier
                    .clip(RoundedCornerShape(25))
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize(),
                selected = selected,     //index == selectedTabIndex
                onClick = {
                    selectedTabIndex = index
                    onCheckedChange(index == 0)
                },
                text = {Text(item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                },
            )
        }
    }
}


