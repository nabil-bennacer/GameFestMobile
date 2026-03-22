package com.example.gamefest.data.remote.dto

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val message: String,
    val user: UserDto? = null
)

data class ProfileResponse(
    val user: UserDto
)
