package com.example.gamefest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gamefest.data.local.dao.GameDao
import com.example.gamefest.data.local.dao.PublisherDao
import com.example.gamefest.data.local.dao.UserDao
import com.example.gamefest.data.local.entity.GameEntity
import com.example.gamefest.data.local.entity.PublisherEntity
import com.example.gamefest.data.local.entity.UserEntity

@Database(
    entities = [PublisherEntity::class, GameEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class GameFestDatabase : RoomDatabase() {

    abstract fun publisherDao(): PublisherDao
    abstract fun gameDao(): GameDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var Instance: GameFestDatabase? = null

        fun getDatabase(context: Context): GameFestDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, GameFestDatabase::class.java, "gamefest_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}