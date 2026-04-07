package com.example.gamefest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReservationDto(
    @SerializedName("reservation_id") val reservationId: Int = 0,
    @SerializedName("reservant_id") val reservantId: Int,
    @SerializedName("game_publisher_id") val gamePublisherId: Int?,
    @SerializedName("festival_id") val festivalId: Int,
    val status: String = "CONFIRMED",
    @SerializedName("invoice_status") val invoiceStatus: String = "PENDING",
    @SerializedName("is_publisher_presenting") val isPublisherPresenting: Boolean = false,
    val comments: String? = null,
    val publisher: ReservationPublisherDto? = null,
    val zones: List<ZoneReservationDto>? = null,
    val games: List<FestivalGameDto>? = null
)

data class ReservationPublisherDto(
    val id: Int,
    val name: String
)
