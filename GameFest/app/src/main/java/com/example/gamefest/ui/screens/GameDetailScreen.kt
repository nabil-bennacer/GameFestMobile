package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.ui.viewmodels.GameDetailViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameId: Int,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    viewModel: GameDetailViewModel = viewModel(factory = GameDetailViewModel.Factory)
) {
    val game by viewModel.getGame(gameId).collectAsState(initial = null)
    var deleteConfirmationRequired by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(game?.name ?: "Chargement...") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Modifier")
                    }
                    IconButton(onClick = { deleteConfirmationRequired = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
            if (deleteConfirmationRequired) {
                DeleteConfirmationDialog(
                    onDeleteCancel = {
                        // Si on annule, on cache juste la boîte
                        deleteConfirmationRequired = false
                    },
                    onDeleteConfirm = {
                        deleteConfirmationRequired = false
                        // 1. On supprime via le ViewModel
                        viewModel.deleteItem(gameId)
                        // 2. On retourne à l'écran précédent !
                        onBackClick()
                    }
                )
            }
        }
    ) { paddingValues ->

        game?.let { currentGame ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Type de jeu : ${currentGame.type}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("👥 Nombre de joueurs max :${currentGame.maxPlayers}")
                        Text("🎂 Âge minimum : ${currentGame.minAge} ans et +")
                    }
                }

            }
        } ?: run {
            // Pendant les quelques millisecondes où "game" est null, on affiche un rond de chargement
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /* Ne rien faire si on clique à côté */ },
        title = { Text("Attention") },
        text = { Text("Êtes-vous sûr de vouloir supprimer cet élément ? Cette action est irréversible.") },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text("Annuler")
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text("Supprimer")
            }
        }
    )
}