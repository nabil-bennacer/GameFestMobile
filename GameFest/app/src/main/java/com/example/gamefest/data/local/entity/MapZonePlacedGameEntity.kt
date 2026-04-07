package com.example.gamefest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "map_zone_placed_games")
data class MapZonePlacedGameEntity(
    @PrimaryKey
    val id: Int,
    val priceZoneId: Int,
    val mapZoneId: Int,
    val mapZoneName: String,
    val gameName: String,
    val publisherName: String,
    val allocatedTables: Double
)
