package com.alexandre.meusgastos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexandre.meusgastos.data.local.entity.CategoryEntity
import com.alexandre.meusgastos.ui.viewmodel.ExpenseViewModel

@Composable
fun CategoriesScreen(viewModel: ExpenseViewModel) {
    val categories by viewModel.categories.collectAsState()
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editingCategory = null; showEditor = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nova categoria")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(categories) { category ->
                ListItem(
                    headlineContent = { Text(category.name) },
                    supportingContent = {
                        category.monthlyBudget?.let { Text("Orçamento mensal: R$ %.2f".format(it)) }
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { editingCategory = category; showEditor = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = {
                                viewModel.deleteCategory(category) { success ->
                                    if (!success) errorMessage = "Categorias padrão não podem ser excluídas — apenas renomeadas."
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir")
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }

    if (showEditor) {
        CategoryEditorDialog(
            initial = editingCategory,
            onDismiss = { showEditor = false },
            onSave = { category -> viewModel.saveCategory(category); showEditor = false }
        )
    }

    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            confirmButton = { TextButton(onClick = { errorMessage = null }) { Text("Ok") } },
            title = { Text("Não foi possível excluir") },
            text = { Text(message) }
        )
    }
}

@Composable
private fun CategoryEditorDialog(
    initial: CategoryEntity?,
    onDismiss: () -> Unit,
    onSave: (CategoryEntity) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var budgetText by remember { mutableStateOf(initial?.monthlyBudget?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nova categoria" else "Editar categoria") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
                OutlinedTextField(
                    value = budgetText, onValueChange = { budgetText = it },
                    label = { Text("Orçamento mensal (opcional)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    (initial ?: CategoryEntity(name = "")).copy(
                        name = name.ifBlank { "Sem nome" },
                        monthlyBudget = budgetText.toDoubleOrNull()
                    )
                )
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
