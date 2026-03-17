package com.example.gamefest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.data.repository.PublisherRepository
import com.example.gamefest.data.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PublisherViewModel(
    private val repositoryPublisher: PublisherRepository,
    private val repositoryGame: GameRepository
) : ViewModel() {

    val publishers: StateFlow<List<PublisherEntity>> = repositoryPublisher.getAllPublishers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Déclenche la synchronisation web au lancement du ViewModel
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            repositoryPublisher.refreshPublishers()
            repositoryGame.refreshGames()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                val pubRepository = application.container.publisherRepository
                val gameRepository = application.container.gameRepository
                PublisherViewModel(pubRepository,
                    gameRepository)



            }
        }
    }
}