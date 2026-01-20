package com.onedeepath.balanccapp.ui.navigation

sealed class AppScreens(val route: String) {
    object SplashScreens: AppScreens("splash_screen")
    object MainScreen: AppScreens("main_screen")
    object DetailScreen: AppScreens("detail_screen")
    object AddIncomeOrExpenseScreen: AppScreens("add_income_or_expense_screen")
    object SettingsScreen: AppScreens("settings_screen")
}