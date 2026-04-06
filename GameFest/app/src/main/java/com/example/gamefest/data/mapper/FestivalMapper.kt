package com.example.gamefest.data.mapper

import com.example.gamefest.data.local.entity.FestivalEntity
import com.example.gamefest.data.remote.dto.FestivalDto

fun FestivalDto.toEntity(): FestivalEntity {
    return FestivalEntity(
        id = this.id,
        name = this.name,
        location = this.location,
        startDate = this.startDate,
        endDate = this.endDate,
        tablesCount = this.tablesCount ?: 0,
        priceZoneTypeId = this.priceZoneTypeId ?:0
    )
}

fun List<FestivalDto>.toEntityList(): List<FestivalEntity> {
    return this.map { it.toEntity() }
}
