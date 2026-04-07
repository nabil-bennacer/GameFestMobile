package com.example.gamefest.data.remote.dto

import com.google.gson.annotations.SerializedName

// pour envoyer au backend
data class MapZoneCreateDto(
    @SerializedName("festival_id") val festivalId: Int,
    @SerializedName("price_zone_id") val priceZoneId: Int,
    val name: String,
    @SerializedName("small_tables") val smallTables: Int,
    @SerializedName("large_tables") val largeTables: Int = 0,
    @SerializedName("city_tables") val cityTables: Int = 0
)