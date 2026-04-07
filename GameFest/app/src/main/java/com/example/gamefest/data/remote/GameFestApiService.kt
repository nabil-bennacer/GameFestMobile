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

    // Festivals
    @GET("festivals/all")
    suspend fun getAllFestivals(): Response<List<FestivalDto>>

    @POST("festivals/add")
    suspend fun createFestival(@Body festival: FestivalDto): Response<FestivalDto>

    @PUT("festivals/{id}")
    suspend fun updateFestival(@Path("id") id: Int, @Body festival: FestivalDto): Response<FestivalDto>

    @DELETE("festivals/{id}")
    suspend fun deleteFestival(@Path("id") id: Int): Response<Unit>

    // Price Zones (Matching the pattern used in the web app and other resources)
    @GET("price_zone/festival/{festivalId}")
    suspend fun getPriceZonesByFestival(@Path("festivalId") festivalId: Int): Response<List<PriceZoneDto>>

    @POST("price_zone/add")
    suspend fun createPriceZone(@Body request: PriceZoneRequest): Response<PriceZoneDto>

    @PUT("price_zone/{id}")
    suspend fun updatePriceZone(@Path("id") id: Int, @Body request: Map<String, Any>): Response<List<PriceZoneDto>>

    @DELETE("price_zone/{id}")
    suspend fun deletePriceZone(@Path("id") id: Int): Response<Unit>

    // Admin Users
    @GET("users/admin/all")
    suspend fun getAllUsers(): Response<List<UserDto>>

    @GET("users/admin/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<UserDto>

    @POST("users/admin/create")
    suspend fun createUserByAdmin(@Body user: UserDto): Response<UserDto>

    @PUT("users/admin/{id}/role")
    suspend fun updateUserRole(@Path("id") id: Int, @Body roleUpdate: Map<String, String>): Response<AuthResponse>

    @DELETE("users/admin/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    // Reservations
    @GET("reservations/all")
    suspend fun getAllReservations(): Response<List<ReservationDto>>

    @GET("reservations/festival/{festivalId}")
    suspend fun getReservationsByFestival(@Path("festivalId") festivalId: Int): Response<List<ReservationDto>>

    @POST("reservations/add")
    suspend fun createReservation(@Body reservation: ReservationCreateRequest): Response<ApiResponse<ReservationDto>>

    @POST("reservations/{id}/games")
    suspend fun addReservationGames(
        @Path("id") reservationId: Int,
        @Body request: AddReservationGamesRequest
    ): Response<ApiResponse<ReservationDto>>

    @POST("reservations/game/{gameId}/place")
    suspend fun placeReservationGame(
        @Path("gameId") festivalGameId: Int,
        @Body request: PlaceGameRequest
    ): Response<ApiResponse<FestivalGameDto>>

    @DELETE("reservations/{id}")
    suspend fun deleteReservation(@Path("id") reservationId: Int): Response<ApiResponse<Any>>

    @POST("map_zones")
    suspend fun createMapZone(@Body mapZone: MapZoneCreateDto): Response<MapZoneDto>
}
