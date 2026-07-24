package com.alexandre.meusgastos.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandre.meusgastos.data.local.entity.CategoryEntity
import com.alexandre.meusgastos.data.local.entity.ExpenseEntity
import com.alexandre.meusgastos.data.repository.ExpenseRepository
import com.alexandre.meusgastos.ocr.ReceiptScanner
import com.alexandre.meusgastos.ocr.ReceiptScanResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val receiptScanner: ReceiptScanner
) : ViewModel() {

    // --- Estado para a tela inicial ---
    val expenses = repository.observeExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = repository.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalToday = repository.observeTotalToday()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalThisMonth = repository.observeTotalThisMonth()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- Estado do fluxo de leitura de cupom ---
    private val _scanResult = MutableStateFlow<ReceiptScanResult?>(null)
    val scanResult: StateFlow<ReceiptScanResult?> = _scanResult
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    fun scanReceipt(imageUri: Uri) {
        viewModelScope.launch {
            _isScanning.value = true
            _scanResult.value = receiptScanner.scan(imageUri)
            _isScanning.value = false
        }
    }

    fun clearScanResult() {
        _scanResult.value = null
    }

    // --- Ações de gasto ---
    fun saveExpense(
        description: String,
        amount: Double,
        date: LocalDate,
        categoryId: Long?,
        paymentMethod: String,
        notes: String?,
        receiptImagePath: String?,
        rawOcrText: String?
    ) {
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    description = description,
                    amount = amount,
                    date = date,
                    categoryId = categoryId,
                    paymentMethod = paymentMethod,
                    notes = notes,
                    receiptImagePath = receiptImagePath,
                    rawOcrText = rawOcrText
                )
            )
        }
    }

    fun updateExpense(expense: ExpenseEntity) = viewModelScope.launch { repository.updateExpense(expense) }
    fun deleteExpense(expense: ExpenseEntity) = viewModelScope.launch { repository.deleteExpense(expense) }

    // --- Ações de categoria ---
    fun saveCategory(category: CategoryEntity) = viewModelScope.launch { repository.saveCategory(category) }
    fun deleteCategory(category: CategoryEntity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(repository.deleteCategory(category)) }
    }

    // --- Relatórios ---
    fun categoryTotals(start: LocalDate, end: LocalDate) =
        repository.observeTotalsByCategory(start, end)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun dailyTotals(start: LocalDate, end: LocalDate) =
        repository.observeDailyTotals(start, end)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
