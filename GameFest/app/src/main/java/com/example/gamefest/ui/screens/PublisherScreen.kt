package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// Imports depuis nos nouveaux dossiers
import com.example.gamefest.ui.components.PublisherCard
import com.example.gamefest.ui.viewmodels.PublisherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublisherScreen(
    viewModel: PublisherViewModel = viewModel(factory = PublisherViewModel.Factory),
    onPublisherClick: (Int, String) -> Unit = { _, _ -> }
) {
    val publisherList by viewModel.publishers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Éditeurs GameFest") },
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
            items(publisherList) { publisher ->
                PublisherCard(publisher = publisher,
                    onClick = {
                        // Quand on clique, on envoie l'ID et le nom de l'éditeur !
                        onPublisherClick(publisher.id, publisher.name)
                    }
                )
            }
        }
    }
}