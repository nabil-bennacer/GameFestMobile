package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.ui.viewmodels.PublisherDetails
import com.example.gamefest.ui.viewmodels.PublisherEntryViewModel
import com.example.gamefest.ui.viewmodels.PublisherUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublisherEntryScreen(
    onNavigateUp: () -> Unit,
    viewModel: PublisherEntryViewModel = viewModel(factory = PublisherEntryViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter un Éditeur") },
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
                // On lance la sauvegarde et on quitte l'écran
                coroutineScope.launch {
                    viewModel.savePublisher()
                    onNavigateUp()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun PublisherEntryBody(
    publisherUiState: PublisherUiState,
    onPublisherValueChange: (PublisherDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PublisherInputForm(
            publisherDetails = publisherUiState.publisherDetails,
            onValueChange = onPublisherValueChange
        )

        Button(
            onClick = onSaveClick,
            enabled = publisherUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Enregistrer")
        }
    }
}

@Composable
fun PublisherInputForm(
    publisherDetails: PublisherDetails,
    modifier: Modifier = Modifier,
    onValueChange: (PublisherDetails) -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = publisherDetails.name,
            onValueChange = { onValueChange(publisherDetails.copy(name = it)) },
            label = { Text("Nom de l'éditeur *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = publisherDetails.logoUrl,
            onValueChange = { onValueChange(publisherDetails.copy(logoUrl = it)) },
            label = { Text("URL du Logo (Optionnel)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Les cases à cocher (Exposant / Distributeur)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = publisherDetails.exposant,
                onCheckedChange = { onValueChange(publisherDetails.copy(exposant = it)) }
            )
            Text("Est un Exposant")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = publisherDetails.distributeur,
                onCheckedChange = { onValueChange(publisherDetails.copy(distributeur = it)) }
            )
            Text("Est un Distributeur")
        }
    }
}