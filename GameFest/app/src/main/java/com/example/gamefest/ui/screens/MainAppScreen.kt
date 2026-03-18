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
                                backStack.add(GameDetailDestination(gameId))
                            }
                        )
                    }

                    TopLevelDestination.FESTIVALS -> NavEntry(destination) {
                        FestivalScreen(
                            onFestivalClick = { festivalId ->
                                // Optional: add detail screen for festival if needed later
                            }
                        )
                    }

                    is GamesByPublisherDestination -> NavEntry(destination) {
                        GamesByPublisherScreen(
                            publisherId = destination.publisherId,
                            publisherName = destination.publisherName,
                            onBackClick = {
                                if(backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                            },
                            onGameClick = { gameId ->
                                backStack.add(GameDetailDestination(gameId))
                            }
                        )
                    }

                    is GameDetailDestination -> NavEntry(destination) {
                        GameDetailScreen(
                            gameId = destination.gameId,
                            onBackClick = {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        )
                    }

                    else -> NavEntry(destination) {
                        Text("Écran introuvable")
                    }
                }
            }
        )
    }
}
