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
import com.example.gamefest.data.repository.ReservationRepository
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
    private val priceZoneRepository: PriceZoneRepository,
    private val reservationRepository: ReservationRepository
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
                Log.d("FestivalVM", "Festival created: ${createdFestival.id}")
                // Ensure local cache has zones for the newly created festival.
                priceZoneRepository.refreshPriceZones(createdFestival.id)
                festivalRepository.refreshFestivals()
            } else {
                Log.e("FestivalVM", "Failed to create festival")
            }
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
            festivalRepository.refreshFestivals()
        }
    }

    fun deleteFestival(id: Int, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val deleted = festivalRepository.deleteFestival(id)
            if (deleted) {
                festivalRepository.refreshFestivals()
                reservationRepository.refreshReservations()
            }
            onComplete(deleted)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                FestivalViewModel(
                    application.container.festivalRepository,
                    application.container.priceZoneRepository,
                    application.container.reservationRepository
                )
            }
        }
    }
}
