package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.data.local.entity.GameEntity
import com.example.gamefest.data.local.entity.MapZoneEntity
import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.ui.viewmodels.GameSelection
import com.example.gamefest.ui.viewmodels.ReservationEntryViewModel
import com.example.gamefest.ui.viewmodels.ZoneSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationEntryScreen(
    festivalId: Int? = null,
    onNavigateUp: () -> Unit,
    viewModel: ReservationEntryViewModel = viewModel(factory = ReservationEntryViewModel.provideFactory(festivalId))
) {
    val publishers by viewModel.publishers.collectAsState()
    val festivals by viewModel.festivals.collectAsState()
    val allGames by viewModel.allGames.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouvelle Réservation") },
                navigationIcon = { IconButton(onClick = onNavigateUp) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            DropdownSelector(
                label = "Festival *", selectedValue = viewModel.details.festivalId, items = festivals,
                itemToId = { it.id.toString() }, itemToLabel = { it.name },
                onItemSelected = { viewModel.updateFestival(it) }, enabled = festivalId == null
            )

            DropdownSelector(
                label = "Éditeur / Réservant *", selectedValue = viewModel.details.publisherId, items = publishers,
                itemToId = { it.id.toString() }, itemToLabel = { it.name },
                onItemSelected = { viewModel.updatePublisher(it) }
            )

            HorizontalDivider()

            Text("Facturation", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            viewModel.details.selectedZones.forEachIndexed { index, zone ->
                ZoneBlock(
                    index = index, zone = zone, priceZonesWithDetails = viewModel.priceZonesWithDetails,
                    canRemove = viewModel.details.selectedZones.size > 1,
                    onZoneChanged = { viewModel.updateZone(index, it) }, onRemove = { viewModel.removeZone(index) }
                )
            }
            TextButton(onClick = { viewModel.addZone() }, modifier = Modifier.fillMaxWidth()) { Text("+ Ajouter une zone tarifaire") }

            HorizontalDivider()


            Text("Placement des Jeux", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            if (viewModel.details.selectedGames.isEmpty()) {
                Text("Vous pouvez placer des jeux maintenant ou plus tard.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            viewModel.details.selectedGames.forEachIndexed { index, gameSelection ->
                GameBlock(
                    index = index, gameSelection = gameSelection,
                    allGames = viewModel.filteredGames, mapZones = viewModel.allMapZones,
                    onGameChanged = { viewModel.updateGame(index, it) }, onRemove = { viewModel.removeGame(index) }
                )
            }
            TextButton(onClick = { viewModel.addGame() }, modifier = Modifier.fillMaxWidth()) { Text("+ Placer un jeu") }

            if (viewModel.hasPlacementOverflow) {
                Text(
                    text = "Attention: vous placez ${String.format("%.1f", viewModel.totalPlacedTables)} table(s) de jeux alors que la réservation couvre ${String.format("%.1f", viewModel.totalReservedTables)} table(s).",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()


            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Total estimé", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(String.format("%.2f €", viewModel.totalPrice), style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                }
            }

            Button(
                onClick = {
                    viewModel.saveReservation { success ->
                        if (success) {
                            onNavigateUp()
                        }
                    }
                },
                enabled = viewModel.isFormValid && !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSaving) "Enregistrement..." else "Confirmer la réservation")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ZoneBlock(index: Int, zone: ZoneSelection, priceZonesWithDetails: List<PriceZoneWithDetails>, canRemove: Boolean, onZoneChanged: (ZoneSelection) -> Unit, onRemove: () -> Unit) {
    val selectedZoneId = zone.priceZoneId.toIntOrNull()
    val selectedZoneDetails = priceZonesWithDetails.find { it.priceZone.id == selectedZoneId }
    val availableTables = selectedZoneDetails?.tableTypes?.sumOf { it.nbAvailable.toInt() } ?: 0
    val totalTables = selectedZoneDetails?.tableTypes?.sumOf { it.nbTotal.toInt() } ?: 0
    val requestedTables = zone.tableCount.toIntOrNull() ?: 0
    val isOverCapacity = selectedZoneId != null && requestedTables > availableTables

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Tarif ${index + 1}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                if (canRemove) {
                    IconButton(onClick = onRemove) { Icon(Icons.Default.Delete, "Supprimer", tint = MaterialTheme.colorScheme.error) }
                }
            }

            DropdownSelector(
                "Zone tarifaire *", zone.priceZoneId,
                priceZonesWithDetails.map { it.priceZone },
                { it.id.toString() },
                { pz ->
                    val zd = priceZonesWithDetails.find { it.priceZone.id == pz.id }
                    val avail = zd?.tableTypes?.sumOf { it.nbAvailable.toInt() } ?: 0
                    val total = zd?.tableTypes?.sumOf { it.nbTotal.toInt() } ?: 0
                    "${pz.name} (${pz.tablePrice} €) — $avail/$total tables"
                },
                { onZoneChanged(zone.copy(priceZoneId = it)) }
            )

            OutlinedTextField(
                value = zone.tableCount,
                onValueChange = { newValue -> onZoneChanged(zone.copy(tableCount = newValue.filter { it.isDigit() })) },
                label = { Text("Nombre de tables *") },
                supportingText = if (selectedZoneId != null) {
                    { Text("$availableTables tables disponibles sur $totalTables") }
                } else null,
                isError = isOverCapacity,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            if (isOverCapacity) {
                Text(
                    text = "⚠️ Attention : vous demandez $requestedTables tables mais seulement $availableTables sont disponibles !",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GameBlock(index: Int, gameSelection: GameSelection, allGames: List<GameEntity>, mapZones: List<MapZoneEntity>, onGameChanged: (GameSelection) -> Unit, onRemove: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Jeu ${index + 1}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = onRemove) { Icon(Icons.Default.Delete, "Supprimer", tint = MaterialTheme.colorScheme.error) }
            }

            DropdownSelector("Sélectionner un jeu *", gameSelection.gameId, allGames, { it.id.toString() }, { it.name }, { onGameChanged(gameSelection.copy(gameId = it)) })

            val mapOptions = listOf(Pair("", "Non placé")) + mapZones.map { Pair(it.id.toString(), it.name) }
            DropdownSelectorPair("Zone Plan", gameSelection.mapZoneId, mapOptions) { onGameChanged(gameSelection.copy(mapZoneId = it)) }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = gameSelection.allocatedTables,
                    onValueChange = { onGameChanged(gameSelection.copy(allocatedTables = it)) },
                    label = { Text("Tables (ex: 0.5)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true
                )
                OutlinedTextField(
                    value = gameSelection.copyCount,
                    onValueChange = { onGameChanged(gameSelection.copy(copyCount = it.filter { c -> c.isDigit() })) },
                    label = { Text("Exemplaires") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), singleLine = true
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownSelector(label: String, selectedValue: String, items: List<T>, itemToId: (T) -> String, itemToLabel: (T) -> String, onItemSelected: (String) -> Unit, enabled: Boolean = true) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = items.firstOrNull { itemToId(it) == selectedValue }?.let { itemToLabel(it) } ?: ""
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { if (enabled) expanded = !expanded }) {
        OutlinedTextField(value = selectedLabel, onValueChange = {}, readOnly = true, enabled = enabled, label = { Text(label) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item -> DropdownMenuItem(text = { Text(itemToLabel(item)) }, onClick = { onItemSelected(itemToId(item)); expanded = false }) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelectorPair(label: String, selectedValue: String, items: List<Pair<String, String>>, onItemSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = items.firstOrNull { it.first == selectedValue }?.second ?: ""
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(value = selectedLabel, onValueChange = {}, readOnly = true, label = { Text(label) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item -> DropdownMenuItem(text = { Text(item.second) }, onClick = { onItemSelected(item.first); expanded = false }) }
        }
    }
}