package com.example.gamefest.data.repository

import android.util.Log
import com.example.gamefest.data.local.dao.ReservationDao
import com.example.gamefest.data.local.entity.ReservationWithZones
import com.example.gamefest.data.mapper.toEntity
import com.example.gamefest.data.mapper.toEntityList
import com.example.gamefest.data.mapper.toZoneEntityList
import com.example.gamefest.data.remote.GameFestApiService
import com.example.gamefest.data.remote.dto.ReservationDto
import kotlinx.coroutines.flow.Flow

class ReservationRepositoryImpl(
    private val dao: ReservationDao,
    private val api: GameFestApiService
) : ReservationRepository {


    override fun getAllReservations(): Flow<List<ReservationWithZones>> =
        dao.getAllReservations()

    override suspend fun refreshReservations() {
        try {
            val response = api.getAllReservations()
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    // Insérer les réservations
                    dao.insertReservations(dtos.toEntityList())
                    // Supprimer et ré-insérer les zones pour chaque réservation
                    dao.deleteAllZones()
                    dao.insertZoneReservations(dtos.toZoneEntityList())
                }
            }
        } catch (e: Exception) {
            Log.e("ReservationRepository", "Impossible de rafraîchir les réservations", e)
        }
    }


    override suspend fun saveReservation(reservationDto: ReservationDto) {
        // Sauvegarder localement d'abord
        dao.insertReservation(reservationDto.toEntity())
        reservationDto.zones?.let { zones ->
            dao.insertZoneReservations(zones.map { it.toEntity() })
        }

        try {
            api.createReservation(reservationDto)
        } catch (e: Exception) {
            Log.e("ReservationRepository", "Erreur lors de l'envoi au serveur", e)
        }
    }
}
