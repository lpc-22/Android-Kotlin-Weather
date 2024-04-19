package com.pc.weather_puichuen.data

import android.app.Application
import androidx.lifecycle.LiveData

class HistoryRepository(application: Application) {
    private var db : AppDB? = null
    private var historyDAO = AppDB.getDB(application)?.historyDAO()

     var allHistory : LiveData<List<History>>? = historyDAO?.getAllHistory()

    init {
        this.db = AppDB.getDB(application)
    }

    fun insertHistory(historyToInsert : History){
        AppDB.databaseQueryExecutor.execute{
            this.historyDAO?.insertHistory(historyToInsert)
        }
    }

    fun deleteHistory(deleteHistory: History){
        AppDB.databaseQueryExecutor.execute{
            this.historyDAO?.deleteHistory(deleteHistory)
        }
    }
}