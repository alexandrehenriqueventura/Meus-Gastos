package com.alexandre.meusgastos.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexandre.meusgastos.data.local.entity.CategoryEntity
import com.alexandre.meusgastos.data.local.entity.ExpenseEntity
import com.alexandre.meusgastos.ui.theme.CategoryColors
import com.alexandre.meusgastos.ui.theme.CategoryIcons
import java.time.format.DateTimeFormatter

/**
 * Card modernizado para exibir um gasto individual com ícone contextualizado.
 * O ícone e a cor mudam conforme a categoria, melhorando reconhecimento visual.
 */
@Composable
fun ExpenseCard(
    expense: ExpenseEntity,
    category: CategoryEntity?,
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val categoryName = category?.name ?: "Sem categoria"
    val iconName = category?.icon ?: "more_horiz"
    val colorHex = category?.colorHex ?: CategoryColors.getColor("more_horiz")
    val backgroundColor = Color(AndroidColor.parseColor(colorHex))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ícone em fundo colorido arredondado
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = backgroundColor.copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = CategoryIcons.getIcon(iconName),
                    contentDescription = categoryName,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp),
                    tint = backgroundColor
                )
            }

            // Informações do gasto
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.labelSmall,
                        color = backgroundColor
                    )
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = expense.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Valor com destaque
            Text(
                text = "R$ ${String.format("%.2f", expense.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
