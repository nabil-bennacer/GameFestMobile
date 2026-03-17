package com.example.gamefest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.GameEntity
import com.example.gamefest.data.repository.GameRepository
import kotlinx.coroutines.flow.Flow

class GameDetailViewModel(private val repository: GameRepository) : ViewModel() {

    fun getGame(id: Int): Flow<GameEntity?> = repository.getGameById(id)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                GameDetailViewModel(application.container.gameRepository)
            }
        }
    }
}