package com.example.gamefest.data.repository

import android.util.Log
import com.example.gamefest.data.local.dao.PriceZoneDao
import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.data.mapper.toEntity
import com.example.gamefest.data.remote.GameFestApiService
import com.example.gamefest.data.remote.dto.PriceZoneDto
import com.example.gamefest.data.remote.dto.PriceZoneRequest
import kotlinx.coroutines.flow.Flow

class PriceZoneRepositoryImpl(
    private val dao: PriceZoneDao,
    private val api: GameFestApiService
) : PriceZoneRepository {

    override fun getPriceZonesForFestival(festivalId: Int): Flow<List<PriceZoneWithDetails>> =
        dao.getPriceZonesForFestival(festivalId)

    override suspend fun refreshPriceZones(festivalId: Int) {
        try {
            val response = api.getPriceZonesByFestival(festivalId)
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    savePriceZonesLocally(dtos)
                }
            } else {
                Log.e("PriceZoneRepo", "Refresh failed: ${response.code()} ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("PriceZoneRepo", "Error refreshing price zones", e)
        }
    }

    override suspend fun createPriceZone(request: PriceZoneRequest) {
        try {
            Log.d("PriceZoneRepo", "Creating price zone: ${request.name} for festival ${request.festivalId}")
            val response = api.createPriceZone(request)
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    savePriceZonesLocally(listOf(dto))
                    Log.d("PriceZoneRepo", "Price zone created successfully")
                }
            } else {
                Log.e("PriceZoneRepo", "Create failed: ${response.code()} ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("PriceZoneRepo", "Error creating price zone", e)
        }
    }

    override suspend fun updatePriceZone(id: Int, updateMap: Map<String, Any>) {
        try {
            val response = api.updatePriceZone(id, updateMap)
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    savePriceZonesLocally(dtos)
                }
            }
        } catch (e: Exception) {
            Log.e("PriceZoneRepo", "Error updating price zone", e)
        }
    }

    private suspend fun savePriceZonesLocally(dtos: List<PriceZoneDto>) {
        dao.insertPriceZones(dtos.map { it.toEntity() })
        dtos.forEach { dto ->
            dto.tableTypes?.let { tableDtos ->
                dao.insertTableTypes(tableDtos.map { it.toEntity() })
            }
            dto.mapZones?.let { mapDtos ->
                dao.insertMapZones(mapDtos.map { it.toEntity() })
            }
        }
    }

    override suspend fun deletePriceZone(id: Int) {
        try {
            val response = api.deletePriceZone(id)
            if (response.isSuccessful) {
                dao.deletePriceZoneById(id)
            }
        } catch (e: Exception) {
            Log.e("PriceZoneRepo", "Error deleting price zone", e)
        }
    }
}
