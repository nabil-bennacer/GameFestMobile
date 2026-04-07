package com.example.gamefest.data.remote.dto

data class ApiResponse<T>(
    val message: String? = null,
    val data: T? = null
)
