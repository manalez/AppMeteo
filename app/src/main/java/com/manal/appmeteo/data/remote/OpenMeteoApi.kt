package com.manal.appmeteo.data.remote

import com.manal.appmeteo.data.dto.GeoResponse
import com.manal.appmeteo.data.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {

    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "fr"
    ): GeoResponse

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,wind_speed_10m,weather_code",
        @Query("daily") daily: String = "temperature_2m_min,temperature_2m_max",
        @Query("timezone") timezone: String = "auto",
        @Query("models") models: String = "meteofrance_seamless"
    ): WeatherResponse
}