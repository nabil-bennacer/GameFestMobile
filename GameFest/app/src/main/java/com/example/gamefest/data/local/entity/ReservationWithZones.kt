package com.example.gamefest.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ReservationWithZones(
    @Embedded val reservation: ReservationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "reservationId"
    )
    val zones: List<ZoneReservationEntity>
)
