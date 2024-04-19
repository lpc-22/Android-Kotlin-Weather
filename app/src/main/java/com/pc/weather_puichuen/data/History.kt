package com.pc.weather_puichuen.data

import android.location.Address
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pc.weather_puichuen.models.Weather

// Entity
@Entity(tableName = "table_history")
class History (
    var location: String,
    var time: String,
    var temperature: Double,
    var hudmidity: Double,
    var condition: String

//    var location: Address,
//    var weather: Weather
){
    @PrimaryKey(autoGenerate = true)
    var id = 0
}