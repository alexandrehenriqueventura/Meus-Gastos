package com.alexandre.meusgastos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Categoria de gasto criada/editada pelo usuário.
 * `colorHex` e `icon` alimentam a UI (chips e legendas dos gráficos).
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String = "#4C6EF5",
    val icon: String = "shopping_cart",
    val monthlyBudget: Double? = null, // opcional: limite mensal para alertas
    val isDefault: Boolean = false      // categorias padrão (Alimentação, Transporte...) não podem ser excluídas, só renomeadas
)
