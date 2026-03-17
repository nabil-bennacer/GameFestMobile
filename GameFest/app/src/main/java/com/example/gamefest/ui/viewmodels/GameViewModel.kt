package com.example.gamefest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.GameEntity
import com.example.gamefest.data.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {
    val games: StateFlow<List<GameEntity>> = repository.getAllGames()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            repository.refreshGames()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                val repository = application.container.gameRepository
                GameViewModel(repository)
            }
        }
    }
}