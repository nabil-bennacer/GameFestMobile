package com.example.gamefest.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Event
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable


enum class TopLevelDestination(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    FESTIVALS("Festivals", Icons.Default.Event, "Liste des festivals"),
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

@Serializable
object AdminUsersDestination

@Serializable
object PublisherEntryDestination

@Serializable
data class PublisherEditDestination(val publisherId: Int)

@Serializable
data class GameEntryDestination(val preselectedPublisherId: Int? = null)

@Serializable
data class GameEditDestination(val gameId: Int)