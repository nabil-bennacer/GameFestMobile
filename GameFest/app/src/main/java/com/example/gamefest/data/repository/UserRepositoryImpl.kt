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

    override suspend fun getAllUsers(): Result<List<UserDto>> {
        return try {
            val response = api.getAllUsers()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Erreur de récupération des utilisateurs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserRole(id: Int, role: String): Result<UserDto> {
        return try {
            val response = api.updateUserRole(id, mapOf("role" to role))
            if (response.isSuccessful) {
                // On extrait l'objet 'user' de 'AuthResponse'
                val updatedUser = response.body()?.user
                if (updatedUser != null) {
                    Result.success(updatedUser)
                } else {
                    Result.failure(Exception("Réponse vide lors de la mise à jour du rôle"))
                }
            } else {
                Result.failure(Exception("Erreur de mise à jour du rôle: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
