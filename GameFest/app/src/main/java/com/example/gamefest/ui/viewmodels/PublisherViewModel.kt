package com.example.gamefest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.data.repository.PublisherRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PublisherViewModel(
    private val repository: PublisherRepository
) : ViewModel() {

    // L'état de l'écran observé par Jetpack Compose
    val publishers: StateFlow<List<PublisherEntity>> = repository.getAllPublishers()
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
            repository.refreshPublishers()
        }
    }

    // L'usine pour créer ce ViewModel en lui injectant le Repository
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                val repository = application.container.publisherRepository
                PublisherViewModel(repository)
            }
        }
    }
}