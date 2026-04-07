package com.example.gamefest.data.mapper

import com.example.gamefest.data.local.entity.ReservationEntity
import com.example.gamefest.data.local.entity.ReservationGameEntity
import com.example.gamefest.data.local.entity.ZoneReservationEntity
import com.example.gamefest.data.remote.dto.FestivalGameDto
import com.example.gamefest.data.remote.dto.ReservationDto
import com.example.gamefest.data.remote.dto.ZoneReservationDto

// DTO -> Entity
fun ReservationDto.toEntity(): ReservationEntity {
    return ReservationEntity(
        id = this.reservationId,
        festivalId = this.festivalId,
        publisherId = this.gamePublisherId ?: 0,
        publisherName = this.publisher?.name,
        status = this.status,
        invoiceStatus = this.invoiceStatus,
        isPublisherPresenting = this.isPublisherPresenting,
        comments = this.comments
    )
}

fun ZoneReservationDto.toEntity(): ZoneReservationEntity {
    return ZoneReservationEntity(
        id = this.id,
        reservationId = this.reservationId,
        priceZoneId = this.priceZoneId,
        tableCount = this.tableCount
    )
}

fun List<ReservationDto>.toEntityList(): List<ReservationEntity> {
    return this.map { it.toEntity() }
}

fun List<ReservationDto>.toZoneEntityList(): List<ZoneReservationEntity> {
    return this.flatMap { reservation ->
        reservation.zones?.map { it.toEntity() } ?: emptyList()
    }
}

fun FestivalGameDto.toEntity(reservationId: Int): ReservationGameEntity {
    return ReservationGameEntity(
        id = id,
        reservationId = reservationId,
        gameId = gameId,
        gameName = game?.name,
        mapZoneId = mapZoneId,
        mapZoneName = mapZone?.name,
        mapZonePriceZoneId = mapZone?.priceZoneId,
        copyCount = copyCount,
        allocatedTables = allocatedTables
    )
}

fun List<ReservationDto>.toReservationGameEntityList(): List<ReservationGameEntity> {
    return this.flatMap { reservation ->
        reservation.games?.map { it.toEntity(reservation.reservationId) } ?: emptyList()
    }
}
