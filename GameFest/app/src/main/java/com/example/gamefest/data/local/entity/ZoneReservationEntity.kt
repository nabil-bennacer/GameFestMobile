package com.example.gamefest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "zone_reservations",
    foreignKeys = [
        ForeignKey(
            entity = ReservationEntity::class,
            parentColumns = ["id"],
            childColumns = ["reservationId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ZoneReservationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reservationId: Int,
    val priceZoneId: Int,
    val tableCount: Int
)
