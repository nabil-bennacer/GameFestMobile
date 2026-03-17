package com.example.gamefest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "publishers")
data class PublisherEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val logoUrl: String?,
    val exposant: Boolean?,
    val distributeur: Boolean?
)
