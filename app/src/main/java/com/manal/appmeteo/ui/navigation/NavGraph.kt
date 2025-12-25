package com.manal.appmeteo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.manal.appmeteo.ui.detail.DetailScreen
import com.manal.appmeteo.ui.home.HomeScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // HOME SCREEN
        composable(route = "home") {
            HomeScreen(
                onOpenDetail = { name, lat, lon ->
                    val encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
                    navController.navigate("detail/$encodedName/$lat/$lon")
                },
                onRequestLocation = {
                    // TODO: Implémenter la géolocalisation
                }
            )
        }

        // DETAIL SCREEN
        composable(
            route = "detail/{name}/{lat}/{lon}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedName = backStackEntry.arguments?.getString("name") ?: ""
            val cityName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.toString())
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull() ?: 0.0

            DetailScreen(
                cityName = cityName,
                lat = lat,
                lon = lon
            )
        }
    }
}