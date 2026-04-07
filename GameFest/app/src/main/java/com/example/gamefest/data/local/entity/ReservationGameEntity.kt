package com.example.gamefest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservation_games",
    foreignKeys = [
        ForeignKey(
            entity = ReservationEntity::class,
            parentColumns = ["id"],
            childColumns = ["reservationId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReservationGameEntity(
    @PrimaryKey
    val id: Int,
    val reservationId: Int,
    val gameId: Int,
    val gameName: String?,
    val mapZoneId: Int?,
    val mapZoneName: String?,
    val mapZonePriceZoneId: Int?,
    val copyCount: Int,
    val allocatedTables: Float
)