package com.example.gamefest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReservationCreateRequest(
    @SerializedName("game_publisher_id") val gamePublisherId: Int,
    @SerializedName("festival_id") val festivalId: Int,
    @SerializedName("publisher_is_reservant") val publisherIsReservant: Boolean = true,
    val status: String = "CONFIRMED",
    val comments: String? = null,
    val tables: List<ReservationTableRequest>
)

data class ReservationTableRequest(
    @SerializedName("price_zone_id") val priceZoneId: Int,
    @SerializedName("table_count") val tableCount: Int
)