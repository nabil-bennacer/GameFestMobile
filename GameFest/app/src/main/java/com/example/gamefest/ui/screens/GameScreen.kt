package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.ui.components.GameCard
import com.example.gamefest.ui.viewmodels.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(factory = GameViewModel.Factory),
    onGameClick: (Int) -> Unit = {}
) {
    val gameList by viewModel.games.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tous les Jeux") },
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
            items(gameList) { game ->
                GameCard(
                    game = game,
                    onClick = { onGameClick(game.id) }
                )
            }
        }
    }
}