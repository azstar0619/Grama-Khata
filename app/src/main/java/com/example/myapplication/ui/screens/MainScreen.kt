package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.myapplication.data.*
import com.example.myapplication.ui.MainViewModel

private val TOP_LEVEL_ROUTES = setOf("home", "customers", "dues", "reports")

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val language by viewModel.language.collectAsState()
    val strings = if (language == Language.ENGLISH) EnglishStrings else KannadaStrings

    CompositionLocalProvider(LocalStrings provides strings) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val showBottomBar = currentRoute in TOP_LEVEL_ROUTES

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        listOf(
                            Triple("home", Icons.Default.Home, strings.home),
                            Triple("customers", Icons.Default.People, strings.customers),
                            Triple("dues", Icons.Default.Notifications, strings.dues),
                            Triple("reports", Icons.Default.BarChart, strings.reports),
                        ).forEach { (route, icon, label) ->
                            NavigationBarItem(
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) },
                                selected = currentRoute == route,
                                onClick = {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    DashboardScreen(navController, viewModel)
                }
                composable("customers") {
                    CustomerListScreen(navController, viewModel)
                }
                composable(
                    route = "customer/{customerId}",
                    arguments = listOf(navArgument("customerId") { type = NavType.StringType })
                ) { back ->
                    val customerId = back.arguments?.getString("customerId") ?: ""
                    CustomerDetailScreen(navController, viewModel, customerId)
                }
                composable("add_customer") {
                    AddCustomerScreen(navController, viewModel)
                }
                composable(
                    route = "add_transaction/{customerId}/{txType}",
                    arguments = listOf(
                        navArgument("customerId") { type = NavType.StringType },
                        navArgument("txType") { type = NavType.StringType }
                    )
                ) { back ->
                    val customerId = back.arguments?.getString("customerId") ?: ""
                    val txType = back.arguments?.getString("txType") ?: "CREDIT"
                    AddTransactionScreen(navController, viewModel, customerId, txType)
                }
                composable("dues") {
                    DuesScreen(navController, viewModel)
                }
                composable("reports") {
                    ReportsScreen(navController, viewModel)
                }
                composable("settings") {
                    SettingsScreen(navController, viewModel)
                }
            }
        }
    }
}
