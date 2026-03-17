package com.example.gamefest.data.remote.dto

data class GameDto(
    val id: Int,
    val name: String,
    val type: String,
    val minAge: Int,
    val imageUrl: String?,
    val publisherId: Int,
    val maxPlayers: Int
)
