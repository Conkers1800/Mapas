package com.conkers.mapas.api

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.conkers.mapas.Screen.LocationInputScreen
import com.conkers.mapas.Screen.MapScreen
import com.conkers.mapas.otros.getCurrentLocation
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface DirectionsApi {
    @GET("v2/directions/driving-car")
    suspend fun getRoute(
        @Query("api_key") apiKey: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): RouteResponse

    @POST("v2/directions/driving-car")
    suspend fun postRoute(
        @Header("Authorization") apiKey: String,
        @Body body: GeoJsonRequest
    ): RouteResponse


}



val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https://api.openrouteservice.org/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val api: DirectionsApi = retrofit.create(DirectionsApi::class.java)


fun createRetrofitApi(): DirectionsApi {
    return Retrofit.Builder()
        .baseUrl("https://api.openrouteservice.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DirectionsApi::class.java)
}

@Composable
fun AppNavigator(apiKey: String, context: Context) {
    var routeCoordinates by remember { mutableStateOf(listOf(
        listOf(-101.1967479, 20.1288084), // Coordenada inicial de prueba
        listOf(-101.1967479, 21.0000000), // Coordenada final de prueba
    )) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var destinationAddress by remember { mutableStateOf("") }
    var showLocationInput by remember { mutableStateOf(false) }
    val api = remember { createRetrofitApi() }

    // Obtener la ubicación actual
    LaunchedEffect(Unit) {
        getCurrentLocation(
            context = context,
            onSuccess = { location ->
                currentLocation = location
                Log.d("Location", "Lat: ${location.latitude}, Lng: ${location.longitude}")
            },
            onError = {
                Log.e("Location Error", "No se pudo obtener la ubicación actual")
            }
        )
    }

    // Calcular la ruta
    LaunchedEffect(currentLocation, destinationAddress) {
        if (currentLocation != null && destinationAddress.isNotEmpty()) {
            try {
                val startCoordinates = listOf(currentLocation!!.longitude, currentLocation!!.latitude)
                val endCoordinates = destinationAddress.split(",").map { it.toDouble() }

                val responsePost = fetchRoutePost(api, apiKey, listOf(startCoordinates, endCoordinates))
                routeCoordinates = responsePost?.routes?.firstOrNull()?.geometry?.coordinates ?: emptyList()
                if (routeCoordinates.isEmpty()) {
                    Log.e("Error", "No se pudieron obtener las coordenadas de la ruta")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Error", "Error al calcular la ruta: ${e.message}")
            }
        }
    }

    // Renderizar la pantalla principal
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showLocationInput = true }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        if (routeCoordinates.isNotEmpty()) {
            MapScreen(
                onRouteCalculated = { destination ->
                    destinationAddress = destination
                },
                routeCoordinates = routeCoordinates
            )
        } else {
            Text(
                "Cargando mapa o no hay rutas disponibles",
                modifier = Modifier.padding(paddingValues)
            )
        }
    }

    if (showLocationInput) {
        LocationInputScreen(
            onDismissRequest = { showLocationInput = false },
            onRouteSubmitted = { destination ->
                destinationAddress = destination
                showLocationInput = false
            }
        )
    }
}
