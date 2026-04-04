package com.example.gamefest.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PriceZoneWithDetails(
    @Embedded val priceZone: PriceZoneEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "priceZoneId"
    )
    val tableTypes: List<TableTypeEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "priceZoneId"
    )
    val mapZones: List<MapZoneEntity>
)
