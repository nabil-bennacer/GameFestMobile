package com.example.gamefest.data.repository

import android.util.Log
import com.example.gamefest.data.local.dao.ReservationDao
import com.example.gamefest.data.local.entity.ReservationWithZones
import com.example.gamefest.data.mapper.toEntityList
import com.example.gamefest.data.mapper.toReservationGameEntityList
import com.example.gamefest.data.mapper.toZoneEntityList
import com.example.gamefest.data.remote.GameFestApiService
import com.example.gamefest.data.remote.dto.AddReservationGamesRequest
import com.example.gamefest.data.remote.dto.PlaceGameRequest
import com.example.gamefest.data.remote.dto.ReservationCreateRequest
import com.example.gamefest.data.remote.dto.ReservationDto
import com.example.gamefest.data.remote.dto.ReservationGameInput
import com.example.gamefest.data.remote.dto.ReservationTableRequest
import kotlinx.coroutines.flow.Flow
import kotlin.math.ceil

class ReservationRepositoryImpl(
    private val dao: ReservationDao,
    private val api: GameFestApiService
) : ReservationRepository {


    override fun getAllReservations(): Flow<List<ReservationWithZones>> =
        dao.getAllReservations()

    override suspend fun refreshReservations() {
        try {
            val response = api.getAllReservations()
            if (response.isSuccessful) {
                response.body()?.let { dtos ->
                    // Rebuild local cache to remove stale reservations deleted on server.
                    dao.deleteAllZones()
                    dao.deleteAllGames()
                    dao.deleteAll()
                    dao.insertReservations(dtos.toEntityList())
                    dao.insertZoneReservations(dtos.toZoneEntityList())
                    dao.insertReservationGames(dtos.toReservationGameEntityList())
                }
            }
        } catch (e: Exception) {
            Log.e("ReservationRepository", "Impossible de rafraîchir les réservations", e)
        }
    }


    override suspend fun saveReservation(reservationDto: ReservationDto): Boolean {
        try {
            val createRequest = ReservationCreateRequest(
                gamePublisherId = reservationDto.gamePublisherId ?: reservationDto.reservantId,
                festivalId = reservationDto.festivalId,
                comments = reservationDto.comments,
                status = reservationDto.status,
                tables = reservationDto.zones.orEmpty().map {
                    ReservationTableRequest(
                        priceZoneId = it.priceZoneId,
                        tableCount = it.tableCount
                    )
                }
            )

            val createResponse = api.createReservation(createRequest)
            if (!createResponse.isSuccessful) {
                Log.e("ReservationRepository", "Erreur createReservation: ${createResponse.code()} ${createResponse.errorBody()?.string()}")
                return false
            }

            val reservationId = createResponse.body()?.data?.reservationId
            if (reservationId == null) {
                Log.e("ReservationRepository", "Réponse createReservation invalide: reservationId absent")
                return false
            }

            val requestedGames = reservationDto.games.orEmpty()
            if (requestedGames.isNotEmpty()) {
                val addGamesResponse = api.addReservationGames(
                    reservationId,
                    AddReservationGamesRequest(
                        games = requestedGames.map {
                            ReservationGameInput(
                                gameId = it.gameId,
                                copyCount = it.copyCount,
                                allocatedTables = it.allocatedTables
                            )
                        }
                    )
                )

                if (!addGamesResponse.isSuccessful) {
                    Log.e("ReservationRepository", "Erreur addReservationGames: ${addGamesResponse.code()} ${addGamesResponse.errorBody()?.string()}")
                } else {
                    val createdGamesByGameId = addGamesResponse.body()
                        ?.data
                        ?.games
                        .orEmpty()
                        .groupBy { it.gameId }
                        .mapValues { (_, games) -> games.toMutableList() }

                    requestedGames
                        .filter { it.mapZoneId != null }
                        .forEach { requestedGame ->
                            val createdGame = createdGamesByGameId[requestedGame.gameId]?.removeFirstOrNull()
                            if (createdGame == null || requestedGame.mapZoneId == null) {
                                return@forEach
                            }

                            val tablesToAllocate = maxOf(1, ceil(requestedGame.allocatedTables.toDouble()).toInt())
                            val placeResponse = api.placeReservationGame(
                                createdGame.id,
                                PlaceGameRequest(
                                    mapZoneId = requestedGame.mapZoneId,
                                    tableSize = "STANDARD",
                                    allocatedTables = tablesToAllocate
                                )
                            )

                            if (!placeResponse.isSuccessful) {
                                Log.e("ReservationRepository", "Erreur placeReservationGame (gameId=${createdGame.id}): ${placeResponse.code()} ${placeResponse.errorBody()?.string()}")
                            }
                        }
                }
            }

            refreshReservations()
            return true
        } catch (e: Exception) {
            Log.e("ReservationRepository", "Erreur lors de l'envoi au serveur", e)
            return false
        }
    }

    override suspend fun deleteReservation(reservationId: Int): Boolean {
        try {
            // Optimistic local delete so the card disappears immediately.
            dao.deleteZonesByReservationId(reservationId)
            dao.deleteGamesByReservationId(reservationId)
            dao.deleteReservationById(reservationId)

            val response = api.deleteReservation(reservationId)
            if (response.isSuccessful) {
                refreshReservations()
                return true
            } else {
                Log.e("ReservationRepository", "Erreur deleteReservation: ${response.code()} ${response.errorBody()?.string()}")
                // Re-sync local state with server if delete failed remotely.
                refreshReservations()
                return false
            }
        } catch (e: Exception) {
            Log.e("ReservationRepository", "Erreur lors de la suppression de la réservation", e)
            refreshReservations()
            return false
        }
    }
}
