package com.example.gamefest.data.remote

import com.example.gamefest.data.remote.dto.FestivalDto
import com.example.gamefest.data.remote.dto.GameDto
import com.example.gamefest.data.remote.dto.PublisherDto
import retrofit2.Response
import retrofit2.http.*

interface GameFestApiService {

    // Editeurs
    @GET("game_publishers/all")
    suspend fun getAllPublishers(): Response<List<PublisherDto>>

    @POST("game_publishers/add")
    suspend fun createPublisher(@Body publisher: PublisherDto): Response<PublisherDto>

    @PUT("game_publishers/{id}")
    suspend fun updatePublisher(@Path("id") id: Int, @Body publisher: PublisherDto): Response<PublisherDto>

    @DELETE("game_publishers/{id}")
    suspend fun deletePublisher(@Path("id") id: Int): Response<Unit>

    // Jeux
    @GET("games/all")
    suspend fun getAllGames(): Response<List<GameDto>>

    @GET("games/publisher/{publisherId}")
    suspend fun getGamesByPublisher(@Path("publisherId") publisherId: Int): Response<List<GameDto>>

    @POST("games/add")
    suspend fun createGame(@Body game: GameDto): Response<GameDto>

    @PUT("games/{id}")
    suspend fun updateGame(@Path("id") id: Int, @Body game: GameDto): Response<GameDto>

    @DELETE("games/{id}")
    suspend fun deleteGame(@Path("id") id: Int): Response<Unit>

    // Festivals
    @GET("festivals/all")
    suspend fun getAllFestivals(): Response<List<FestivalDto>>

    @POST("festivals/add")
    suspend fun createFestival(@Body festival: FestivalDto): Response<FestivalDto>

    @PUT("festivals/{id}")
    suspend fun updateFestival(@Path("id") id: Int, @Body festival: FestivalDto): Response<FestivalDto>

    @DELETE("festivals/{id}")
    suspend fun deleteFestival(@Path("id") id: Int): Response<Unit>
}