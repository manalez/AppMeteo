package com.manal.appmeteo.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Pour le geocoding
    private val geocodingRetrofit = Retrofit.Builder()
        .baseUrl("https://geocoding-api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    // Pour la météo
    private val weatherRetrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: OpenMeteoApi by lazy {
        object : OpenMeteoApi {
            private val geocodingApi = geocodingRetrofit.create(OpenMeteoApi::class.java)
            private val weatherApi = weatherRetrofit.create(OpenMeteoApi::class.java)

            override suspend fun searchCity(name: String, count: Int, language: String) =
                geocodingApi.searchCity(name, count, language)

            override suspend fun getWeather(
                lat: Double,
                lon: Double,
                current: String,
                daily: String,
                timezone: String,
                models: String
            ) = weatherApi.getWeather(lat, lon, current, daily, timezone, models)
        }
    }
}