package com.example.gamefest.data.repository

import com.example.gamefest.data.local.entity.UserEntity
import com.example.gamefest.data.remote.dto.*
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val currentUser: Flow<UserEntity?>
    suspend fun login(request: LoginRequest): Result<UserEntity>
    suspend fun register(request: RegisterRequest): Result<UserEntity>
    suspend fun logout(): Result<Unit>
    suspend fun getProfile(): Result<UserEntity>
}
