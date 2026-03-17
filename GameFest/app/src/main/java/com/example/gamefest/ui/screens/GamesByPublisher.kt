package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.repository.GameRepository
import com.example.gamefest.ui.components.GameCard

// 1. LE PETIT CERVEAU (qui filtre les jeux)
class GamesByPublisherViewModel(private val repository: GameRepository) : ViewModel() {
    // Il renvoie directement le Flow filtré depuis Room
    fun getGames(publisherId: Int) = repository.getGamesByPublisher(publisherId)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                GamesByPublisherViewModel(application.container.gameRepository)
            }
        }
    }
}

// 2. L'ÉCRAN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesByPublisherScreen(
    publisherId: Int,
    publisherName: String,
    onBackClick: () -> Unit,
    onGameClick: (Int) -> Unit,
    viewModel: GamesByPublisherViewModel = viewModel(factory = GamesByPublisherViewModel.Factory)
) {
    // On observe la liste filtrée
    val gamesList by viewModel.getGames(publisherId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jeux de $publisherName") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(gamesList) { game ->
                GameCard(
                    game = game,
                    onClick = { onGameClick(game.id) }
                )
            }
        }
    }
}