package com.onedeepath.balanccapp.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.onedeepath.balanccapp.data.datastore.SettingsPreferences
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.addbalance.AddIncomeOrExpenseScreen
import com.onedeepath.balanccapp.ui.screens.detail.MonthsDetailScreen
import com.onedeepath.balanccapp.ui.screens.main.MainScreen
import com.onedeepath.balanccapp.ui.screens.settings.SettingsScreen
import com.onedeepath.balanccapp.ui.screens.splash.SplashScreen


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavigation(
    settingsPreferences: SettingsPreferences,
    isDarkTheme: Boolean) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.SplashScreens.route
    ) {
        composable(AppScreens.SplashScreens.route) {
            SplashScreen(navController)
        }
        composable(AppScreens.MainScreen.route) { backStackEntry ->

            val parentEntry = remember{ backStackEntry }
            val yearMonthViewModel :  YearMonthViewModel = hiltViewModel(parentEntry)
            MainScreen(
                navController = navController, yearMonthViewModel = yearMonthViewModel)
        }
        composable(AppScreens.DetailScreen.route) {

            val parentEntry = remember {
                navController.getBackStackEntry(AppScreens.MainScreen.route)
            }
            val sharedViewModel: YearMonthViewModel = hiltViewModel(parentEntry)

            MonthsDetailScreen(
                navController = navController,
                yearMonthViewModel = sharedViewModel
            )
        }
        composable(AppScreens.AddIncomeOrExpenseScreen.route) {
            val parentEntry = remember {
                navController.getBackStackEntry(AppScreens.MainScreen.route)
            }
            val sharedViewModel: YearMonthViewModel = hiltViewModel(parentEntry)

            AddIncomeOrExpenseScreen(yearMonthViewModel = sharedViewModel)
        }
        composable(AppScreens.SettingsScreen.route) {

            SettingsScreen(navController = navController, settingsPreferences, isDarkTheme)

        }

    }

}