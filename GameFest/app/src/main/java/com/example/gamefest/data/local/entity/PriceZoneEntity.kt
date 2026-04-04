package com.example.gamefest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "price_zones",
    foreignKeys = [
        ForeignKey(
            entity = FestivalEntity::class,
            parentColumns = ["id"],
            childColumns = ["festivalId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PriceZoneEntity(
    @PrimaryKey val id: Int,
    val festivalId: Int,
    val name: String,
    val tablePrice: Double
)
