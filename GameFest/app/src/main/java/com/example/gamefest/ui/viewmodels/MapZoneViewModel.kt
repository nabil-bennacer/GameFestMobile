package com.example.gamefest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.MapZoneEntity
import com.example.gamefest.data.repository.PriceZoneRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MapZoneViewModel(
    private val repository: PriceZoneRepository,
    private val priceZoneId: Int
) : ViewModel() {

    val mapZones: StateFlow<List<MapZoneEntity>> = repository.getMapZonesByPriceZone(priceZoneId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    companion object {
        fun provideFactory(
            repository: PriceZoneRepository,
            priceZoneId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MapZoneViewModel(repository, priceZoneId)
            }
        }
    }
}
