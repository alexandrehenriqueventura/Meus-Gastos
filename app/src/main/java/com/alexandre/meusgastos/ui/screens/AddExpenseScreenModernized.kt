package com.alexandre.meusgastos.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexandre.meusgastos.ocr.ScanConfidence
import com.alexandre.meusgastos.ui.components.CategorySelector
import com.alexandre.meusgastos.ui.viewmodel.ExpenseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Versão modernizada de AddExpenseScreen com seleção visual de categorias.
 * Inclui grid interativo de categorias com ícones contextualizados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreenModernized(viewModel: ExpenseViewModel, onSaved: () -> Unit) {
    val categories by viewModel.categories.collectAsState()
    val scanResult by viewModel.scanResult.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var selectedCategory by remember { mutableStateOf<com.alexandre.meusgastos.data.local.entity.CategoryEntity?>(null) }
    var paymentMethod by remember { mutableStateOf("Pix") }
    var receiptPath by remember { mutableStateOf<String?>(null) }
    var rawOcrText by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success -> if (success) receiptPath?.let { viewModel.scanReceipt(Uri.parse(it)) } }

    LaunchedEffect(scanResult) {
        scanResult?.let { result ->
            if (description.isBlank()) result.merchantGuess?.let { description = it }
            if (amountText.isBlank()) result.totalGuess?.let { amountText = "%.2f".format(it) }
            result.dateGuess?.let { date = it }
            rawOcrText = result.rawText
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Novo Gasto",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Botão de câmera aprimorado
        OutlinedButton(
            onClick = { /* TODO: implementar captura real */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Fotografar cupom / nota fiscal")
        }

        if (isScanning) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

        scanResult?.let { result ->
            val (label, color) = when (result.confidence) {
                ScanConfidence.HIGH -> "Leitura completa ✓" to MaterialTheme.colorScheme.primary
                ScanConfidence.PARTIAL -> "Leitura parcial — complete os campos" to MaterialTheme.colorScheme.tertiary
                ScanConfidence.LOW -> "Confira os valores com atenção" to MaterialTheme.colorScheme.error
            }
            AssistChip(
                onClick = {},
                label = { Text(label) },
                colors = AssistChipDefaults.assistChipColors(labelColor = color)
            )
        }

        // Campos de entrada
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Estabelecimento / descrição") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Valor (R$)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onValueChange = {},
            readOnly = true,
            label = { Text("Data") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        // Seletor de categoria moderno
        Text(
            "Categoria",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        CategorySelector(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        // Botão de salvar
        Button(
            onClick = {
                val amount = amountText.replace(",", ".").toDoubleOrNull() ?: return@Button
                viewModel.saveExpense(
                    description = description.ifBlank { "Gasto sem descrição" },
                    amount = amount,
                    date = date,
                    categoryId = selectedCategory?.id,
                    paymentMethod = paymentMethod,
                    notes = null,
                    receiptImagePath = receiptPath,
                    rawOcrText = rawOcrText
                )
                viewModel.clearScanResult()
                onSaved()
            },
            enabled = amountText.isNotBlank() && selectedCategory != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Salvar Gasto", fontWeight = FontWeight.Bold)
        }
    }
}
