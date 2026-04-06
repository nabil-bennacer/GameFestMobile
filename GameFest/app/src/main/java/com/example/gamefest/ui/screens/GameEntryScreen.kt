package com.example.gamefest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.ui.viewmodels.GameDetails
import com.example.gamefest.ui.viewmodels.GameEntryViewModel
import com.example.gamefest.ui.viewmodels.GameUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameEntryScreen(
    preselectedPublisherId: Int? = null,
    onNavigateUp: () -> Unit,
    viewModel: GameEntryViewModel = viewModel(factory = GameEntryViewModel.Factory)

) {
    val coroutineScope = rememberCoroutineScope()
    val publisherList by viewModel.publisherList.collectAsState()

    // On récupère la liste des types de jeux
    val gameTypes by viewModel.gameTypes.collectAsState()

    LaunchedEffect(preselectedPublisherId) {
        preselectedPublisherId?.let { id ->
            viewModel.preselectPublisher(id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter un Jeu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        GameEntryBody(
            gameUiState = viewModel.gameUiState,
            publishers = publisherList,
            gameTypes = gameTypes,
            onGameValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveGame()
                    onNavigateUp()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun GameEntryBody(
    gameUiState: GameUiState,
    publishers: List<PublisherEntity>,
    gameTypes: List<String>,
    onGameValueChange: (GameDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GameInputForm(
            gameDetails = gameUiState.gameDetails,
            publishers = publishers,
            gameTypes = gameTypes,
            onValueChange = onGameValueChange
        )

        Button(
            onClick = onSaveClick,
            enabled = gameUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Enregistrer le jeu")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameInputForm(
    gameDetails: GameDetails,
    publishers: List<PublisherEntity>,
    gameTypes : List<String>,
    modifier: Modifier = Modifier,
    onValueChange: (GameDetails) -> Unit = {}
) {
    // Variable pour savoir si la liste déroulante est ouverte ou fermée
    var expanded by remember { mutableStateOf(false) }

    var expandedType by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = gameDetails.name,
            onValueChange = { onValueChange(gameDetails.copy(name = it)) },
            label = { Text("Nom du jeu *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        ExposedDropdownMenuBox(
            expanded = expandedType,
            onExpandedChange = { expandedType = !expandedType }
        ) {
            OutlinedTextField(
                value = gameDetails.type,
                onValueChange = {}, // Lecture seule
                readOnly = true,
                label = { Text("Catégorie du jeu *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedType,
                onDismissRequest = { expandedType = false }
            ) {
                gameTypes.forEach { typeOption ->
                    DropdownMenuItem(
                        text = { Text(typeOption) },
                        onClick = {
                            onValueChange(gameDetails.copy(type = typeOption))
                            expandedType = false
                        }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = gameDetails.minAge,
                onValueChange = { onValueChange(gameDetails.copy(minAge = it)) },
                label = { Text("Âge min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            OutlinedTextField(
                value = gameDetails.maxPlayers,
                onValueChange = { onValueChange(gameDetails.copy(maxPlayers = it)) },
                label = { Text("Joueurs max") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }



        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded } // Ouvre/Ferme au clic
        ) {
            // On cherche le nom de l'éditeur sélectionné pour l'afficher dans la case
            val selectedPublisherName = publishers.find { it.id.toString() == gameDetails.publisherId }?.name ?: ""

            OutlinedTextField(
                value = selectedPublisherName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Éditeur") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            // Le menu avec tous les choix
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // S'il n'y a pas d'éditeurs, on met un petit message
                if (publishers.isEmpty()) {
                    DropdownMenuItem(text = { Text("Aucun éditeur disponible") }, onClick = { expanded = false })
                } else {
                    // Sinon, on boucle sur tous les éditeurs pour créer une ligne cliquable
                    publishers.forEach { publisher ->
                        DropdownMenuItem(
                            text = { Text(publisher.name) },
                            onClick = {
                                onValueChange(gameDetails.copy(publisherId = publisher.id.toString()))
                                expanded = false // On referme le menu
                            }
                        )
                    }
                }
            }
        }
    }
}