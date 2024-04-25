package com.dicoding.asclepius.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CancerHistory::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun cancerHistoryDao(): CancerHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "Cancer_History_Database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}