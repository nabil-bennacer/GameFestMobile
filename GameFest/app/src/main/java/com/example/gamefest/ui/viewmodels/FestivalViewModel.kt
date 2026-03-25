package com.example.gamefest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.FestivalEntity
import com.example.gamefest.data.remote.dto.FestivalDto
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

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshFestivals()
        }
    }

    fun addFestival(name: String, location: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            val newFestival = FestivalDto(
                id = (0..Int.MAX_VALUE).random(), // Temporary ID generation for local
                name = name,
                location = location,
                startDate = startDate,
                endDate = endDate
            )
            repository.addFestival(newFestival)
        }
    }

    fun updateFestival(id: Int, name: String, location: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            val updatedFestival = FestivalDto(
                id = id,
                name = name,
                location = location,
                startDate = startDate,
                endDate = endDate
            )
            repository.updateFestival(id, updatedFestival)
        }
    }

    fun deleteFestival(id: Int) {
        viewModelScope.launch {
            repository.deleteFestival(id)
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
