package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.gamefest.ui.navigation.GameDetailDestination
import com.example.gamefest.ui.navigation.GamesByPublisherDestination
import com.example.gamefest.ui.navigation.TopLevelDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {

    // Backstack typé (plus safe)
    val backStack = remember { mutableStateListOf<Any>(TopLevelDestination.PUBLISHERS) }

    val currentDestination = backStack.lastOrNull()

    Scaffold(
        bottomBar = {
            if (currentDestination is TopLevelDestination) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    TopLevelDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination == destination,
                            onClick = {
                                backStack.clear()
                                backStack.add(destination)
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.contentDescription
                                )
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),
            entryProvider = { destination ->
                when (destination) {

                    TopLevelDestination.PUBLISHERS -> NavEntry(destination) {
                        PublisherScreen(
                            onPublisherClick = { publisherId, publisherName ->
                                backStack.add(
                                    GamesByPublisherDestination(
                                        publisherId,
                                        publisherName
                                    )
                                )
                            }
                        )
                    }

                    TopLevelDestination.GAMES -> NavEntry(destination) {
                        GameScreen(
                            onGameClick = { gameId ->
                                // Quand on cliquera sur un jeu, on ira vers l'écran de détails du jeu
                                backStack.add(GameDetailDestination(gameId))
                            }
                        )
                    }

                    is GamesByPublisherDestination -> NavEntry(destination) {
                        GamesByPublisherScreen(
                            publisherId = destination.publisherId,
                            publisherName = destination.publisherName,
                            onBackClick = {
                                if(backStack.size > 1) backStack.removeAt(backStack.lastIndex) // Permet de revenir en arrière avec la flèche
                            },
                            onGameClick = { gameId ->
                                // On navigue vers le détail du jeu si on clique dessus
                                backStack.add(GameDetailDestination(gameId))
                            }
                        )
                    }

                    is GameDetailDestination -> NavEntry(destination) {
                        Column {
                            Text("Détail du jeu n°${destination.gameId} ")
                            Button(
                                onClick = {
                                    if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                                }
                            ) {
                                Text("Retour")
                            }
                        }
                    }

                    else -> NavEntry(destination) {
                        Text("Écran introuvable")
                    }
                }
            }
        )
    }
}