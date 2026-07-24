package com.alexandre.meusgastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alexandre.meusgastos.data.local.AppDatabase
import com.alexandre.meusgastos.data.repository.ExpenseRepository
import com.alexandre.meusgastos.ocr.ReceiptScanner
import com.alexandre.meusgastos.ui.screens.*
import com.alexandre.meusgastos.ui.theme.MeusGastosTheme
import com.alexandre.meusgastos.ui.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val repository = ExpenseRepository(db.expenseDao(), db.categoryDao())
        val scanner = ReceiptScanner(applicationContext)

        setContent {
            MeusGastosTheme {
                val viewModel: ExpenseViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
                            ExpenseViewModel(repository, scanner) as T
                    }
                )
                AppScaffold(viewModel)
            }
        }
    }
}

private data class NavItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
private fun AppScaffold(viewModel: ExpenseViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        NavItem("home", "Início", Icons.Default.Home),
        NavItem("reports", "Relatórios", Icons.Default.BarChart),
        NavItem("categories", "Categorias", Icons.Default.Category),
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(viewModel, onAddExpense = { navController.navigate("add_expense") })
            }
            composable("add_expense") {
                AddExpenseScreen(viewModel, onSaved = { navController.popBackStack() })
            }
            composable("reports") { ReportsScreen(viewModel) }
            composable("categories") { CategoriesScreen(viewModel) }
        }
    }
}
