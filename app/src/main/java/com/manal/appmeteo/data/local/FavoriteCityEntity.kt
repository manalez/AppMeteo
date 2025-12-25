// ========== FavoriteCityEntity.kt ==========
// Ce fichier DOIT être dans : data/local/FavoriteCityEntity.kt

package com.manal.appmeteo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey
    val cityKey: String,        // ex: "Paris|48.85|2.35"
    val name: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val addedAt: Long = System.currentTimeMillis()
)