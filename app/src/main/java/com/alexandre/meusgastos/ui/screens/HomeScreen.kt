package com.alexandre.meusgastos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexandre.meusgastos.ui.viewmodel.ExpenseViewModel
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(viewModel: ExpenseViewModel, onAddExpense: () -> Unit) {
    val expenses by viewModel.expenses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val totalToday by viewModel.totalToday.collectAsState()
    val totalThisMonth by viewModel.totalThisMonth.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(title = "Hoje", value = totalToday, modifier = Modifier.weight(1f))
            SummaryCard(title = "Este mês", value = totalThisMonth, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = onAddExpense, modifier = Modifier.fillMaxWidth()) {
            Text("+ Novo gasto")
        }

        Spacer(Modifier.height(16.dp))
        Text("Últimos gastos", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(expenses) { expense ->
                val categoryName = categories.find { it.id == expense.categoryId }?.name ?: "Sem categoria"
                ListItem(
                    headlineContent = { Text(expense.description) },
                    supportingContent = {
                        Text("$categoryName · ${expense.date.format(DateTimeFormatter.ofPattern("dd/MM"))}")
                    },
                    trailingContent = { Text("R$ %.2f".format(expense.amount)) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: Double, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text("R$ %.2f".format(value), style = MaterialTheme.typography.headlineSmall)
        }
    }
}
