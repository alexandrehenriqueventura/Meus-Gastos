package com.alexandre.meusgastos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,           // nome do estabelecimento ou descrição livre
    val amount: Double,                 // valor total do gasto
    val date: LocalDate,
    val categoryId: Long?,
    val paymentMethod: String = "Não informado", // Dinheiro, Débito, Crédito, Pix...
    val notes: String? = null,
    val receiptImagePath: String? = null, // caminho da foto do cupom/nota, se houver
    val rawOcrText: String? = null,       // texto bruto extraído pelo OCR, guardado para conferência/reprocessamento
    val itemsJson: String? = null,        // itens individuais do cupom, serializados em JSON (opcional)
    val createdAt: Long = System.currentTimeMillis()
)
