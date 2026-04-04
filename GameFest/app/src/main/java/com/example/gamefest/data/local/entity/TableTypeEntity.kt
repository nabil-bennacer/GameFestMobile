package com.example.gamefest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "table_types",
    foreignKeys = [
        ForeignKey(
            entity = PriceZoneEntity::class,
            parentColumns = ["id"],
            childColumns = ["priceZoneId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TableTypeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val nbTotal: Double,
    val nbAvailable: Double,
    val nbTotalPlayer: Int,
    val priceZoneId: Int
)
