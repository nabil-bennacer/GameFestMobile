package com.example.gamefest.data.mapper

import com.example.gamefest.data.local.entity.GameEntity
import com.example.gamefest.data.remote.dto.GameDto

fun GameDto.toEntity(): GameEntity {
    return GameEntity(
        id = this.id,
        name = this.name,
        type = this.type,
        minAge = this.minAge,
        imageUrl = this.imageUrl,
        publisherId = this.publisherId,
        maxPlayers = this.maxPlayers
    )
}

fun List<GameDto>.toEntityList(): List<GameEntity> {
    return this.map { it.toEntity() }
}