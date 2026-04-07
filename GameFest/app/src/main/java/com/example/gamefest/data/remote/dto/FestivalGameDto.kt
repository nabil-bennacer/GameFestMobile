package com.example.gamefest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FestivalGameDto(
    val id: Int = 0,
    @SerializedName("game_id") val gameId: Int,
    @SerializedName("map_zone_id") val mapZoneId: Int?,
    @SerializedName("copy_count") val copyCount: Int,
    @SerializedName("allocated_tables") val allocatedTables: Float,
    val game: ReservationGameSnapshotDto? = null,
    @SerializedName("mapZone") val mapZone: ReservationMapZoneSnapshotDto? = null
)

data class ReservationGameSnapshotDto(
    val id: Int,
    val name: String
)

data class ReservationMapZoneSnapshotDto(
    val id: Int,
    val name: String,
    @SerializedName("price_zone_id") val priceZoneId: Int?
)