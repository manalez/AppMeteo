package com.manal.appmeteo.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manal.appmeteo.data.dto.GeoResult
import com.manal.appmeteo.data.local.CachedWeatherEntity
import com.manal.appmeteo.data.local.FavoriteCityEntity
import com.manal.appmeteo.data.repository.WeatherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val query: String = "",
    val results: List<GeoResult> = emptyList(),
    val favorites: List<Pair<FavoriteCityEntity, CachedWeatherEntity?>> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = WeatherRepository(app.applicationContext)

    private val _state = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        // Observer les favoris et charger leur météo
        viewModelScope.launch {
            repo.observeFavorites().collect { favs ->
                val list = favs.map { fav ->
                    val weather = repo.getWeatherOfflineFirst(
                        fav.name,
                        fav.latitude,
                        fav.longitude
                    )
                    fav to weather
                }
                _state.update { it.copy(favorites = list) }
            }
        }
    }

    fun onQueryChange(v: String) {
        _state.update { it.copy(query = v) }
    }

    fun searchCity() {
        val q = _state.value.query.trim()
        if (q.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val results = repo.searchCity(q)
                _state.update {
                    it.copy(
                        isLoading = false,
                        results = results,
                        error = if (results.isEmpty()) "Aucun résultat" else null
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Erreur réseau / API"
                    )
                }
            }
        }
    }

    fun toggleFavorite(city: GeoResult) {
        viewModelScope.launch {
            repo.toggleFavorite(city)
        }
    }

    suspend fun isFavorite(city: GeoResult): Boolean {
        return repo.isFavorite(city)
    }
}