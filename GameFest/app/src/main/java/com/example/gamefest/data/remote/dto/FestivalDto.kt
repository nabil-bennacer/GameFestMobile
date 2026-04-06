package com.example.gamefest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FestivalDto(
    val id: Int,
    val name: String,
    val location: String?,
    val startDate: String?,
    val endDate: String?,
    @SerializedName("small_tables") val tablesCount: Int? = null,
    val priceZoneTypeId: Int? = null,
    val priceZones: List<PriceZoneDto>? = null
)
