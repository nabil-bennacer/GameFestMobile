package com.example.gamefest.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "games",
    foreignKeys = [
        ForeignKey(
            entity = PublisherEntity::class,
            parentColumns = ["id"],
            childColumns = ["publisherId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["publisherId"])]
)
data class GameEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: String,
    val minAge: Int,
    val imageUrl: String?,
    val publisherId: Int,
    val maxPlayers: Int
)
