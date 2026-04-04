package com.example.gamefest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "map_zones",
    foreignKeys = [
        ForeignKey(
            entity = FestivalEntity::class,
            parentColumns = ["id"],
            childColumns = ["festivalId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PriceZoneEntity::class,
            parentColumns = ["id"],
            childColumns = ["priceZoneId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class MapZoneEntity(
    @PrimaryKey val id: Int,
    val festivalId: Int,
    val priceZoneId: Int?,
    val name: String,
    val smallTables: Int,
    val largeTables: Int,
    val cityTables: Int
)
