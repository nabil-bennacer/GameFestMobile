package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.gamefest.ui.navigation.*
import com.example.gamefest.ui.viewmodels.AuthViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val isCheckingAuth by authViewModel.isCheckingAuth

    // Backstack initialisé avec PUBLISHERS
    val backStack = remember { mutableStateListOf<Any>(TopLevelDestination.PUBLISHERS) }

    // Cet effet gère la redirection automatique basée sur l'état d'authentification
    // On n'agit QUE quand isCheckingAuth est false (getProfile() a terminé)
    LaunchedEffect(isCheckingAuth, currentUser) {
        if (!isCheckingAuth) {
            if (currentUser == null &&
                backStack.lastOrNull() !is LoginDestination &&
                backStack.lastOrNull() !is RegisterDestination
            ) {
                backStack.clear()
                backStack.add(LoginDestination)
            }
        }
    }

    // Pendant la vérification initiale du cookie, on affiche un loader
    if (isCheckingAuth) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentDestination = backStack.lastOrNull()

    Scaffold(
        bottomBar = {
            if (currentDestination is TopLevelDestination && currentUser != null) {
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
        },
        floatingActionButton = {
            if (currentDestination == TopLevelDestination.PUBLISHERS) {
                FloatingActionButton(onClick = {
                    backStack.add(PublisherEntryDestination)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter un éditeur")
                }
            }
            else if (currentDestination == TopLevelDestination.GAMES) {
                FloatingActionButton(onClick = { backStack.add(GameEntryDestination()) }) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter un jeu")
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),
            entryProvider = { destination ->
                when (destination) {
                    TopLevelDestination.FESTIVALS -> NavEntry(destination) {
                        FestivalScreen(
                            onFestivalClick = { festivalId ->
                                // Optional: add detail screen for festival if needed later
                            }
                        )
                    }
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

                    TopLevelDestination.PROFILE -> NavEntry(destination) {
                        if (currentUser == null) {
                            LoginScreen(
                                viewModel = authViewModel,
                                onRegisterClick = { backStack.add(RegisterDestination) },
                                onLoginSuccess = {
                                    backStack.clear()
                                    backStack.add(TopLevelDestination.PUBLISHERS)
                                }
                            )
                        } else {
                            ProfileScreen(
                                viewModel = authViewModel,
                                onLogout = {
                                    backStack.clear()
                                    backStack.add(LoginDestination)
                                },
                                onNavigateToAdmin = {
                                    backStack.add(AdminUsersDestination)
                                }
                            )
                        }
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
                            },
                            onEditClick = {
                                backStack.add(PublisherEditDestination(destination.publisherId))
                            },
                            onAddGameClick = {
                                backStack.add(GameEntryDestination(preselectedPublisherId = destination.publisherId))
                            }
                        )
                    }

                    is GameDetailDestination -> NavEntry(destination) {
                        GameDetailScreen(
                            gameId = destination.gameId,
                            onBackClick = {
                                backStack.removeAt(backStack.lastIndex)
                            },
                            onEditClick = {
                                backStack.add(GameEditDestination(destination.gameId))
                            }
                        )
                    }

                    LoginDestination -> NavEntry(destination) {
                        LoginScreen(
                            viewModel = authViewModel,
                            onRegisterClick = { backStack.add(RegisterDestination) },
                            onLoginSuccess = {
                                backStack.clear()
                                backStack.add(TopLevelDestination.PUBLISHERS)
                            }
                        )
                    }

                    RegisterDestination -> NavEntry(destination) {
                        RegisterScreen(
                            viewModel = authViewModel,
                            onLoginClick = { backStack.removeAt(backStack.lastIndex) },
                            onRegisterSuccess = {
                                backStack.clear()
                                backStack.add(TopLevelDestination.PUBLISHERS)
                            }
                        )
                    }

                    AdminUsersDestination -> NavEntry(destination) {
                        AdminUsersScreen(
                            onBackClick = {
                                if(backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                            }
                        )
                    }

                    PublisherEntryDestination -> NavEntry(destination) {
                        PublisherEntryScreen(
                            onNavigateUp = { backStack.removeAt(backStack.lastIndex) }
                        )
                    }

                    is PublisherEditDestination -> NavEntry(destination) {
                        PublisherEditScreen(
                            publisherId = destination.publisherId,
                            onNavigateUp = { backStack.removeAt(backStack.lastIndex) }
                        )
                    }

                    is GameEntryDestination -> NavEntry(destination) {
                        GameEntryScreen(
                            preselectedPublisherId = destination.preselectedPublisherId,
                            onNavigateUp = { backStack.removeAt(backStack.lastIndex) }
                        )
                    }

                    is GameEditDestination -> NavEntry(destination) {
                        GameEditScreen(
                            gameId = destination.gameId,
                            onNavigateUp = { backStack.removeAt(backStack.lastIndex) }
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
