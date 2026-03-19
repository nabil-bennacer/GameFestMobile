package com.example.gamefest.data.repository

import com.example.gamefest.data.local.dao.UserDao
import com.example.gamefest.data.local.entity.UserEntity
import com.example.gamefest.data.mapper.toEntity
import com.example.gamefest.data.remote.GameFestApiService
import com.example.gamefest.data.remote.PersistentCookieJar
import com.example.gamefest.data.remote.dto.*
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val dao: UserDao,
    private val api: GameFestApiService,
    private val cookieJar: PersistentCookieJar
) : UserRepository {

    override val currentUser: Flow<UserEntity?> = dao.getCurrentUser()

    override suspend fun login(request: LoginRequest): Result<UserEntity> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful) {
                val userDto = response.body()?.user
                if (userDto != null) {
                    val entity = userDto.toEntity()
                    dao.clearUser()
                    dao.insertUser(entity)
                    Result.success(entity)
                } else {
                    Result.failure(Exception("Login failed: No user in response"))
                }
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<UserEntity> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful) {
                val userDto = response.body()?.user
                if (userDto != null) {
                    val entity = userDto.toEntity()
                    dao.clearUser()
                    dao.insertUser(entity)
                    Result.success(entity)
                } else {
                    Result.failure(Exception("Registration failed: No user in response"))
                }
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            api.logout()
            dao.clearUser()
            cookieJar.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            dao.clearUser()
            cookieJar.clearAll()
            Result.success(Unit) // On considère la déconnexion réussie localement
        }
    }

    override suspend fun getProfile(): Result<UserEntity> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                val userDto = response.body()?.user
                if (userDto != null) {
                    val entity = userDto.toEntity()
                    dao.clearUser()
                    dao.insertUser(entity)
                    Result.success(entity)
                } else {
                    Result.failure(Exception("Profile fetch failed: No user in response"))
                }
            } else {
                if (response.code() == 401) {
                    dao.clearUser()
                }
                Result.failure(Exception("Profile fetch failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
