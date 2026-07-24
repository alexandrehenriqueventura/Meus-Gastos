package com.alexandre.meusgastos.data.repository

import com.alexandre.meusgastos.data.local.dao.CategoryDao
import com.alexandre.meusgastos.data.local.dao.CategoryTotal
import com.alexandre.meusgastos.data.local.dao.DailyTotal
import com.alexandre.meusgastos.data.local.dao.ExpenseDao
import com.alexandre.meusgastos.data.local.entity.CategoryEntity
import com.alexandre.meusgastos.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val categoryDao: CategoryDao
) {
    // Gastos
    fun observeExpenses(): Flow<List<ExpenseEntity>> = expenseDao.observeAll()
    fun observeExpensesBetween(start: LocalDate, end: LocalDate) = expenseDao.observeBetween(start, end)
    suspend fun addExpense(expense: ExpenseEntity) = expenseDao.insert(expense)
    suspend fun updateExpense(expense: ExpenseEntity) = expenseDao.update(expense)
    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.delete(expense)

    // Cálculos usados nos cartões de resumo e relatórios
    fun observeTotalToday(): Flow<Double> = expenseDao.observeTotalForDay(LocalDate.now())
    fun observeTotalThisMonth(): Flow<Double> {
        val today = LocalDate.now()
        return expenseDao.observeTotalBetween(today.withDayOfMonth(1), today.withDayOfMonth(today.lengthOfMonth()))
    }
    fun observeTotalsByCategory(start: LocalDate, end: LocalDate): Flow<List<CategoryTotal>> =
        expenseDao.observeTotalsByCategory(start, end)
    fun observeDailyTotals(start: LocalDate, end: LocalDate): Flow<List<DailyTotal>> =
        expenseDao.observeDailyTotals(start, end)
    suspend fun averagePerDay(start: LocalDate, end: LocalDate): Double =
        expenseDao.averagePerDay(start, end)

    // Categorias
    fun observeCategories(): Flow<List<CategoryEntity>> = categoryDao.observeAll()
    suspend fun saveCategory(category: CategoryEntity) = categoryDao.upsert(category)
    suspend fun deleteCategory(category: CategoryEntity): Boolean {
        if (category.isDefault) return false // categorias padrão só podem ser editadas, não removidas
        categoryDao.delete(category)
        return true
    }
}
