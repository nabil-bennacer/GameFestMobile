package com.example.gamefest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.data.repository.FestivalRepository
import com.example.gamefest.data.repository.PriceZoneRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FestivalDetailViewModel(
    private val repository: PriceZoneRepository,
    private val festivalRepository: FestivalRepository,
    private val festivalId: Int
) : ViewModel() {

    val priceZones: StateFlow<List<PriceZoneWithDetails>> = repository.getPriceZonesForFestival(festivalId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refreshPriceZones()
    }

    fun refreshPriceZones() {
        viewModelScope.launch {
            // Refresh festivals first — the response contains embedded priceZones with tableTypes
            festivalRepository.refreshFestivals()
            // Then refresh price zones specifically for this festival
            repository.refreshPriceZones(festivalId)
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
