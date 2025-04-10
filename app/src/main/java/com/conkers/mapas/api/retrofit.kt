package com.conkers.mapas.api

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.conkers.mapas.otros.RequestPermissionsScreen
import com.conkers.mapas.otros.checkLocationSettings
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
    var permissionsGranted by remember { mutableStateOf(false) }
    var routeCoordinates by remember { mutableStateOf(emptyList<List<Double>>()) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var destinationAddress by remember { mutableStateOf("") }
    var showLocationInput by remember { mutableStateOf(false) }
    val api = remember { createRetrofitApi() }

    if (!permissionsGranted) {
        RequestPermissionsScreen(onPermissionsGranted = {
            permissionsGranted = true
            checkLocationSettings(
                context = context,
                onGPSAvailable = {
                    getCurrentLocation(
                        context = context,
                        onSuccess = { location ->
                            currentLocation = location
                            Log.d("Location", "Ubicación obtenida: ${location.latitude}, ${location.longitude}")
                        },
                        onError = {
                            Log.e("Location Error", "No se pudo obtener la ubicación actual")
                        }
                    )
                },
                onGPSUnavailable = {
                    Toast.makeText(context, "Por favor habilita el GPS para continuar", Toast.LENGTH_SHORT).show()
                }
            )
        })
    } else {
        LaunchedEffect(currentLocation, destinationAddress) {
            if (currentLocation != null && destinationAddress.isNotEmpty()) {
                try {
                    val startCoordinates = listOf(currentLocation!!.longitude, currentLocation!!.latitude)
                    val endCoordinates = destinationAddress.split(",").map { it.toDouble() }

                    Log.d("AppNavigator", "Punto inicial: $startCoordinates, Punto final: $endCoordinates")

                    val responsePost = fetchRoutePost(api, apiKey, listOf(startCoordinates, endCoordinates))
                    routeCoordinates = responsePost?.routes?.firstOrNull()?.geometry?.coordinates ?: emptyList()
                    if (routeCoordinates.isEmpty()) {
                        Log.e("Error", "No se pudieron obtener las coordenadas detalladas de la ruta")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("Error", "Error al calcular la ruta: ${e.message}")
                }
            }
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showLocationInput = true }) {
                    Text("+")
                }
            }
        ) { paddingValues ->
            MapScreen(
                onRouteCalculated = { destination ->
                    destinationAddress = destination
                },
                routeCoordinates = routeCoordinates,
                currentLocation = currentLocation // Para centrar el mapa en la ubicación actual
            )
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
}
