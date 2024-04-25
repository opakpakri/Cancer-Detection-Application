package com.dicoding.asclepius.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CancerHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: CancerHistory)

    @Query("SELECT * FROM CancerHistory")
    suspend fun getAllHistory(): List<CancerHistory>

    @Delete
    suspend fun deleteHistory(history: CancerHistory)
}
