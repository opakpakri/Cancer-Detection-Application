package com.dicoding.asclepius.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DataHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: DataHistory)

    @Query("SELECT * FROM DataHistory")
    suspend fun getAllHistory(): List<DataHistory>

    @Delete
    suspend fun deleteHistory(history: DataHistory)
}
