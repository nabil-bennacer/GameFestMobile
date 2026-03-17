package com.example.gamefest.data.mapper

import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.data.remote.dto.PublisherDto

// Va transformer nos Dto en Entities
fun PublisherDto.toEntity(): PublisherEntity {
    return PublisherEntity(
        id = this.id,
        name = this.name,
        logoUrl = this.logoUrl,
        exposant = this.exposant,
        distributeur = this.distributeur
    )
}


fun List<PublisherDto>.toEntityList(): List<PublisherEntity> {
    return this.map { it.toEntity() }
}