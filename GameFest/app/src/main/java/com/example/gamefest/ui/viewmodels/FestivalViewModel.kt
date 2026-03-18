package com.example.gamefest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.FestivalEntity
import com.example.gamefest.data.repository.FestivalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FestivalViewModel(
    private val repository: FestivalRepository
) : ViewModel() {

    val festivals: StateFlow<List<FestivalEntity>> = repository.getAllFestivals()
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
            repository.refreshFestivals()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                val repository = application.container.festivalRepository
                FestivalViewModel(repository)
            }
        }
    }
}
