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
import com.example.gamefest.data.local.entity.GameEntity
import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.data.repository.GameRepository
import com.example.gamefest.data.repository.PublisherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameEditViewModel(
    private val gameRepository: GameRepository,
    private val publisherRepository: PublisherRepository
) : ViewModel() {

    var gameUiState by mutableStateOf(GameUiState())
        private set

    // On récupère aussi la liste des éditeurs pour la liste déroulante
    val publisherList: StateFlow<List<PublisherEntity>> = publisherRepository.getAllPublishers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 1. Charger les données du jeu
    fun loadGame(gameId: Int) {
        viewModelScope.launch {
            val game = gameRepository.getGameById(gameId).filterNotNull().first()
            gameUiState = GameUiState(
                gameDetails = game.toDetails(),
                isEntryValid = true
            )
        }
    }

    fun updateUiState(gameDetails: GameDetails) {
        gameUiState = GameUiState(
            gameDetails = gameDetails,
            isEntryValid = validateInput(gameDetails)
        )
    }


    suspend fun updateGame() {
        if (gameUiState.isEntryValid) {
            gameRepository.updateGame(gameUiState.gameDetails.toDto())
        }
    }

    private fun validateInput(uiState: GameDetails = gameUiState.gameDetails): Boolean {
        return uiState.name.isNotBlank() && uiState.type.isNotBlank()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                GameEditViewModel(
                    gameRepository = application.container.gameRepository,
                    publisherRepository = application.container.publisherRepository
                )
            }
        }
    }
}

fun GameEntity.toDetails() = GameDetails(
    id = id,
    name = name,
    type = type,
    minAge = minAge?.toString() ?: "",
    imageUrl = imageUrl ?: "",
    publisherId = publisherId?.toString() ?: "",
    maxPlayers = maxPlayers?.toString() ?: "",
)