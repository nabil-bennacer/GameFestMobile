package com.example.gamefest.data

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

interface APIService {
    @GET("posts")
    suspend fun getPosts() : List<Post>

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    // Le serveur renvoie {"user": {...}}
    @GET("api/users/me")
    suspend fun getMe(): UserResponse
}
