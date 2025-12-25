// WeatherResponse.kt
package com.manal.appmeteo.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentWeather,
    val daily: DailyWeather
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    @Json(name = "temperature_2m")
    val temperature_2m: Double,

    @Json(name = "wind_speed_10m")
    val wind_speed_10m: Double,

    @Json(name = "weather_code")
    val weathercode: Int
)

@JsonClass(generateAdapter = true)
data class DailyWeather(
    @Json(name = "temperature_2m_min")
    val temperature_2m_min: List<Double>,

    @Json(name = "temperature_2m_max")
    val temperature_2m_max: List<Double>
)