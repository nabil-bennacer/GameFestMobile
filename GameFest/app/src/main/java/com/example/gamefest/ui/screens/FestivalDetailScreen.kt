package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.ui.viewmodels.FestivalDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalDetailScreen(
    festivalId: Int,
    festivalName: String,
    userRole: String? = null,
    onBackClick: () -> Unit,
    onReserveClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val application = context.applicationContext as GameFestApplication
    val viewModel: FestivalDetailViewModel = viewModel(
        factory = FestivalDetailViewModel.provideFactory(
            application.container.priceZoneRepository,
            application.container.festivalRepository,
            festivalId
        )
    )

    val priceZones by viewModel.priceZones.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(festivalName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            if (userRole == "ADMIN" || userRole == "SUPER_ORGANISATOR") {
                ExtendedFloatingActionButton(
                    onClick = onReserveClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Réserver des tables") }
                )
            }
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Zones de prix",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (priceZones.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucune zone de prix définie pour ce festival.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(priceZones) { zoneWithDetails ->
                        PriceZoneDetailCard(zoneWithDetails = zoneWithDetails)
                    }
                }
            }
        }
    }
}

@Composable
fun PriceZoneDetailCard(zoneWithDetails: PriceZoneWithDetails) {
    val zone = zoneWithDetails.priceZone
    val tableTypes = zoneWithDetails.tableTypes

    val totalTables = tableTypes.sumOf { it.nbTotal.toInt() }
    val availableTables = tableTypes.sumOf { it.nbAvailable.toInt() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = zone.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${zone.tablePrice} € / table",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tables disponibles :",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$availableTables / $totalTables",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (availableTables > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            if (zoneWithDetails.mapZones.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Zones associées : ${zoneWithDetails.mapZones.joinToString { it.name }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}