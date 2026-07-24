package com.alexandre.meusgastos.data.local.dao

import androidx.room.*
import com.alexandre.meusgastos.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class CategoryTotal(val categoryId: Long?, val categoryName: String?, val total: Double)
data class DailyTotal(val date: LocalDate, val total: Double)

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun observeBetween(start: LocalDate, end: LocalDate): Flow<List<ExpenseEntity>>

    @Insert
    suspend fun insert(expense: ExpenseEntity): Long

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    // Total gasto no dia — usado no cartão "Hoje" da tela inicial
    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date = :day")
    fun observeTotalForDay(day: LocalDate): Flow<Double>

    // Total do mês corrente
    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date BETWEEN :start AND :end")
    fun observeTotalBetween(start: LocalDate, end: LocalDate): Flow<Double>

    // Soma por categoria no período — alimenta o gráfico de pizza
    @Query(
        """
        SELECT c.id AS categoryId, c.name AS categoryName, COALESCE(SUM(e.amount), 0) AS total
        FROM categories c
        LEFT JOIN expenses e ON e.categoryId = c.id AND e.date BETWEEN :start AND :end
        GROUP BY c.id
        ORDER BY total DESC
        """
    )
    fun observeTotalsByCategory(start: LocalDate, end: LocalDate): Flow<List<CategoryTotal>>

    // Soma por dia no período — alimenta o gráfico de linha/barras de evolução diária
    @Query(
        """
        SELECT date, SUM(amount) AS total
        FROM expenses
        WHERE date BETWEEN :start AND :end
        GROUP BY date
        ORDER BY date ASC
        """
    )
    fun observeDailyTotals(start: LocalDate, end: LocalDate): Flow<List<DailyTotal>>

    // Média diária de gasto no período — usada no card de "média por dia"
    @Query(
        """
        SELECT COALESCE(SUM(amount), 0) * 1.0 /
               (julianday(:end) - julianday(:start) + 1)
        FROM expenses WHERE date BETWEEN :start AND :end
        """
    )
    suspend fun averagePerDay(start: LocalDate, end: LocalDate): Double
}
