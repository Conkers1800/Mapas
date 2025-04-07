package com.conkers.mapas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import com.conkers.mapas.Screen.LocationInputScreen
import com.conkers.mapas.Screen.MapScreen
import com.conkers.mapas.api.AppNavigator
import com.conkers.mapas.api.createRetrofitApi
import com.conkers.mapas.api.fetchRoute
import com.conkers.mapas.otros.PermissionScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var permissionGranted by remember { mutableStateOf(false) }
            var routeCoordinates by remember { mutableStateOf(emptyList<List<Double>>()) }

            // Pantalla de permisos
            if (!permissionGranted) {
                PermissionScreen(onPermissionGranted = { permissionGranted = true })
            } else if (routeCoordinates.isEmpty()) {
                // Pantalla para ingresar coordenadas de inicio y destino
                LocationInputScreen(onRouteRequested = { start, end ->
                    // Llamada a la API para calcular la ruta
                    CoroutineScope(Dispatchers.IO).launch {
                        val apiKey = ""
                        val api = createRetrofitApi()
                        val coordinates = fetchRoute(api, start, end, apiKey)
                        routeCoordinates = coordinates
                    }
                })
            } else {
                // Pantalla del mapa con la ruta generada
                MapScreen(routeCoordinates = routeCoordinates)
            }
        }
    }
}
