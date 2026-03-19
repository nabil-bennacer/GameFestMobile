package com.example.gamefest.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable


enum class TopLevelDestination(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    PUBLISHERS("Éditeurs", Icons.Default.Business, "Liste des éditeurs"),
    GAMES("Tous les Jeux", Icons.Default.Casino, "Liste de tous les jeux"),
    PROFILE("Profil", Icons.Default.Person, "Mon Profil")
}

@Serializable
data class GamesByPublisherDestination(val publisherId: Int, val publisherName: String)

@Serializable
data class GameDetailDestination(val gameId: Int)

@Serializable
object LoginDestination

@Serializable
object RegisterDestination
