package com.example.gamefest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey
    val id: Int,
    val festivalId: Int,
    val publisherId: Int,
    val publisherName: String?,
    val status: String,
    val invoiceStatus: String,
    val isPublisherPresenting: Boolean,
    val comments: String?
)
