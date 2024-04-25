package com.dicoding.asclepius.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DataHistory")
data class DataHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String,
    val result: String,
)