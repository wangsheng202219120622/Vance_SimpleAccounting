package com.deweiwang.bookkeeping.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.deweiwang.bookkeeping.expense.AddExpenseScreen
import com.deweiwang.bookkeeping.income.AddIncomeScreen
import com.deweiwang.bookkeeping.home.HomeScreen
import com.deweiwang.bookkeeping.search.SearchScreen
import com.deweiwang.bookkeeping.settings.SettingScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddExpense : Screen("add_expense")
    object AddIncome : Screen("add_income")
    object Search : Screen("search")
    object Settings : Screen("settings")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.AddExpense.route) {
            AddExpenseScreen(navController)
        }
        composable(Screen.AddIncome.route) {
            AddIncomeScreen(navController)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingScreen(navController)
        }
    }
}