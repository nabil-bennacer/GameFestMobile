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
                    ReservationCard(reservationWithZones)
                }
            }
        }
    }
}

@Composable
private fun ReservationCard(reservationWithZones: ReservationWithZones) {
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
                text = "Éditeur ID: ${reservation.publisherId}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Festival ID: ${reservation.festivalId}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (zones.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${zones.size} zone(s) réservée(s) — ${zones.sumOf { it.tableCount }} table(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
