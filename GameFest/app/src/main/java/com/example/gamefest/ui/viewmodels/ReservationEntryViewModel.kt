package com.example.gamefest.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.entity.MapZoneEntity
import com.example.gamefest.data.local.entity.PriceZoneEntity
import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.data.local.entity.FestivalEntity
import com.example.gamefest.data.remote.dto.ReservationDto
import com.example.gamefest.data.remote.dto.ZoneReservationDto
import com.example.gamefest.data.repository.FestivalRepository
import com.example.gamefest.data.repository.PriceZoneRepository
import com.example.gamefest.data.repository.PublisherRepository
import com.example.gamefest.data.repository.ReservationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// --- Data classes pour l'état du formulaire ---

data class ZoneSelection(
    val priceZoneId: String = "",
    val mapZoneId: String = "",
    val tableCount: String = ""
)

data class ReservationDetails(
    val publisherId: String = "",
    val festivalId: String = "",
    val selectedZones: List<ZoneSelection> = listOf(ZoneSelection())
)

class ReservationEntryViewModel(
    private val reservationRepository: ReservationRepository,
    private val publisherRepository: PublisherRepository,
    private val festivalRepository: FestivalRepository,
    private val priceZoneRepository: PriceZoneRepository,
    initialFestivalId: Int? = null
) : ViewModel() {

    var details by mutableStateOf(
        ReservationDetails(
            festivalId = initialFestivalId?.toString() ?: ""
        )
    )
        private set

    val publishers: StateFlow<List<PublisherEntity>> =
        publisherRepository.getAllPublishers()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val festivals: StateFlow<List<FestivalEntity>> =
        festivalRepository.getAllFestivals()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var priceZonesWithDetails by mutableStateOf<List<PriceZoneWithDetails>>(emptyList())
        private set

    init {
        viewModelScope.launch { publisherRepository.refreshPublishers() }
        viewModelScope.launch { festivalRepository.refreshFestivals() }
        // Si un festivalId initial est fourni, charger les zones
        if (initialFestivalId != null) {
            loadPriceZones(initialFestivalId)
        }
    }

    fun updatePublisher(publisherId: String) {
        details = details.copy(publisherId = publisherId)
    }

    fun updateFestival(festivalId: String) {
        details = details.copy(
            festivalId = festivalId,
            selectedZones = listOf(ZoneSelection()) // reset les zones
        )
        val id = festivalId.toIntOrNull()
        if (id != null) {
            loadPriceZones(id)
        } else {
            priceZonesWithDetails = emptyList()
        }
    }

    private fun loadPriceZones(festivalId: Int) {
        viewModelScope.launch {
            priceZoneRepository.refreshPriceZones(festivalId)
            priceZoneRepository.getPriceZonesForFestival(festivalId).collect {
                priceZonesWithDetails = it
            }
        }
    }

    fun updateZone(index: Int, zone: ZoneSelection) {
        val updatedZones = details.selectedZones.toMutableList()
        if (index in updatedZones.indices) {
            updatedZones[index] = zone
            details = details.copy(selectedZones = updatedZones)
        }
    }

    fun addZone() {
        details = details.copy(
            selectedZones = details.selectedZones + ZoneSelection()
        )
    }

    fun removeZone(index: Int) {
        if (details.selectedZones.size > 1) {
            val updatedZones = details.selectedZones.toMutableList()
            updatedZones.removeAt(index)
            details = details.copy(selectedZones = updatedZones)
        }
    }

    val totalPrice: Double
        get() {
            val priceZoneMap = priceZonesWithDetails.associate { it.priceZone.id to it.priceZone.tablePrice }
            return details.selectedZones.sumOf { zone ->
                val priceZoneId = zone.priceZoneId.toIntOrNull()
                val tableCount = zone.tableCount.toIntOrNull() ?: 0
                val unitPrice = priceZoneMap[priceZoneId] ?: 0.0
                unitPrice * tableCount
            }
        }

    fun getFilteredMapZones(priceZoneId: String): List<MapZoneEntity> {
        val id = priceZoneId.toIntOrNull() ?: return emptyList()
        return priceZonesWithDetails
            .firstOrNull { it.priceZone.id == id }
            ?.mapZones ?: emptyList()
    }

    val isFormValid: Boolean
        get() {
            if (details.publisherId.isBlank() || details.festivalId.isBlank()) return false
            return details.selectedZones.all { zone ->
                zone.priceZoneId.isNotBlank() &&
                zone.tableCount.isNotBlank() &&
                (zone.tableCount.toIntOrNull() ?: 0) > 0
            }
        }

    suspend fun saveReservation() {
        if (!isFormValid) return

        val dto = ReservationDto(
            gamePublisherId = details.publisherId.toIntOrNull(),
            festivalId = details.festivalId.toInt(),
            tables = details.selectedZones.map { zone ->
                ZoneReservationDto(
                    priceZoneId = zone.priceZoneId.toInt(),
                    tableCount = zone.tableCount.toInt()
                )
            }
        )
        reservationRepository.saveReservation(dto)
    }

    companion object {
        fun provideFactory(
            initialFestivalId: Int? = null
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                ReservationEntryViewModel(
                    reservationRepository = application.container.reservationRepository,
                    publisherRepository = application.container.publisherRepository,
                    festivalRepository = application.container.festivalRepository,
                    priceZoneRepository = application.container.priceZoneRepository,
                    initialFestivalId = initialFestivalId
                )
            }
        }
    }
}