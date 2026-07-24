package com.alexandre.meusgastos.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.overflow.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexandre.meusgastos.data.local.entity.CategoryEntity
import com.alexandre.meusgastos.ui.theme.CategoryColors
import com.alexandre.meusgastos.ui.theme.CategoryIcons

/**
 * Botão interativo para seleção de categoria.
 * Exibe ícone, nome e feedback visual de seleção.
 */
@Composable
fun CategoryButton(
    category: CategoryEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorHex = category.colorHex
    val backgroundColor = Color(AndroidColor.parseColor(colorHex))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(12.dp),
            color = backgroundColor.copy(alpha = if (isSelected) 0.25f else 0.15f),
            border = if (isSelected)
                BorderStroke(2.dp, backgroundColor)
            else
                null
        ) {
            Icon(
                imageVector = CategoryIcons.getIcon(category.icon),
                contentDescription = category.name,
                modifier = Modifier.padding(8.dp),
                tint = backgroundColor
            )
        }

        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
