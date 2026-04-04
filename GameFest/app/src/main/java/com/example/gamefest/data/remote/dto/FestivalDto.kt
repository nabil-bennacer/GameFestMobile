package com.example.gamefest.data.remote.dto

data class FestivalDto(
    val id: Int,
    val name: String,
    val location: String?,
    val startDate: String?,
    val endDate: String?,
    val priceZoneTypeId: Int? = null
)
