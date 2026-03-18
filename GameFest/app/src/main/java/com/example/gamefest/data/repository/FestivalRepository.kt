package com.example.gamefest.data.repository

import com.example.gamefest.data.local.entity.FestivalEntity
import com.example.gamefest.data.remote.dto.FestivalDto
import kotlinx.coroutines.flow.Flow

interface FestivalRepository {
    fun getAllFestivals(): Flow<List<FestivalEntity>>
    fun getFestivalById(id: Int): Flow<FestivalEntity?>

    suspend fun refreshFestivals()
    suspend fun addFestival(festival: FestivalDto)
    suspend fun deleteFestival(id: Int)
}
