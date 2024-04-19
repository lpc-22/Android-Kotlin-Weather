package com.pc.weather_puichuen.api

import com.pc.weather_puichuen.models.Weather
import retrofit2.http.GET
import retrofit2.http.Path

interface MyInterface {

    @GET("timeline/{latitude},{longitude}/today?unitGroup=metric&elements=datetime,temp,humidity,conditions&include=current&key=J6XML7D5ZQFTQ64XA7YCQG7PY&contentType=json")
    suspend fun getWeatherByLatLong(
        @Path("latitude") latitude:Double,
        @Path("longitude") longitude:Double
    ): Weather
}