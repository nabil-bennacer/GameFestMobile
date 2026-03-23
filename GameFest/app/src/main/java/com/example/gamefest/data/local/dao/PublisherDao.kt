package com.example.gamefest.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gamefest.data.local.entity.PublisherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PublisherDao {
    // Le Flow émettra une nouvelle liste automatiquement à chaque modification de la table
    @Query("SELECT * FROM publishers")
    fun getAllPublishers(): Flow<List<PublisherEntity>>

    @Query("SELECT * FROM publishers WHERE id = :id")
    fun getPublisherById(id: Int): Flow<PublisherEntity?>

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Cree l'éditeur si il existe pas sinon le remplace par le nouveau
    suspend fun insertPublisher(publisher: PublisherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPublishers(publishers: List<PublisherEntity>)

    // Delete
    @Delete
    suspend fun deletePublisher(publisher: PublisherEntity)


    @Query("DELETE FROM publishers WHERE id = :id")
    suspend fun deletePublisherById(id: Int)
}