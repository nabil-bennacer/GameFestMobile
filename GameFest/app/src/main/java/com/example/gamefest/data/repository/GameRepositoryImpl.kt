package com.example.gamefest.data.repository

import com.example.gamefest.data.local.dao.GameDao
import com.example.gamefest.data.local.entity.GameEntity
import com.example.gamefest.data.mapper.toEntity
import com.example.gamefest.data.mapper.toEntityList
import com.example.gamefest.data.remote.GameFestApiService
import com.example.gamefest.data.remote.dto.GameDto
import kotlinx.coroutines.flow.Flow

class GameRepositoryImpl(
    private val dao: GameDao,
    private val api: GameFestApiService
) : GameRepository {

    override fun getAllGames(): Flow<List<GameEntity>> = dao.getAllGames()

    override fun getGamesByPublisher(publisherId: Int): Flow<List<GameEntity>> = dao.getGamesByPublisher(publisherId)

    override fun getGameById(id: Int): Flow<GameEntity?> = dao.getGameById(id)

    override suspend fun refreshGames() {
        try {
            val response = api.getAllGames()
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    dao.insertGames(dtos.toEntityList())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun addGame(game: GameDto) {
        dao.insertGame(game.toEntity())
        try {
            api.createGame(game)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteGame(id: Int) {
        dao.deleteGameById(id)
        try {
            api.deleteGame(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}