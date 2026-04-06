package com.example.gamefest.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.FestivalEntity
import com.example.gamefest.data.remote.dto.FestivalDto
import com.example.gamefest.data.remote.dto.PriceZoneRequest
import com.example.gamefest.data.remote.dto.TableTypeRequest
import com.example.gamefest.data.repository.FestivalRepository
import com.example.gamefest.data.repository.PriceZoneRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PriceZoneOption(val label: String, val id: Int) {
    STANDARD("Standard", 1),
    VIP("VIP", 2),
    BOTH("Standard et VIP", 3)
}

class FestivalViewModel(
    private val festivalRepository: FestivalRepository,
    private val priceZoneRepository: PriceZoneRepository
) : ViewModel() {

    val festivals: StateFlow<List<FestivalEntity>> = festivalRepository.getAllFestivals()
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
            festivalRepository.refreshFestivals()
        }
    }

    fun addFestival(
        name: String,
        location: String,
        startDate: String,
        endDate: String,
        tablesCount: Int,
        priceZoneOption: PriceZoneOption
    ) {
        viewModelScope.launch {
            val newFestivalDto = FestivalDto(
                id = 0,
                name = name,
                location = location,
                startDate = startDate,
                endDate = endDate,
                priceZoneTypeId = priceZoneOption.id,
                tablesCount = tablesCount
            )
            val createdFestival = festivalRepository.addFestival(newFestivalDto)
            
            if (createdFestival != null) {
                Log.d("FestivalVM", "Festival created: ${createdFestival.id}. Initializing zones...")
                createPriceZonesForFestival(createdFestival.id, priceZoneOption)
                festivalRepository.refreshFestivals()
            } else {
                Log.e("FestivalVM", "Failed to create festival")
            }
        }
    }

    private suspend fun createPriceZonesForFestival(festivalId: Int, option: PriceZoneOption) {
        val standardTableTypes = listOf(
            TableTypeRequest("STANDARD", 10, 4),
            TableTypeRequest("LARGE", 5, 8),
            TableTypeRequest("CITY", 2, 6)
        )
        
        val vipTableTypes = listOf(
            TableTypeRequest("STANDARD", 5, 4),
            TableTypeRequest("LARGE", 3, 8),
            TableTypeRequest("CITY", 1, 6)
        )

        try {
            when (option) {
                PriceZoneOption.STANDARD -> {
                    priceZoneRepository.createPriceZone(
                        PriceZoneRequest(festivalId, "Standard", 10.0, standardTableTypes)
                    )
                }
                PriceZoneOption.VIP -> {
                    priceZoneRepository.createPriceZone(
                        PriceZoneRequest(festivalId, "VIP", 25.0, vipTableTypes)
                    )
                }
                PriceZoneOption.BOTH -> {
                    priceZoneRepository.createPriceZone(
                        PriceZoneRequest(festivalId, "Standard", 10.0, standardTableTypes)
                    )
                    priceZoneRepository.createPriceZone(
                        PriceZoneRequest(festivalId, "VIP", 25.0, vipTableTypes)
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("FestivalVM", "Error creating price zones", e)
        }
    }

    fun updateFestival(
        id: Int,
        name: String,
        location: String,
        startDate: String,
        endDate: String,
        tablesCount: Int,
        priceZoneOption: PriceZoneOption
    ) {
        viewModelScope.launch {
            val updatedFestival = FestivalDto(
                id = id,
                name = name,
                location = location,
                startDate = startDate,
                endDate = endDate,
                priceZoneTypeId = priceZoneOption.id,
                tablesCount = tablesCount
            )
            festivalRepository.updateFestival(id, updatedFestival)
            
            // Sync price zones
            val existingZones = priceZoneRepository.getPriceZonesForFestival(id).first()
            val hasStandard = existingZones.any { it.priceZone.name.contains("Standard", ignoreCase = true) }
            val hasVip = existingZones.any { it.priceZone.name.contains("VIP", ignoreCase = true) }

            when (priceZoneOption) {
                PriceZoneOption.STANDARD -> if (!hasStandard) createPriceZonesForFestival(id, PriceZoneOption.STANDARD)
                PriceZoneOption.VIP -> if (!hasVip) createPriceZonesForFestival(id, PriceZoneOption.VIP)
                PriceZoneOption.BOTH -> {
                    if (!hasStandard) createPriceZonesForFestival(id, PriceZoneOption.STANDARD)
                    if (!hasVip) createPriceZonesForFestival(id, PriceZoneOption.VIP)
                }
            }
            festivalRepository.refreshFestivals()
        }
    }

    fun deleteFestival(id: Int) {
        viewModelScope.launch {
            festivalRepository.deleteFestival(id)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                FestivalViewModel(
                    application.container.festivalRepository,
                    application.container.priceZoneRepository
                )
            }
        }
    }
}
