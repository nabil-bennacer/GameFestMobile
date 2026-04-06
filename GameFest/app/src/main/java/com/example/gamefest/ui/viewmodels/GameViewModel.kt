package com.example.gamefest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.GameEntity
import com.example.gamefest.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType: StateFlow<String?> = _selectedType

    val gamesList: StateFlow<List<GameEntity>> = combine(
    repository.getAllGames(),
    _searchQuery,
    _selectedType
    ) { games, query, type ->
        games.filter { game ->
            val matchesSearch = game.name.contains(query, ignoreCase = true)

            val matchesType = type == null || game.type == type

            matchesSearch && matchesType
        }
    }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
    )

    val gameTypes: StateFlow<List<String>> = repository.getAllGames()
        .map { games ->
            games.map { it.type }
                .distinct()
                .filter { it.isNotBlank() }
                .sorted()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedType(type: String?) {
        // Si on clique sur le type déjà sélectionné, ça le désélectionne
        _selectedType.value = if (_selectedType.value == type) null else type
    }

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