package com.example.gamefest.data.remote

import com.example.gamefest.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface GameFestApiService {

    // Auth
    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("users/refresh")
    suspend fun refresh(): Response<AuthResponse>

    @GET("users/me")
    suspend fun getProfile(): Response<ProfileResponse>

    @POST("users/logout")
    suspend fun logout(): Response<AuthResponse>

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

    // Admin Users
    @GET("users/admin/all")
    suspend fun getAllUsers(): Response<List<UserDto>>

    @GET("users/admin/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<UserDto>

    @POST("users/admin/create")
    suspend fun createUserByAdmin(@Body user: UserDto): Response<UserDto>

    @PUT("users/admin/{id}/role")
    suspend fun updateUserRole(@Path("id") id: Int, @Body roleUpdate: Map<String, String>): Response<UserDto>

    @DELETE("users/admin/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>
}
