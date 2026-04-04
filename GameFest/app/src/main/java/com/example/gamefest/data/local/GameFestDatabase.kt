package com.example.gamefest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gamefest.data.local.dao.*
import com.example.gamefest.data.local.entity.*

@Database(
    entities = [
        PublisherEntity::class,
        GameEntity::class,
        UserEntity::class,
        FestivalEntity::class,
        PriceZoneEntity::class,
        TableTypeEntity::class,
        MapZoneEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class GameFestDatabase : RoomDatabase() {

    abstract fun publisherDao(): PublisherDao
    abstract fun gameDao(): GameDao
    abstract fun userDao(): UserDao
    abstract fun festivalDao(): FestivalDao
    abstract fun priceZoneDao(): PriceZoneDao

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
