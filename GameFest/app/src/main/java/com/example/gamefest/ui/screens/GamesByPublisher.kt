package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.repository.GameRepository
import com.example.gamefest.ui.components.GameCard
import com.example.gamefest.data.repository.PublisherRepository
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

class GamesByPublisherViewModel(
    private val repository: GameRepository,
    private val publisherRepository: PublisherRepository
) : ViewModel() {
    // Il renvoie directement le Flow filtré depuis Room

    fun getGames(publisherId: Int) = repository.getGamesByPublisher(publisherId)

    fun deletePublisher(publisherId: Int) {
        viewModelScope.launch {
            publisherRepository.deletePublisher(publisherId)
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                GamesByPublisherViewModel(repository = application.container.gameRepository,
                publisherRepository = application.container.publisherRepository)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesByPublisherScreen(
    publisherId: Int,
    publisherName: String,
    onBackClick: () -> Unit,
    onGameClick: (Int) -> Unit,
    onEditClick: () -> Unit,
    onAddGameClick: () -> Unit,
    viewModel: GamesByPublisherViewModel = viewModel(factory = GamesByPublisherViewModel.Factory)
) {
    // On observe la liste filtrée
    val gamesList by viewModel.getGames(publisherId).collectAsState(initial = emptyList())

    var deleteConfirmationRequired by remember { mutableStateOf(false) }

    if (deleteConfirmationRequired) {
        DeletePublisherDialog(
            onDeleteConfirm = {
                deleteConfirmationRequired = false
                viewModel.deletePublisher(publisherId)
                onBackClick() // On retourne à la liste après suppression
            },
            onDeleteCancel = {
                deleteConfirmationRequired = false
            }
        )
    }

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
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier l'éditeur"
                        )
                    }
                    IconButton(onClick = { deleteConfirmationRequired = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer l'éditeur")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddGameClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter un jeu à cet éditeur")
            }
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

@Composable
private fun DeletePublisherDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Attention") },
        text = { Text("Êtes-vous sûr de vouloir supprimer cet éditeur ? Tous ses jeux associés seront également supprimés.") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) { Text("Annuler") }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) { Text("Supprimer") }
        }
    )
}