package com.example.gamefest.data.repository

import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.data.remote.dto.PublisherDto
import kotlinx.coroutines.flow.Flow

interface PublisherRepository {
    fun getAllPublishers(): Flow<List<PublisherEntity>>
    fun getPublisherById(id: Int): Flow<PublisherEntity?>

    suspend fun refreshPublishers()
    suspend fun addPublisher(publisher: PublisherDto)
    suspend fun deletePublisher(id: Int)
}