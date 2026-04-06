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
import com.example.gamefest.data.local.entity.FestivalEntity
import com.example.gamefest.data.local.entity.MapZoneEntity
import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.ui.viewmodels.ReservationEntryViewModel
import com.example.gamefest.ui.viewmodels.ZoneSelection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationEntryScreen(
    festivalId: Int? = null,
    onNavigateUp: () -> Unit,
    viewModel: ReservationEntryViewModel = viewModel(
        factory = ReservationEntryViewModel.provideFactory(festivalId)
    )
) {
    val coroutineScope = rememberCoroutineScope()
    val publishers by viewModel.publishers.collectAsState()
    val festivals by viewModel.festivals.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouvelle Réservation") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            DropdownSelector(
                label = "Éditeur *",
                selectedValue = viewModel.details.publisherId,
                items = publishers,
                itemToId = { it.id.toString() },
                itemToLabel = { it.name },
                onItemSelected = { viewModel.updatePublisher(it) }
            )

            DropdownSelector(
                label = "Festival *",
                selectedValue = viewModel.details.festivalId,
                items = festivals,
                itemToId = { it.id.toString() },
                itemToLabel = { it.name },
                onItemSelected = { viewModel.updateFestival(it) },
                enabled = festivalId == null // Désactivé si pré-sélectionné
            )

            HorizontalDivider()

            Text(
                text = "Zones de réservation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            viewModel.details.selectedZones.forEachIndexed { index, zone ->
                ZoneBlock(
                    index = index,
                    zone = zone,
                    priceZonesWithDetails = viewModel.priceZonesWithDetails,
                    filteredMapZones = viewModel.getFilteredMapZones(zone.priceZoneId),
                    canRemove = viewModel.details.selectedZones.size > 1,
                    onZoneChanged = { updatedZone -> viewModel.updateZone(index, updatedZone) },
                    onRemove = { viewModel.removeZone(index) }
                )
            }

            TextButton(
                onClick = { viewModel.addZone() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Ajouter une autre zone")
            }

            HorizontalDivider()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total estimé",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = String.format("%.2f €", viewModel.totalPrice),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.saveReservation()
                        onNavigateUp()
                    }
                },
                enabled = viewModel.isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Valider la réservation")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
private fun ZoneBlock(
    index: Int,
    zone: ZoneSelection,
    priceZonesWithDetails: List<PriceZoneWithDetails>,
    filteredMapZones: List<MapZoneEntity>,
    canRemove: Boolean,
    onZoneChanged: (ZoneSelection) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zone ${index + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                if (canRemove) {
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer cette zone",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Dropdown PriceZone
            DropdownSelector(
                label = "Zone tarifaire *",
                selectedValue = zone.priceZoneId,
                items = priceZonesWithDetails.map { it.priceZone },
                itemToId = { it.id.toString() },
                itemToLabel = { "${it.name} (${it.tablePrice} €/table)" },
                onItemSelected = { priceZoneId ->
                    // Quand on change de PriceZone, on reset la MapZone
                    onZoneChanged(zone.copy(priceZoneId = priceZoneId, mapZoneId = ""))
                }
            )

            // Dropdown MapZone (filtré par PriceZone)
            DropdownSelector(
                label = "Zone plan",
                selectedValue = zone.mapZoneId,
                items = filteredMapZones,
                itemToId = { it.id.toString() },
                itemToLabel = { it.name },
                onItemSelected = { mapZoneId ->
                    onZoneChanged(zone.copy(mapZoneId = mapZoneId))
                },
                enabled = zone.priceZoneId.isNotBlank()
            )

            // Champ nombre de tables
            OutlinedTextField(
                value = zone.tableCount,
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() }
                    onZoneChanged(zone.copy(tableCount = filtered))
                },
                label = { Text("Nombre de tables *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownSelector(
    label: String,
    selectedValue: String,
    items: List<T>,
    itemToId: (T) -> String,
    itemToLabel: (T) -> String,
    onItemSelected: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = items.firstOrNull { itemToId(it) == selectedValue }
        ?.let { itemToLabel(it) } ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemToLabel(item)) },
                    onClick = {
                        onItemSelected(itemToId(item))
                        expanded = false
                    }
                )
            }
        }
    }
}
