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
import com.example.gamefest.data.local.entity.*
import com.example.gamefest.data.remote.dto.*
import com.example.gamefest.data.repository.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ZoneSelection(
    val priceZoneId: String = "",
    val tableCount: String = ""
)

data class GameSelection(
    val gameId: String = "",
    val mapZoneId: String = "",
    val allocatedTables: String = "1",
    val copyCount: String = "1"
)

data class ReservationDetails(
    val publisherId: String = "",
    val festivalId: String = "",
    val selectedZones: List<ZoneSelection> = listOf(ZoneSelection()),
    val selectedGames: List<GameSelection> = emptyList()
)

class ReservationEntryViewModel(
    private val reservationRepository: ReservationRepository,
    private val publisherRepository: PublisherRepository,
    private val festivalRepository: FestivalRepository,
    private val priceZoneRepository: PriceZoneRepository,
    private val gameRepository: GameRepository,
    initialFestivalId: Int? = null
) : ViewModel() {

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    var details by mutableStateOf(ReservationDetails(festivalId = initialFestivalId?.toString() ?: ""))
        private set

    val publishers: StateFlow<List<PublisherEntity>> = publisherRepository.getAllPublishers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val festivals: StateFlow<List<FestivalEntity>> = festivalRepository.getAllFestivals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGames: StateFlow<List<GameEntity>> = gameRepository.getAllGames()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredGames: List<GameEntity>
        get() {
            val pubId = details.publisherId.toIntOrNull()
            val games = allGames.value
            return if (pubId != null) games.filter { it.publisherId == pubId } else games
        }
    
    fun getAvailableTables(priceZoneId: Int): Int {
        val zone = priceZonesWithDetails.find { it.priceZone.id == priceZoneId }
        return zone?.tableTypes?.sumOf { it.nbAvailable.toInt() } ?: 0
    }

    fun getTotalTables(priceZoneId: Int): Int {
        val zone = priceZonesWithDetails.find { it.priceZone.id == priceZoneId }
        return zone?.tableTypes?.sumOf { it.nbTotal.toInt() } ?: 0
    }

    var priceZonesWithDetails by mutableStateOf<List<PriceZoneWithDetails>>(emptyList())
        private set

    val allMapZones: List<MapZoneEntity>
        get() = priceZonesWithDetails.flatMap { it.mapZones }

    init {
        viewModelScope.launch { publisherRepository.refreshPublishers() }
        viewModelScope.launch { festivalRepository.refreshFestivals() }
        viewModelScope.launch { gameRepository.refreshGames() }
        if (initialFestivalId != null) loadPriceZones(initialFestivalId)
    }

    fun updatePublisher(publisherId: String) {
        details = details.copy(publisherId = publisherId)
    }

    fun updateFestival(festivalId: String) {
        details = details.copy(
            festivalId = festivalId,
            selectedZones = listOf(ZoneSelection()),
            selectedGames = emptyList()
        )
        val id = festivalId.toIntOrNull()
        if (id != null) loadPriceZones(id) else priceZonesWithDetails = emptyList()
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
    fun addZone() { details = details.copy(selectedZones = details.selectedZones + ZoneSelection()) }
    fun removeZone(index: Int) {
        if (details.selectedZones.size > 1) {
            val updatedZones = details.selectedZones.toMutableList()
            updatedZones.removeAt(index)
            details = details.copy(selectedZones = updatedZones)
        }
    }

    fun updateGame(index: Int, game: GameSelection) {
        val updatedGames = details.selectedGames.toMutableList()
        if (index in updatedGames.indices) {
            updatedGames[index] = game
            details = details.copy(selectedGames = updatedGames)
        }
    }
    fun addGame() { details = details.copy(selectedGames = details.selectedGames + GameSelection()) }
    fun removeGame(index: Int) {
        val updatedGames = details.selectedGames.toMutableList()
        updatedGames.removeAt(index)
        details = details.copy(selectedGames = updatedGames)
    }

    val totalPrice: Double
        get() {
            val priceZoneMap = priceZonesWithDetails.associate { it.priceZone.id to it.priceZone.tablePrice }
            return details.selectedZones.sumOf { zone ->
                val priceZoneId = zone.priceZoneId.toIntOrNull()
                val tableCount = zone.tableCount.toIntOrNull() ?: 0
                (priceZoneMap[priceZoneId] ?: 0.0) * tableCount
            }
        }

    val totalReservedTables: Float
        get() = details.selectedZones.sumOf { (it.tableCount.toIntOrNull() ?: 0).toDouble() }.toFloat()

    val totalPlacedTables: Float
        get() = details.selectedGames
            .filter { it.mapZoneId.isNotBlank() }
            .sumOf { (it.allocatedTables.toFloatOrNull() ?: 0f).toDouble() }
            .toFloat()

    val hasPlacementOverflow: Boolean
        get() = totalPlacedTables > totalReservedTables

    val isFormValid: Boolean
        get() {
            if (details.publisherId.isBlank() || details.festivalId.isBlank()) return false
            val zonesValid = details.selectedZones.all {
                it.priceZoneId.isNotBlank() && it.tableCount.isNotBlank() && (it.tableCount.toIntOrNull() ?: 0) > 0
            }
            val gamesValid = details.selectedGames.all {
                it.gameId.isNotBlank() && it.allocatedTables.isNotBlank()
            }
            return zonesValid && gamesValid && !hasPlacementOverflow
        }

    fun saveReservation(onComplete: (Boolean) -> Unit = {}) {
        if (!isFormValid || _isSaving.value) {
            onComplete(false)
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            val publisherIdInt = details.publisherId.toInt()

            val dto = ReservationDto(
                reservationId = 0,
                reservantId = publisherIdInt,
                gamePublisherId = publisherIdInt,
                festivalId = details.festivalId.toInt(),
                status = "CONFIRMED",
                invoiceStatus = "PENDING",
                zones = details.selectedZones.map { zone ->
                    ZoneReservationDto(
                        id = 0, reservationId = 0,
                        priceZoneId = zone.priceZoneId.toInt(),
                        tableCount = zone.tableCount.toInt()
                    )
                },
                games = details.selectedGames.map { game ->
                    FestivalGameDto(
                        gameId = game.gameId.toInt(),
                        mapZoneId = game.mapZoneId.toIntOrNull(),
                        copyCount = game.copyCount.toIntOrNull() ?: 1,
                        allocatedTables = game.allocatedTables.toFloatOrNull() ?: 1f
                    )
                }
            )

            val saved = reservationRepository.saveReservation(dto)
            if (saved) {
                // Rafraîchir les données pour mettre à jour les tables restantes
                priceZoneRepository.refreshPriceZones(details.festivalId.toInt())
            }
            _isSaving.value = false
            onComplete(saved)
        }
    }

    companion object {
        fun provideFactory(initialFestivalId: Int? = null): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                ReservationEntryViewModel(
                    reservationRepository = application.container.reservationRepository,
                    publisherRepository = application.container.publisherRepository,
                    festivalRepository = application.container.festivalRepository,
                    priceZoneRepository = application.container.priceZoneRepository,
                    gameRepository = application.container.gameRepository,
                    initialFestivalId = initialFestivalId
                )
            }
        }
    }
}