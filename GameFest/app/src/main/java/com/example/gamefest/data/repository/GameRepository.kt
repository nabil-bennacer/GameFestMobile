package com.example.gamefest.data.repository

import com.example.gamefest.data.local.entity.GameEntity
import com.example.gamefest.data.remote.dto.GameDto
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getAllGames(): Flow<List<GameEntity>>
    fun getGamesByPublisher(publisherId: Int): Flow<List<GameEntity>>
    fun getGameById(id: Int): Flow<GameEntity?>

    suspend fun refreshGames()
    suspend fun addGame(game: GameDto)
    suspend fun deleteGame(id: Int)
}