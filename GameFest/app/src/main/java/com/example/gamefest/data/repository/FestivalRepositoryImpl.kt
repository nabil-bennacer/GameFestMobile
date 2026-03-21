package com.example.gamefest.data.repository

import android.util.Log
import com.example.gamefest.data.local.dao.FestivalDao
import com.example.gamefest.data.local.entity.FestivalEntity
import com.example.gamefest.data.mapper.toEntity
import com.example.gamefest.data.mapper.toEntityList
import com.example.gamefest.data.remote.GameFestApiService
import com.example.gamefest.data.remote.dto.FestivalDto
import kotlinx.coroutines.flow.Flow

class FestivalRepositoryImpl(
    private val dao: FestivalDao,
    private val api: GameFestApiService
) : FestivalRepository {

    override fun getAllFestivals(): Flow<List<FestivalEntity>> = dao.getAllFestivals()

    override fun getFestivalById(id: Int): Flow<FestivalEntity?> = dao.getFestivalById(id)

    override suspend fun refreshFestivals() {
        try {
            val response = api.getAllFestivals()
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    dao.insertAll(dtos.toEntityList())
                }
            }
        } catch (e: Exception) {
            Log.e("FestivalRepository", "Error refreshing festivals", e)
        }
    }

    override suspend fun addFestival(festival: FestivalDto) {
        dao.insertAll(listOf(festival.toEntity()))
        try {
            api.createFestival(festival)
        } catch (e: Exception) {
            Log.e("FestivalRepository", "Error adding festival to remote", e)
        }
    }

    override suspend fun deleteFestival(id: Int) {
        dao.deleteFestivalById(id)
        try {
            api.deleteFestival(id)
        } catch (e: Exception) {
            Log.e("FestivalRepository", "Error deleting festival from remote", e)
        }
    }
}
