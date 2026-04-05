package com.example.gamefest.data.local.dao

import androidx.room.*
import com.example.gamefest.data.local.entity.MapZoneEntity
import com.example.gamefest.data.local.entity.PriceZoneEntity
import com.example.gamefest.data.local.entity.PriceZoneWithDetails
import com.example.gamefest.data.local.entity.TableTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceZoneDao {
    @Transaction
    @Query("SELECT * FROM price_zones WHERE festivalId = :festivalId")
    fun getPriceZonesForFestival(festivalId: Int): Flow<List<PriceZoneWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceZones(priceZones: List<PriceZoneEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTableTypes(tableTypes: List<TableTypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapZones(mapZones: List<MapZoneEntity>)

    @Query("SELECT * FROM map_zones WHERE priceZoneId = :priceZoneId")
    fun getMapZonesByPriceZone(priceZoneId: Int): Flow<List<MapZoneEntity>>

    @Query("DELETE FROM price_zones WHERE id = :id")
    suspend fun deletePriceZoneById(id: Int)
}
