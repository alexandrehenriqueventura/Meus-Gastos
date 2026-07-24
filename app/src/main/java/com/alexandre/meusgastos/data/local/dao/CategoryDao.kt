package com.alexandre.meusgastos.data.local.dao

import androidx.room.*
import com.alexandre.meusgastos.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(category: CategoryEntity): Long

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("SELECT COUNT(*) FROM expenses WHERE categoryId = :categoryId")
    suspend fun expenseCountForCategory(categoryId: Long): Int

    @Query("SELECT * FROM categories WHERE isDefault = 1")
    suspend fun getDefaults(): List<CategoryEntity>
}
