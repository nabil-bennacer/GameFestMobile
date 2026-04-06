package com.example.gamefest.data.remote.dto

data class FestivalGameDto(
    val gameId: Int,
    val mapZoneId: Int?,
    val copyCount: Int,
    val allocatedTables: Float
)