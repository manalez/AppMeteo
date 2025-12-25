package com.manal.appmeteo.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.manal.appmeteo.data.dto.GeoResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDetail: (String, Double, Double) -> Unit,
    onRequestLocation: () -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val s by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AppMeteo") },
                actions = {
                    IconButton(onClick = onRequestLocation) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Ma position")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Barre de recherche
            OutlinedTextField(
                value = s.query,
                onValueChange = vm::onQueryChange,
                label = { Text("Rechercher une ville") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = vm::searchCity,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Rechercher")
            }

            if (s.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            s.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Section Favoris
                item {
                    Text(
                        "⭐ Favoris",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(s.favorites) { (fav, weather) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOpenDetail(fav.name, fav.latitude, fav.longitude)
                            }
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                "${fav.name}${if (fav.country != null) ", ${fav.country}" else ""}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(6.dp))
                            if (weather != null) {
                                Text("🌡️ ${weather.temperature}°C • 🌬️ ${weather.windSpeed} km/h")
                                Text("📉 ${weather.tempMin}°C • 📈 ${weather.tempMax}°C • ${weather.conditionLabel}")
                            } else {
                                Text("Aucune donnée en cache")
                            }
                        }
                    }
                }

                // Section Résultats de recherche
                if (s.results.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "🔎 Résultats",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    items(s.results) { city ->
                        SearchResultRow(
                            city = city,
                            onOpen = { onOpenDetail(city.name, city.latitude, city.longitude) },
                            onToggleFav = { vm.toggleFavorite(city) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    city: GeoResult,
    onOpen: () -> Unit,
    onToggleFav: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .clickable { onOpen() }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "${city.name}${if (city.country != null) ", ${city.country}" else ""}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "lat=${city.latitude}, lon=${city.longitude}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onToggleFav) {
                Icon(Icons.Outlined.StarBorder, contentDescription = "Favori")
            }
        }
    }
}