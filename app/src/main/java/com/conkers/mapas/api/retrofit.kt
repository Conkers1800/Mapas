package com.conkers.mapas.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.conkers.mapas.Screen.LocationInputScreen
import com.conkers.mapas.Screen.MapScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApi {
    @GET("v2/directions/driving-car")
    suspend fun getRoute(
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("api_key") apiKey: String
    ): RouteResponse
}

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://api.openrouteservice.org/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val api: DirectionsApi = retrofit.create(DirectionsApi::class.java)

@Composable
fun AppNavigator(apiKey: String) {
    var routeCoordinates by remember { mutableStateOf(emptyList<List<Double>>()) }

    if (routeCoordinates.isEmpty()) {
        LocationInputScreen(onRouteRequested = { start, end ->
            val api = Retrofit.Builder()
                .baseUrl("https://api.openrouteservice.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DirectionsApi::class.java)

            CoroutineScope(Dispatchers.IO).launch {
                val coordinates = fetchRoute(api, start, end, apiKey)
                routeCoordinates = coordinates
            }
        })
    } else {
        MapScreen(routeCoordinates = routeCoordinates)
    }
}

// Configuración de Retrofit para la API
fun createRetrofitApi(): DirectionsApi {
    return Retrofit.Builder()
        .baseUrl("https://api.openrouteservice.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DirectionsApi::class.java)
}
