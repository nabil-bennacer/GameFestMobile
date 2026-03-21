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
import com.example.gamefest.ui.components.FestivalCard
import com.example.gamefest.ui.viewmodels.FestivalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalScreen(
    viewModel: FestivalViewModel = viewModel(factory = FestivalViewModel.Factory),
    onFestivalClick: (Int) -> Unit = {}
) {
    val festivalList by viewModel.festivals.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Festivals GameFest") },
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
            items(festivalList) { festival ->
                FestivalCard(
                    festival = festival,
                    onClick = { onFestivalClick(festival.id) }
                )
            }
        }
    }
}
