package com.alexandre.meusgastos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alexandre.meusgastos.data.local.dao.CategoryDao
import com.alexandre.meusgastos.data.local.dao.ExpenseDao
import com.alexandre.meusgastos.data.local.entity.CategoryEntity
import com.alexandre.meusgastos.data.local.entity.ExpenseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [ExpenseEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // Categorias sugeridas na primeira execução — o usuário pode renomear, remover (exceto as padrão) ou criar outras livremente
        private val defaultCategories = listOf(
            CategoryEntity(name = "Alimentação", colorHex = "#F76707", icon = "restaurant", isDefault = true),
            CategoryEntity(name = "Transporte", colorHex = "#1971C2", icon = "directions_car", isDefault = true),
            CategoryEntity(name = "Moradia", colorHex = "#5F3DC4", icon = "home", isDefault = true),
            CategoryEntity(name = "Saúde", colorHex = "#E03131", icon = "local_hospital", isDefault = true),
            CategoryEntity(name = "Educação", colorHex = "#2F9E44", icon = "school", isDefault = true),
            CategoryEntity(name = "Lazer", colorHex = "#F08C00", icon = "sports_esports", isDefault = true),
            CategoryEntity(name = "Compras", colorHex = "#AE3EC9", icon = "shopping_bag", isDefault = true),
            CategoryEntity(name = "Outros", colorHex = "#495057", icon = "category", isDefault = true),
        )

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "meus_gastos.db")
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.categoryDao()?.let { dao ->
                                    defaultCategories.forEach { dao.upsert(it) }
                                }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
