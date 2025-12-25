package com.manal.appmeteo.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manal.appmeteo.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = false,
    val weather: WeatherUi? = null,
    val error: String? = null
)

data class WeatherUi(
    val temperature: Double,
    val windSpeed: Double,
    val tempMin: Double,
    val tempMax: Double,
    val condition: String
)

class DetailViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = WeatherRepository(app.applicationContext)

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    fun load(cityName: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val cached = repo.getWeatherOfflineFirst(cityName, lat, lon)

                if (cached != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            weather = WeatherUi(
                                temperature = cached.temperature,
                                windSpeed = cached.windSpeed,
                                tempMin = cached.tempMin,
                                tempMax = cached.tempMax,
                                condition = cached.conditionLabel
                            )
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Aucune donnée disponible"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Erreur météo: ${e.message}"
                    )
                }
            }
        }
    }
}