package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.ui.viewmodels.PublisherEditViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublisherEditScreen(
    publisherId: Int,
    onNavigateUp: () -> Unit,
    viewModel: PublisherEditViewModel = viewModel(factory = PublisherEditViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(publisherId) {
        viewModel.loadPublisher(publisherId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modifier l'Éditeur") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        PublisherEntryBody(
            publisherUiState = viewModel.publisherUiState,
            onPublisherValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updatePublisher()
                    onNavigateUp() // On retourne à la page précédente après la sauvegarde
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}