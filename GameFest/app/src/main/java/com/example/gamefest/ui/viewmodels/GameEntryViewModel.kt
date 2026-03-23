package com.example.gamefest.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.data.remote.dto.GameDto
import com.example.gamefest.data.repository.GameRepository
import com.example.gamefest.data.repository.PublisherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class GameEntryViewModel(
    private val gameRepository: GameRepository,
    private val publisherRepository: PublisherRepository
) : ViewModel() {

    var gameUiState by mutableStateOf(GameUiState())
        private set

    // On récupère la liste de tous les éditeurs
    val publisherList: StateFlow<List<PublisherEntity>> =
        publisherRepository.getAllPublishers()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList() // Liste vide le temps que ça charge
            )

    fun updateUiState(gameDetails: GameDetails) {
        gameUiState = GameUiState(
            gameDetails = gameDetails,
            isEntryValid = validateInput(gameDetails)
        )
    }

    suspend fun saveGame() {
        if (validateInput()) {
            gameRepository.addGame(gameUiState.gameDetails.toDto())
        }
    }

    // Validation : Le nom et le type sont obligatoires
    private fun validateInput(uiState: GameDetails = gameUiState.gameDetails): Boolean {
        return uiState.name.isNotBlank() && uiState.type.isNotBlank()
    }

    fun preselectPublisher(publisherId: Int) {
        // Si l'ID de l'éditeur est vide, on le rempli
        if (gameUiState.gameDetails.publisherId.isEmpty()) {
            updateUiState(
                gameUiState.gameDetails.copy(publisherId = publisherId.toString())
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                GameEntryViewModel(
                    gameRepository = application.container.gameRepository,
                    publisherRepository = application.container.publisherRepository
                )
            }
        }
    }
}

data class GameUiState(
    val gameDetails: GameDetails = GameDetails(),
    val isEntryValid: Boolean = false
)

data class GameDetails(
    val id: Int = 0,
    val name: String = "",
    val type: String = "",
    val minAge: String = "",
    val imageUrl: String = "",
    val publisherId: String = "", // L'ID de l'éditeur tapé au clavier
    val maxPlayers: String = ""
)

// On convertit les String du formulaire en Int pour la base de données !
fun GameDetails.toDto(): GameDto = GameDto(
    id = id,
    name = name,
    type = type,
    minAge = minAge.toIntOrNull(),
    imageUrl = imageUrl.ifBlank { null },
    publisherId = publisherId.toIntOrNull(),
    maxPlayers = maxPlayers.toIntOrNull(),

)

