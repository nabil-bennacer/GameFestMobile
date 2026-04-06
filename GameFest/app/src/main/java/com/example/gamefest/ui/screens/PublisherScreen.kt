package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
    val publishers by viewModel.filteredPublishers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Éditeurs") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Rechercher un éditeur...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedRole == "Exposant",
                    onClick = { viewModel.updateSelectedRole("Exposant") },
                    label = { Text("Exposants") }
                )
                FilterChip(
                    selected = selectedRole == "Distributeur",
                    onClick = { viewModel.updateSelectedRole("Distributeur") },
                    label = { Text("Distributeurs") }
                )
            }


            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(publishers) { publisher ->
                    PublisherCard(
                        publisher = publisher,
                        onClick = { onPublisherClick(publisher.id, publisher.name) }
                    )
                }
            }
        }
    }
}