package com.manal.appmeteo.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    cityName: String,
    lat: Double,
    lon: Double,
    vm: DetailViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

    LaunchedEffect(lat, lon) {
        vm.load(cityName, lat, lon)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(cityName) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.error != null -> {
                    Text(
                        state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }

                state.weather != null -> {
                    val w = state.weather!!
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        WeatherCard("🌡 Température", "${w.temperature} °C")
                        WeatherCard("🌬 Vent", "${w.windSpeed} km/h")
                        WeatherCard("📉 Température Min", "${w.tempMin} °C")
                        WeatherCard("📈 Température Max", "${w.tempMax} °C")
                        WeatherCard("☁️ Conditions", w.condition)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}