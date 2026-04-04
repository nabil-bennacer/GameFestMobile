package com.example.gamefest.data.mapper

import com.example.gamefest.data.local.entity.MapZoneEntity
import com.example.gamefest.data.local.entity.PriceZoneEntity
import com.example.gamefest.data.local.entity.TableTypeEntity
import com.example.gamefest.data.remote.dto.MapZoneDto
import com.example.gamefest.data.remote.dto.PriceZoneDto
import com.example.gamefest.data.remote.dto.TableTypeDto

fun PriceZoneDto.toEntity(): PriceZoneEntity {
    return PriceZoneEntity(
        id = id,
        festivalId = festivalId,
        name = name,
        tablePrice = tablePrice
    )
}

fun TableTypeDto.toEntity(): TableTypeEntity {
    return TableTypeEntity(
        id = id,
        name = name,
        nbTotal = nbTotal,
        nbAvailable = nbAvailable,
        nbTotalPlayer = nbTotalPlayer,
        priceZoneId = priceZoneId
    )
}

fun MapZoneDto.toEntity(): MapZoneEntity {
    return MapZoneEntity(
        id = id,
        festivalId = festivalId,
        priceZoneId = priceZoneId,
        name = name,
        smallTables = smallTables,
        largeTables = largeTables,
        cityTables = cityTables
    )
}
