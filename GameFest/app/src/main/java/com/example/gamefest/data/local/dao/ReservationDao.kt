package com.example.gamefest.data.local.dao

import androidx.room.*
import com.example.gamefest.data.local.entity.ReservationEntity
import com.example.gamefest.data.local.entity.ReservationWithZones
import com.example.gamefest.data.local.entity.ZoneReservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    @Transaction
    @Query("SELECT * FROM reservations")
    fun getAllReservations(): Flow<List<ReservationWithZones>>

    @Transaction
    @Query("SELECT * FROM reservations WHERE festivalId = :festivalId")
    fun getReservationsByFestival(festivalId: Int): Flow<List<ReservationWithZones>>

    @Transaction
    @Query("SELECT * FROM reservations WHERE id = :id")
    fun getReservationById(id: Int): Flow<ReservationWithZones?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservations(reservations: List<ReservationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZoneReservations(zones: List<ZoneReservationEntity>)

    @Query("DELETE FROM zone_reservations WHERE reservationId = :reservationId")
    suspend fun deleteZonesByReservationId(reservationId: Int)

    @Query("DELETE FROM reservations WHERE id = :id")
    suspend fun deleteReservationById(id: Int)

    @Query("DELETE FROM reservations")
    suspend fun deleteAll()

    @Query("DELETE FROM zone_reservations")
    suspend fun deleteAllZones()
}
