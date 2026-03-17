package com.example.gamefest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gamefest.data.local.dao.GameDao
import com.example.gamefest.data.local.dao.PublisherDao
import com.example.gamefest.data.local.entity.GameEntity
import com.example.gamefest.data.local.entity.PublisherEntity

@Database(
    entities = [PublisherEntity::class, GameEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GameFestDatabase : RoomDatabase() {

    abstract fun publisherDao(): PublisherDao
    abstract fun gameDao(): GameDao

    // --- LE BLOC À AJOUTER EST ICI ---
    companion object {
        @Volatile
        private var Instance: GameFestDatabase? = null

        fun getDatabase(context: Context): GameFestDatabase {
            // Si l'instance existe déjà, on la renvoie. Sinon, on la crée proprement.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GameFestDatabase::class.java, "gamefest_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}