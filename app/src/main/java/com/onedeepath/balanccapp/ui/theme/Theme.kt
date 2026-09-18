package com.onedeepath.balanccapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = TextPrimaryLight,
    secondary = TextSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = SurfaceSecondaryLight,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = WarningLight,
    onTertiary = Color.White,
    tertiaryContainer = WarningContainerLight,
    onTertiaryContainer = TextPrimaryLight,
    error = ExpenseLight,
    onError = Color.White,
    errorContainer = ExpenseContainerLight,
    onErrorContainer = TextPrimaryLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceSecondaryLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = TextTertiaryLight,
    outlineVariant = BorderLight,
)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = BackgroundDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = TextSecondaryDark,
    onSecondary = BackgroundDark,
    secondaryContainer = SurfaceSecondaryDark,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = WarningDark,
    onTertiary = BackgroundDark,
    tertiaryContainer = WarningContainerDark,
    onTertiaryContainer = TextPrimaryDark,
    error = ExpenseDark,
    onError = BackgroundDark,
    errorContainer = ExpenseContainerDark,
    onErrorContainer = TextPrimaryDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceSecondaryDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = TextTertiaryDark,
    outlineVariant = BorderDark,
)

@Immutable
data class FinancialColorScheme(
    val income: Color,
    val onIncome: Color,
    val incomeContainer: Color,
    val onIncomeContainer: Color,
    val expense: Color,
    val onExpense: Color,
    val expenseContainer: Color,
    val onExpenseContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textDisabled: Color,
    val border: Color,
)

val LightFinancialColorScheme = FinancialColorScheme(
    income = IncomeLight,
    onIncome = Color.White,
    incomeContainer = IncomeContainerLight,
    onIncomeContainer = TextPrimaryLight,
    expense = ExpenseLight,
    onExpense = Color.White,
    expenseContainer = ExpenseContainerLight,
    onExpenseContainer = TextPrimaryLight,
    warning = WarningLight,
    warningContainer = WarningContainerLight,
    textSecondary = TextSecondaryLight,
    textTertiary = TextTertiaryLight,
    textDisabled = TextDisabledLight,
    border = BorderLight,
)

val DarkFinancialColorScheme = FinancialColorScheme(
    income = IncomeDark,
    onIncome = BackgroundDark,
    incomeContainer = IncomeContainerDark,
    onIncomeContainer = TextPrimaryDark,
    expense = ExpenseDark,
    onExpense = BackgroundDark,
    expenseContainer = ExpenseContainerDark,
    onExpenseContainer = TextPrimaryDark,
    warning = WarningDark,
    warningContainer = WarningContainerDark,
    textSecondary = TextSecondaryDark,
    textTertiary = TextTertiaryDark,
    textDisabled = TextDisabledDark,
    border = BorderDark,
)

val LocalFinancialColors = staticCompositionLocalOf { LightFinancialColorScheme }

val MaterialTheme.financialColors: FinancialColorScheme
    @Composable get() = LocalFinancialColors.current
