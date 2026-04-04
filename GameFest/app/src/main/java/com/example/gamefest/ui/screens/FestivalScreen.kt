package com.example.gamefest.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.data.local.entity.FestivalEntity
import com.example.gamefest.ui.components.FestivalCard
import com.example.gamefest.ui.viewmodels.FestivalViewModel
import com.example.gamefest.ui.viewmodels.PriceZoneOption
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalScreen(
    viewModel: FestivalViewModel = viewModel(factory = FestivalViewModel.Factory),
    onFestivalClick: (Int, String) -> Unit = { _, _ -> }
) {
    val festivalList by viewModel.festivals.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var festivalToEdit by remember { mutableStateOf<FestivalEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Festivals GameFest") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                festivalToEdit = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un festival")
            }
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
                    onEditClick = {
                        festivalToEdit = festival
                        showDialog = true
                    },
                    onDeleteClick = {
                        viewModel.deleteFestival(festival.id)
                    },
                    onClick = {
                        onFestivalClick(festival.id, festival.name)
                    }
                )
            }
        }

        if (showDialog) {
            FestivalDialog(
                festival = festivalToEdit,
                onDismiss = { showDialog = false },
                onConfirm = { name, location, start, end, option ->
                    if (festivalToEdit == null) {
                        viewModel.addFestival(name, location, start, end, option)
                    } else {
                        viewModel.updateFestival(festivalToEdit!!.id, name, location, start, end, option)
                    }
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalDialog(
    festival: FestivalEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, PriceZoneOption) -> Unit
) {
    var name by remember { mutableStateOf(festival?.name ?: "") }
    var location by remember { mutableStateOf(festival?.location ?: "") }
    var startDate by remember { mutableStateOf(festival?.startDate ?: "") }
    var endDate by remember { mutableStateOf(festival?.endDate ?: "") }
    var selectedOption by remember { mutableStateOf(PriceZoneOption.STANDARD) }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

    fun showDatePicker(onDateSelected: (String) -> Unit, initialDate: String) {
        val current = try {
            if (initialDate.isNotEmpty()) dateFormat.parse(initialDate) ?: Date() else Date()
        } catch (e: Exception) {
            Date()
        }
        calendar.time = current
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (festival == null) "Ajouter un Festival" else "Modifier le Festival",
                    style = MaterialTheme.typography.headlineSmall
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lieu") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { },
                    label = { Text("Date de début") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker({ startDate = it }, startDate) },
                    enabled = false,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker({ startDate = it }, startDate) }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Choisir date")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                OutlinedTextField(
                    value = endDate,
                    onValueChange = { },
                    label = { Text("Date de fin") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker({ endDate = it }, endDate) },
                    enabled = false,
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker({ endDate = it }, endDate) }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Choisir date")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedOption.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Option Zones de Prix") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        PriceZoneOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = { onConfirm(name, location, startDate, endDate, selectedOption) },
                        enabled = name.isNotBlank()
                    ) {
                        Text("Confirmer")
                    }
                }
            }
        }
    }
}
