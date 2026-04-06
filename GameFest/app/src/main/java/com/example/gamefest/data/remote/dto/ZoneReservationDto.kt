package com.example.gamefest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ZoneReservationDto(
    val id: Int = 0,
    @SerializedName("reservation_id") val reservationId: Int = 0,
    @SerializedName("price_zone_id") val priceZoneId: Int,
    @SerializedName("table_count") val tableCount: Int
)
