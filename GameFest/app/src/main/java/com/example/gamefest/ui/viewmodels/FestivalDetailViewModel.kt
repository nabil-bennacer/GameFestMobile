package com.example.gamefest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.data.repository.FestivalRepository
import com.example.gamefest.data.repository.PriceZoneRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FestivalDetailViewModel(
    private val repository: PriceZoneRepository,
    private val festivalRepository: FestivalRepository,
    private val festivalId: Int
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    val priceZones: StateFlow<List<PriceZoneWithDetails>> = repository.getPriceZonesForFestival(festivalId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addMapZone(priceZoneId: Int, name: String, tablesCount: Int) {
        viewModelScope.launch {
            repository.addMapZone(festivalId, priceZoneId, name, tablesCount)
        }
    }
    fun refreshPriceZones() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
               
                festivalRepository.refreshFestivals()
               
                repository.refreshPriceZones(festivalId)

        
                if (repository.getPriceZonesForFestival(festivalId).first().isEmpty()) {
                    delay(400)
                    repository.refreshPriceZones(festivalId)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun deletePriceZone(id: Int) {
        viewModelScope.launch {
            repository.deletePriceZone(id)
        }
    }

    companion object {
        fun provideFactory(
            repository: PriceZoneRepository,
            festivalRepository: FestivalRepository,
            festivalId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FestivalDetailViewModel(repository, festivalRepository, festivalId)
            }
        }
    }
}
