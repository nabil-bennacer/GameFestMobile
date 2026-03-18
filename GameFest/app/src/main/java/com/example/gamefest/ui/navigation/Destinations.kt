package com.example.gamefest.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Event
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable


enum class TopLevelDestination(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    PUBLISHERS("Éditeurs", Icons.Default.Business, "Liste des éditeurs"),
    GAMES("Tous les Jeux", Icons.Default.Casino, "Liste de tous les jeux"),
    FESTIVALS("Festivals", Icons.Default.Event, "Liste des festivals")
}

@Serializable
data class GamesByPublisherDestination(val publisherId: Int, val publisherName: String)

@Serializable
data class GameDetailDestination(val gameId: Int)
