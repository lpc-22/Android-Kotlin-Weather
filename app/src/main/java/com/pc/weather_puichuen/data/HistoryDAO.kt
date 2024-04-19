package com.pc.weather_puichuen.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDAO {
    @Insert
    fun insertHistory(newHistory: History)

    @Delete
    fun deleteHistory(deleteHistory: History)

    @Query("SELECT * FROM table_history")
    fun getAllHistory() : LiveData<List<History>>
}