package com.dicoding.asclepius.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DataHistory::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun cancerHistoryDao(): DataHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "DataHistory"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}