package com.example.gamefest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddReservationGamesRequest(
    val games: List<ReservationGameInput>
)

data class ReservationGameInput(
    @SerializedName("game_id") val gameId: Int,
    @SerializedName("copy_count") val copyCount: Int,
    @SerializedName("allocated_tables") val allocatedTables: Float? = null
)

data class PlaceGameRequest(
    @SerializedName("map_zone_id") val mapZoneId: Int,
    @SerializedName("table_size") val tableSize: String,
    @SerializedName("allocated_tables") val allocatedTables: Int
)
