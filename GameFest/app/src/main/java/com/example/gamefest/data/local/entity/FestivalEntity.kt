package com.example.gamefest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "festivals")
data class FestivalEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val year: Int,
    val location: String?
)
