package com.example.gamefest.data.repository

import com.example.gamefest.data.local.entity.MapZoneEntity
import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.data.remote.dto.PriceZoneRequest
import kotlinx.coroutines.flow.Flow

interface PriceZoneRepository {
    fun getPriceZonesForFestival(festivalId: Int): Flow<List<PriceZoneWithDetails>>
    fun getMapZonesByPriceZone(priceZoneId: Int): Flow<List<MapZoneEntity>>

    suspend fun refreshPriceZones(festivalId: Int)
    suspend fun createPriceZone(request: PriceZoneRequest)
    suspend fun updatePriceZone(id: Int, updateMap: Map<String, Any>)
    suspend fun deletePriceZone(id: Int)
}
