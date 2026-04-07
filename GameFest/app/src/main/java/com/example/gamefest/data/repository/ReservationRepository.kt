package com.example.gamefest.data.repository

import com.example.gamefest.data.local.entity.ReservationWithZones
import com.example.gamefest.data.remote.dto.ReservationDto
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun getAllReservations(): Flow<List<ReservationWithZones>>
    suspend fun refreshReservations()
    suspend fun saveReservation(reservationDto: ReservationDto): Boolean
    suspend fun deleteReservation(reservationId: Int): Boolean
}
