package com.alexandre.meusgastos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexandre.meusgastos.ui.components.ExpenseCard
import com.alexandre.meusgastos.ui.viewmodel.ExpenseViewModel

/**
 * Versão modernizada da HomeScreen com ícones contextualizados e layout aprimorado.
 * Exibe resumo diário/mensal e lista de gastos com visual atualizado.
 */
@Composable
fun HomeScreenModernized(viewModel: ExpenseViewModel, onAddExpense: () -> Unit) {
    val expenses by viewModel.expenses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val totalToday by viewModel.totalToday.collectAsState()
    val totalThisMonth by viewModel.totalThisMonth.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header com título
        Text(
            "Meus Gastos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Cards de resumo
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SummaryCardModernized(
                title = "Hoje",
                value = totalToday,
                modifier = Modifier.weight(1f)
            )
            SummaryCardModernized(
                title = "Este mês",
                value = totalThisMonth,
                modifier = Modifier.weight(1f)
            )
        }

        // Botão flutuante
        FloatingActionButton(
            onClick = onAddExpense,
            modifier = Modifier.align(Alignment.End),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Novo gasto")
        }

        // Lista de gastos
        Text(
            "Últimos gastos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(expenses) { expense ->
                val category = categories.find { it.id == expense.categoryId }
                ExpenseCard(
                    expense = expense,
                    category = category
                )
            }
        }
    }
}

@Composable
private fun SummaryCardModernized(
    title: String,
    value: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "R$ ${String.format("%.2f", value)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
