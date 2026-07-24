package com.alexandre.meusgastos.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexandre.meusgastos.ocr.ScanConfidence
import com.alexandre.meusgastos.ui.viewmodel.ExpenseViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Fluxo: usuário fotografa o cupom -> ML Kit extrai texto -> campos vêm pré-preenchidos
 * (valor, data, estabelecimento) -> usuário confere/corrige -> escolhe categoria -> salva.
 * Os campos SEMPRE ficam editáveis: OCR de cupom térmico erra com frequência (papel desbotado,
 * fonte pequena), então a conferência manual é parte do fluxo, não um extra.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(viewModel: ExpenseViewModel, onSaved: () -> Unit) {
    val categories by viewModel.categories.collectAsState()
    val scanResult by viewModel.scanResult.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var paymentMethod by remember { mutableStateOf("Pix") }
    var receiptPath by remember { mutableStateOf<String?>(null) }
    var rawOcrText by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success -> if (success) receiptPath?.let { viewModel.scanReceipt(Uri.parse(it)) } }

    // Quando o OCR retorna um resultado, pré-preenche os campos (sem sobrescrever o que o usuário já digitou)
    LaunchedEffect(scanResult) {
        scanResult?.let { result ->
            if (description.isBlank()) result.merchantGuess?.let { description = it }
            if (amountText.isBlank()) result.totalGuess?.let { amountText = "%.2f".format(it) }
            result.dateGuess?.let { date = it }
            rawOcrText = result.rawText
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Novo gasto", style = MaterialTheme.typography.headlineSmall)

        OutlinedButton(
            onClick = { /* cria arquivo temporário e aciona cameraLauncher.launch(uri) */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Fotografar cupom / nota fiscal")
        }

        if (isScanning) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

        scanResult?.let { result ->
            val (label, color) = when (result.confidence) {
                ScanConfidence.HIGH -> "Leitura completa — confira os valores" to MaterialTheme.colorScheme.primary
                ScanConfidence.PARTIAL -> "Leitura parcial — complete os campos faltantes" to MaterialTheme.colorScheme.tertiary
                ScanConfidence.LOW -> "Não foi possível ler o cupom — preencha manualmente" to MaterialTheme.colorScheme.error
            }
            AssistChip(onClick = {}, label = { Text(label) }, colors = AssistChipDefaults.assistChipColors(labelColor = color))
        }

        OutlinedTextField(
            value = description, onValueChange = { description = it },
            label = { Text("Estabelecimento / descrição") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = amountText, onValueChange = { amountText = it },
            label = { Text("Valor (R$)") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            onValueChange = {}, readOnly = true,
            label = { Text("Data") }, modifier = Modifier.fillMaxWidth()
            // TODO: abrir DatePickerDialog ao clicar
        )

        Text("Categoria", style = MaterialTheme.typography.labelLarge)
        FlowRowCategories(categories, selectedCategoryId) { selectedCategoryId = it }

        Button(
            onClick = {
                val amount = amountText.replace(",", ".").toDoubleOrNull() ?: return@Button
                viewModel.saveExpense(
                    description = description.ifBlank { "Gasto sem descrição" },
                    amount = amount,
                    date = date,
                    categoryId = selectedCategoryId,
                    paymentMethod = paymentMethod,
                    notes = null,
                    receiptImagePath = receiptPath,
                    rawOcrText = rawOcrText
                )
                viewModel.clearScanResult()
                onSaved()
            },
            enabled = amountText.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Salvar gasto") }
    }
}

@Composable
private fun FlowRowCategories(
    categories: List<com.alexandre.meusgastos.data.local.entity.CategoryEntity>,
    selectedId: Long?,
    onSelect: (Long) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        categories.take(4).forEach { category ->
            FilterChip(
                selected = selectedId == category.id,
                onClick = { onSelect(category.id) },
                label = { Text(category.name) }
            )
        }
    }
    // Nota: em produção, trocar por um FlowRow real (com.google.accompanist ou androidx.compose.foundation.layout.FlowRow
    // no Compose 1.7+) para não cortar categorias além da 4ª — mantido simples aqui de propósito.
}
