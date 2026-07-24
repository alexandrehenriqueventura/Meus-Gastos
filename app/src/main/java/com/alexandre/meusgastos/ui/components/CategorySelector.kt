package com.alexandre.meusgastos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexandre.meusgastos.data.local.entity.CategoryEntity

/**
 * Grid de seleção de categorias com visualização em 3 colunas.
 * Componente reutilizável para adicionar gastos, editar orçamentos, etc.
 */
@Composable
fun CategorySelector(
    categories: List<CategoryEntity>,
    selectedCategory: CategoryEntity?,
    onCategorySelected: (CategoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories.size) { index ->
            CategoryButton(
                category = categories[index],
                isSelected = categories[index].id == selectedCategory?.id,
                onClick = { onCategorySelected(categories[index]) }
            )
        }
    }
}
