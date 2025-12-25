package com.manal.appmeteo.data.repository

import android.content.Context
import com.manal.appmeteo.data.dto.GeoResult
import com.manal.appmeteo.data.local.AppDatabase
import com.manal.appmeteo.data.local.CachedWeatherEntity
import com.manal.appmeteo.data.local.FavoriteCityEntity
import com.manal.appmeteo.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class WeatherRepository(context: Context) {

    private val api = RetrofitClient.api
    private val dao = AppDatabase.getDatabase(context).dao()

    companion object {
        private const val CACHE_EXPIRY = 30 * 60 * 1000L // 30 minutes
    }

    // ============ RECHERCHE VILLE ============
    suspend fun searchCity(name: String): List<GeoResult> {
        return try {
            val response = api.searchCity(name)
            response.results ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ============ MÉTÉO (avec cache) ============
    suspend fun getWeatherOfflineFirst(
        cityName: String,
        lat: Double,
        lon: Double
    ): CachedWeatherEntity? {
        val key = "$cityName|$lat|$lon"

        // 1. Vérifier le cache
        val cached = dao.getCachedWeather(key)
        if (cached != null) {
            val age = System.currentTimeMillis() - cached.updatedAt
            // Si cache valide (< 30 min), le retourner
            if (age < CACHE_EXPIRY) {
                return cached
            }
        }

        // 2. Sinon, appeler l'API
        return try {
            val response = api.getWeather(lat, lon)
            val weatherEntity = CachedWeatherEntity(
                cityKey = key,
                temperature = response.current.temperature_2m,
                windSpeed = response.current.wind_speed_10m,
                tempMin = response.daily.temperature_2m_min.firstOrNull() ?: 0.0,
                tempMax = response.daily.temperature_2m_max.firstOrNull() ?: 0.0,
                conditionLabel = getWeatherCondition(response.current.weathercode),
                updatedAt = System.currentTimeMillis()
            )

            // Sauvegarder dans le cache
            dao.upsertCachedWeather(weatherEntity)
            weatherEntity
        } catch (e: Exception) {
            // Si erreur réseau, retourner le cache même expiré
            cached
        }
    }

    // ============ FAVORIS ============
    fun observeFavorites(): Flow<List<FavoriteCityEntity>> {
        return dao.observeFavorites()
    }

    suspend fun toggleFavorite(city: GeoResult) {
        val key = "${city.name}|${city.latitude}|${city.longitude}"

        if (dao.isFavorite(key)) {
            // Supprimer
            dao.deleteFavorite(key)
        } else {
            // Ajouter
            val favorite = FavoriteCityEntity(
                cityKey = key,
                name = city.name,
                country = city.country,
                latitude = city.latitude,
                longitude = city.longitude
            )
            dao.upsertFavorite(favorite)
        }
    }

    suspend fun isFavorite(city: GeoResult): Boolean {
        val key = "${city.name}|${city.latitude}|${city.longitude}"
        return dao.isFavorite(key)
    }

    // ============ HELPER ============
    private fun getWeatherCondition(code: Int): String {
        return when (code) {
            0 -> "☀️ Ciel dégagé"
            1, 2 -> "🌤️ Peu nuageux"
            3 -> "☁️ Couvert"
            45, 48 -> "🌫️ Brouillard"
            51, 53, 55 -> "🌦️ Bruine"
            61, 63, 65 -> "🌧️ Pluie"
            71, 73, 75 -> "❄️ Neige"
            95, 96, 99 -> "⛈️ Orage"
            else -> "🌡️ Météo"
        }
    }
}