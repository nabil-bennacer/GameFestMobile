package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.data.local.entity.ReservationWithZones
import com.example.gamefest.ui.viewmodels.ReservationListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationListScreen(
    viewModel: ReservationListViewModel = viewModel(factory = ReservationListViewModel.Factory),
    userRole: String? = null,
    onAddClick: () -> Unit = {}
) {
    val reservations by viewModel.reservations.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Réservations") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            if (userRole == "ADMIN" || userRole == "SUPER_ORGANISATOR") {
                FloatingActionButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = "Nouvelle Réservation")
                }
            }
        }
    ) { paddingValues ->
        if (reservations.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucune réservation pour le moment.")
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reservations) { reservationWithZones ->
                    val pzDetails by viewModel.priceZonesWithDetails.collectAsState()
                    ReservationCard(
                        reservationWithZones = reservationWithZones,
                        publisherName = viewModel.getPublisherName(reservationWithZones.reservation.publisherId),
                        priceZonesWithDetails = pzDetails
                    )
                }
            }
        }
    }
}

@Composable
private fun ReservationCard(
    reservationWithZones: ReservationWithZones,
    publisherName: String,
    priceZonesWithDetails: List<com.example.gamefest.data.local.entity.PriceZoneWithDetails>
) {
    val reservation = reservationWithZones.reservation
    val zones = reservationWithZones.zones

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Réservation #${reservation.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                AssistChip(
                    onClick = {},
                    label = { Text(reservation.status.replace("_", " ")) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📦 $publisherName",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            if (zones.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                zones.forEach { zone ->
                    val zoneName = priceZonesWithDetails.find { it.priceZone.id == zone.priceZoneId }?.priceZone?.name ?: "Zone #${zone.priceZoneId}"
                    Text(
                        text = "🎯 $zoneName : ${zone.tableCount} table(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Total : ${zones.sumOf { it.tableCount }} table(s)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
