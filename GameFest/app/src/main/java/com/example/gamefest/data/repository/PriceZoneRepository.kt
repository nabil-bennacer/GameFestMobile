package com.example.gamefest.data.repository

import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.data.remote.dto.PriceZoneRequest
import kotlinx.coroutines.flow.Flow

interface PriceZoneRepository {
    fun getPriceZonesForFestival(festivalId: Int): Flow<List<PriceZoneWithDetails>>

    suspend fun refreshPriceZones(festivalId: Int)
    suspend fun createPriceZone(request: PriceZoneRequest)
    suspend fun updatePriceZone(id: Int, updateMap: Map<String, Any>)
    suspend fun deletePriceZone(id: Int)

    suspend fun addMapZone(festivalId: Int, priceZoneId: Int, name: String, tablesCount: Int)
}

