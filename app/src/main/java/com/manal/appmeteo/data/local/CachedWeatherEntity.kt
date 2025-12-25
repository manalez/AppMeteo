// ========== CachedWeatherEntity.kt ==========
// Ce fichier DOIT être dans : data/local/CachedWeatherEntity.kt

package com.manal.appmeteo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_weather")
data class CachedWeatherEntity(
    @PrimaryKey
    val cityKey: String,
    val temperature: Double,
    val windSpeed: Double,
    val tempMin: Double,
    val tempMax: Double,
    val conditionLabel: String,
    val updatedAt: Long = System.currentTimeMillis()
)