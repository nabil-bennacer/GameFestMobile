package com.example.gamefest.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gamefest.GameFestApplication
import com.example.gamefest.data.local.dao.PriceZoneDao
import com.example.gamefest.data.local.entity.MapZoneEntity
import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.data.local.entity.ReservationWithZones
import com.example.gamefest.data.repository.GameRepository
import com.example.gamefest.data.repository.PublisherRepository
import com.example.gamefest.data.repository.ReservationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReservationListViewModel(
    private val reservationRepository: ReservationRepository,
    private val publisherRepository: PublisherRepository,
    private val priceZoneDao: PriceZoneDao,
    private val gameRepository: GameRepository
) : ViewModel() {

    val reservations: StateFlow<List<ReservationWithZones>> =
        reservationRepository.getAllReservations()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val publishers: StateFlow<List<PublisherEntity>> =
        publisherRepository.getAllPublishers()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val priceZonesWithDetails: StateFlow<List<PriceZoneWithDetails>> =
        priceZoneDao.getAllPriceZones()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun getPublisherName(publisherId: Int): String {
        return publishers.value.find { it.id == publisherId }?.name ?: "Éditeur #$publisherId"
    }

    fun getGameName(gameId: Int): String {
        return allGames.value.find { it.id == gameId }?.name ?: "Jeu #$gameId"
    }

    fun getMapZone(mapZoneId: Int?): MapZoneEntity? {
        if (mapZoneId == null) return null
        return priceZonesWithDetails.value.flatMap { it.mapZones }.find { it.id == mapZoneId }
    }

    fun getPriceZoneName(priceZoneId: Int?): String {
        if (priceZoneId == null) return "Zone tarifaire inconnue"
        return priceZonesWithDetails.value
            .find { it.priceZone.id == priceZoneId }
            ?.priceZone
            ?.name
            ?: "Zone #$priceZoneId"
    }

    val allGames = gameRepository.getAllGames()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            reservationRepository.refreshReservations()
        }
        viewModelScope.launch {
            publisherRepository.refreshPublishers()
        }
        viewModelScope.launch {
            gameRepository.refreshGames()
        }
    }

    fun deleteReservation(reservationId: Int) {
        viewModelScope.launch {
            reservationRepository.deleteReservation(reservationId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameFestApplication)
                ReservationListViewModel(
                    application.container.reservationRepository,
                    application.container.publisherRepository,
                    application.container.database.priceZoneDao(),
                    application.container.gameRepository
                )
            }
        }
    }
}
