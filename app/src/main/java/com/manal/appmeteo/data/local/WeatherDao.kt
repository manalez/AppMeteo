// ========== WeatherDao.kt ==========
// Ce fichier DOIT être dans : data/local/WeatherDao.kt

package com.manal.appmeteo.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    // Favorites
    @Query("SELECT * FROM favorite_cities ORDER BY addedAt DESC")
    fun observeFavorites(): Flow<List<FavoriteCityEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE cityKey = :key)")
    suspend fun isFavorite(key: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFavorite(city: FavoriteCityEntity)

    @Query("DELETE FROM favorite_cities WHERE cityKey = :key")
    suspend fun deleteFavorite(key: String)

    // Cache
    @Query("SELECT * FROM cached_weather WHERE cityKey = :key")
    suspend fun getCachedWeather(key: String): CachedWeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedWeather(weather: CachedWeatherEntity)
}