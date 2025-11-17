package com.onedeepath.balanccapp.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.onedeepath.balanccapp.ui.presentation.viewmodel.BalanceViewModel
import com.onedeepath.balanccapp.ui.presentation.viewmodel.YearMonthViewModel
import com.onedeepath.balanccapp.ui.screens.AppScreens
import com.onedeepath.balanccapp.ui.screens.addbalance.AddIncomeOrExpenseScreen
import com.onedeepath.balanccapp.ui.screens.detail.MonthsDetailScreen
import com.onedeepath.balanccapp.ui.screens.main.MainScreen
import com.onedeepath.balanccapp.ui.screens.splash.SplashScreen


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavigation() {

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
            val sharedViewModel: YearMonthViewModel = hiltViewModel(parentEntry)
            val balanceViewModel : BalanceViewModel = hiltViewModel(parentEntry)

            MainScreen(
                navController = navController, yearMonthViewModel = sharedViewModel, balanceViewModel = balanceViewModel)
        }
        composable(AppScreens.DetailScreen.route) {

            val parentEntry = remember {
                navController.getBackStackEntry(AppScreens.MainScreen.route)
            }
            val sharedViewModel: YearMonthViewModel = hiltViewModel(parentEntry)

            MonthsDetailScreen(
                navController = navController, yearMonthViewModel = sharedViewModel)
        }
        composable(AppScreens.AddIncomeOrExpenseScreen.route) {
            val parentEntry = remember {
                navController.getBackStackEntry(AppScreens.MainScreen.route)
            }
            val sharedViewModel: YearMonthViewModel = hiltViewModel(parentEntry)

            AddIncomeOrExpenseScreen(yearMonthViewModel = sharedViewModel)
        }

    }

}