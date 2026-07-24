package com.alexandre.meusgastos.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Mapeamento de ícones Material Icons para cada categoria de gasto.
 * Cada categoria possui um ícone contextualizado que melhora a UX.
 */
object CategoryIcons {
    private val iconMap = mapOf(
        "restaurant" to Icons.Filled.Restaurant,      // Alimentação
        "directions_car" to Icons.Filled.DirectionsCar,  // Transporte
        "home" to Icons.Filled.Home,                   // Moradia
        "local_hospital" to Icons.Filled.LocalHospital,  // Saúde
        "movie_filter" to Icons.Filled.MovieFilter,    // Entretenimento
        "shopping_cart" to Icons.Filled.ShoppingCart,  // Utilidades/Compras
        "school" to Icons.Filled.School,               // Educação
        "entertainment" to Icons.Filled.TheaterComedy, // Diversão
        "utilities" to Icons.Filled.OfflinePin,        // Contas/Utilidades
        "sports" to Icons.Filled.FitnessCenter,        // Esportes
        "pets" to Icons.Filled.Pets,                   // Animais de estimação
        "more_horiz" to Icons.Filled.MoreHoriz         // Outros (padrão)
    )

    fun getIcon(iconName: String): ImageVector {
        return iconMap[iconName] ?: Icons.Filled.MoreHoriz
    }

    fun getAvailableIcons(): List<Pair<String, ImageVector>> {
        return iconMap.toList()
    }
}

/**
 * Paleta de cores predefinidas para cada categoria.
 * Facilita consistência visual e permite edição personalizada.
 */
object CategoryColors {
    private val colorMap = mapOf(
        "restaurant" to "#FF6200EE",        // Roxo profundo
        "directions_car" to "#FFFF6D00",    // Laranja vibrante
        "home" to "#FF03DAC6",              // Verde-água
        "local_hospital" to "#FFFF0000",    // Vermelho puro
        "movie_filter" to "#FFFB03D5",      // Rosa quente
        "shopping_cart" to "#FF00BCD4",     // Ciano
        "school" to "#FF4CAF50",            // Verde
        "entertainment" to "#FFFF9800",     // Âmbar
        "utilities" to "#FF2196F3",         // Azul
        "sports" to "#FFFF5722",            // Laranja-vermelho
        "pets" to "#FFBE7F00",              // Marrom
        "more_horiz" to "#FF9E9E9E"        // Cinza (padrão)
    )

    fun getColor(iconName: String): String {
        return colorMap[iconName] ?: "#FF9E9E9E"
    }

    fun getAvailableColors(): Map<String, String> {
        return colorMap
    }
}
