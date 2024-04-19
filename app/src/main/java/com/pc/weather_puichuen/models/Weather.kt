package com.pc.weather_puichuen.models

data class Weather(
    val queryCost: Long,
    val latitude: Double,
    val longitude: Double,
    val resolvedAddress: String,
    val address: String,
    val timezone: String,
    val tzoffset: Double,
    val days: List<Day>,
    val currentConditions: CurrentConditions,
)

data class Day(
    val datetime: String,
    val temp: Double,
    val humidity: Double,
    val conditions: String,
)

data class CurrentConditions(
    val datetime: String,
    val temp: Double,
    val humidity: Double,
    val conditions: String,
)