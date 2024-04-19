package com.pc.weather_puichuen.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.Executors

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class AppDB : RoomDatabase() {
    abstract fun historyDAO(): HistoryDAO

    companion object{
        private var db: AppDB? = null

        private const val NUMBER_OF_THREADS = 4
        val databaseQueryExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        fun getDB(context: Context) : AppDB?{
            if (db == null){
                db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDB::class.java,
                    "com.pc.weather_puichuen_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return db
        }
    }
}