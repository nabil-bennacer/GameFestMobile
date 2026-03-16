package com.example.gamefest.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val email: String? = null,
    val name: String? = null, // Correspond au JSON du serveur
    val role: String? = null
)

@Serializable
data class UserResponse(
    val user: User
)

@Serializable
data class LoginResponse(
    val token: String? = null,
    val user: User? = null,
    val message: String? = null
)
