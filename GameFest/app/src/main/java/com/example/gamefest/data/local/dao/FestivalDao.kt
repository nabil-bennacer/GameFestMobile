package com.example.gamefest.data.local.dao

import androidx.room.*
import com.example.gamefest.data.local.entity.FestivalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FestivalDao {
    @Query("SELECT * FROM festivals")
    fun getAllFestivals(): Flow<List<FestivalEntity>>

    @Query("SELECT * FROM festivals WHERE id = :id")
    fun getFestivalById(id: Int): Flow<FestivalEntity?>

    @Update
    suspend fun updateFestival(festival: FestivalEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(festivals: List<FestivalEntity>)

    @Transaction
    suspend fun upsertAll(festivals: List<FestivalEntity>) {
        insertAllIgnore(festivals)
        festivals.forEach { updateFestival(it) }
    }

    @Query("DELETE FROM festivals")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteFestival(festival: FestivalEntity)

    @Query("DELETE FROM festivals WHERE id = :id")
    suspend fun deleteFestivalById(id: Int)
}
