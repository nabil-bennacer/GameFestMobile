package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
    val pzDetails by viewModel.priceZonesWithDetails.collectAsState()
    var reservationToDelete by remember { mutableStateOf<Int?>(null) }

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
                items(items = reservations, key = { it.reservation.id }) { reservationWithZones ->
                    ReservationCard(
                        reservationWithZones = reservationWithZones,
                        publisherName = reservationWithZones.reservation.publisherName
                            ?: viewModel.getPublisherName(reservationWithZones.reservation.publisherId),
                        canDelete = (userRole == "ADMIN" || userRole == "SUPER_ORGANISATOR"),
                        onDeleteClick = { reservationToDelete = reservationWithZones.reservation.id },
                        priceZonesWithDetails = pzDetails,
                        getGameName = viewModel::getGameName,
                        getMapZoneName = { mapZoneId -> viewModel.getMapZone(mapZoneId)?.name ?: "Zone plan inconnue" },
                        getMapZonePriceZoneName = { mapZoneId, fallbackPriceZoneId ->
                            val mapZone = viewModel.getMapZone(mapZoneId)
                            viewModel.getPriceZoneName(mapZone?.priceZoneId ?: fallbackPriceZoneId)
                        }
                    )
                }
            }
        }
    }

    if (reservationToDelete != null) {
        AlertDialog(
            onDismissRequest = { reservationToDelete = null },
            title = { Text("Supprimer la réservation") },
            text = { Text("Voulez-vous vraiment supprimer cette réservation ? Cette action est irréversible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteReservation(reservationToDelete!!)
                        reservationToDelete = null
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { reservationToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun ReservationCard(
    reservationWithZones: ReservationWithZones,
    publisherName: String,
    canDelete: Boolean,
    onDeleteClick: () -> Unit,
    priceZonesWithDetails: List<com.example.gamefest.data.local.entity.PriceZoneWithDetails>,
    getGameName: (Int) -> String,
    getMapZoneName: (Int?) -> String,
    getMapZonePriceZoneName: (Int?, Int?) -> String
) {
    val reservation = reservationWithZones.reservation
    val zones = reservationWithZones.zones
    val games = reservationWithZones.games
    val zonePriceById = priceZonesWithDetails.associate { it.priceZone.id to it.priceZone.tablePrice }
    val reservationPrice = zones.sumOf { zone ->
        (zonePriceById[zone.priceZoneId] ?: 0.0) * zone.tableCount
    }

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
                    text = "Réservation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(
                        onClick = {},
                        label = { Text(reservation.status.replace("_", " ")) }
                    )
                    if (canDelete) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Supprimer la réservation",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = publisherName,
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
                        text = "$zoneName : ${zone.tableCount} table(s)",
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
                Text(
                    text = "Montant : ${String.format("%.2f €", reservationPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (games.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Jeux placés",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                games.forEach { game ->
                    val displayGameName = game.gameName ?: getGameName(game.gameId)
                    val displayMapZoneName = game.mapZoneName ?: getMapZoneName(game.mapZoneId)
                    Text(
                        text = "$displayGameName - $displayMapZoneName (${getMapZonePriceZoneName(game.mapZoneId, game.mapZonePriceZoneId)})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${game.allocatedTables} table(s), ${game.copyCount} exemplaire(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
