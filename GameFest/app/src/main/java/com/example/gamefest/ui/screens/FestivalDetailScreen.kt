package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.ui.viewmodels.FestivalDetailViewModel

data class PlacedGameDisplay(
    val publisherName: String,
    val gameName: String,
    val mapZoneId: Int?,
    val mapZoneName: String,
    val allocatedTables: Float,
    val copyCount: Int
)

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
        key = "festival_detail_$festivalId",
        factory = FestivalDetailViewModel.provideFactory(
            application.container.priceZoneRepository,
            application.container.festivalRepository,
            application.container.reservationRepository,
            festivalId
        )
    )

    val priceZones by viewModel.priceZones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val reservations by viewModel.reservationsForFestival.collectAsState()

    var showMapZoneDialog by remember { mutableStateOf(false) }
    var selectedPriceZoneIdForMapZone by remember { mutableStateOf<Int?>(null) }
    var selectedPriceZoneRemainingTables by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(festivalId) {
        viewModel.refreshPriceZones()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(festivalName) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (priceZones.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucune zone de prix définie pour ce festival.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(priceZones) { zoneWithDetails ->
                        val mapZonesById = zoneWithDetails.mapZones.associateBy { it.id }
                        val zoneMapIds = mapZonesById.keys
                        val reservedTables = reservations
                            .flatMap { it.zones }
                            .filter { it.priceZoneId == zoneWithDetails.priceZone.id }
                            .sumOf { it.tableCount }
                        val placedGames = reservations.flatMap { reservationWithZones ->
                            val publisherName = reservationWithZones.reservation.publisherName
                                ?: "Éditeur #${reservationWithZones.reservation.publisherId}"
                            reservationWithZones.games
                                .filter { it.mapZoneId != null && zoneMapIds.contains(it.mapZoneId) }
                                .map { game ->
                                    val fallbackMapZoneName = mapZonesById[game.mapZoneId]?.name ?: "Zone plan #${game.mapZoneId}"
                                    PlacedGameDisplay(
                                        publisherName = publisherName,
                                        gameName = game.gameName ?: "Jeu #${game.gameId}",
                                        mapZoneId = game.mapZoneId,
                                        mapZoneName = game.mapZoneName ?: fallbackMapZoneName,
                                        allocatedTables = game.allocatedTables,
                                        copyCount = game.copyCount
                                    )
                                }
                        }

                        PriceZoneDetailCard(
                            zoneWithDetails = zoneWithDetails,
                            userRole = userRole,
                            reservedTablesCount = reservedTables,
                            placedGames = placedGames,
                            onAddMapZoneClick = {
                                selectedPriceZoneIdForMapZone = zoneWithDetails.priceZone.id
                                val totalZoneTables = zoneWithDetails.tableTypes.sumOf { it.nbTotal.toInt() }
                                val alreadyAssigned = zoneWithDetails.mapZones.sumOf { it.smallTables + it.largeTables + it.cityTables }
                                selectedPriceZoneRemainingTables = (totalZoneTables - alreadyAssigned).coerceAtLeast(0)
                                showMapZoneDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showMapZoneDialog && selectedPriceZoneIdForMapZone != null) {
        MapZoneDialog(
            onDismiss = { showMapZoneDialog = false },
            maxTables = selectedPriceZoneRemainingTables ?: 0,
            onConfirm = { name, tables ->
                viewModel.addMapZone(selectedPriceZoneIdForMapZone!!, name, tables)
                showMapZoneDialog = false
            }
        )
    }
}

@Composable
fun MapZoneDialog(onDismiss: () -> Unit, maxTables: Int, onConfirm: (String, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var tablesCount by remember { mutableStateOf("1") }
    val tablesValue = tablesCount.toIntOrNull() ?: 0
    val exceedsCapacity = tablesValue > maxTables

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une zone plan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom (ex: Zone Famille)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = tablesCount,
                    onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) tablesCount = it },
                    label = { Text("Nombre de tables") },
                    supportingText = { Text("Maximum autorisé: $maxTables") },
                    isError = exceedsCapacity,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (exceedsCapacity) {
                    Text(
                        text = "Le nombre de tables dépasse la capacité disponible de la zone tarifaire.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, tablesCount.toIntOrNull() ?: 1) },
                enabled = name.isNotBlank() && tablesCount.isNotBlank() && !exceedsCapacity && tablesValue > 0
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun PriceZoneDetailCard(
    zoneWithDetails: PriceZoneWithDetails,
    userRole: String?,
    reservedTablesCount: Int,
    placedGames: List<PlacedGameDisplay>,
    onAddMapZoneClick: () -> Unit
) {
    val zone = zoneWithDetails.priceZone
    val tableTypes = zoneWithDetails.tableTypes

    val totalTables = tableTypes.sumOf { it.nbTotal.toInt() }
    val availableTables = (totalTables - reservedTablesCount).coerceAtLeast(0)

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
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Zones Plans (Répartition) :",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    zoneWithDetails.mapZones.forEach { mapZone ->
                        val mapZoneTables = mapZone.smallTables + mapZone.largeTables + mapZone.cityTables
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "• ${mapZone.name}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "$mapZoneTables tables", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }

                        val gamesInMapZone = placedGames.filter { it.mapZoneId == mapZone.id }
                        if (gamesInMapZone.isNotEmpty()) {
                            gamesInMapZone.forEach { game ->
                                Text(
                                    text = "   ${game.gameName} - ${game.publisherName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "   ${game.allocatedTables} table(s), ${game.copyCount} exemplaire(s)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            if (userRole == "ADMIN" || userRole == "SUPER_ORGANISATOR") {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onAddMapZoneClick, modifier = Modifier.align(Alignment.End)) {
                    Text("+ Ajouter une zone plan")
                }
            }
        }
    }
}