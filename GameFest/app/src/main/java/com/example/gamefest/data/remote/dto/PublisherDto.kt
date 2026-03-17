package com.example.gamefest.data.remote.dto

data class PublisherDto(
    val id: Int,
    val name: String,
    val logoUrl: String?,
    val exposant: Boolean?,
    val distributeur: Boolean?
)
