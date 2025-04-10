package com.conkers.mapas.Screen

import android.graphics.Color
import android.preference.PreferenceManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.osmdroid.views.MapView

@Composable
fun MapScreen(
    onRouteCalculated: (String) -> Unit,
    routeCoordinates: List<List<Double>>
) {
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onRouteCalculated("") }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        AndroidView(
            factory = {
                val mapView = MapView(context)
                Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
                mapView.setTileSource(TileSourceFactory.MAPNIK)
                mapView.controller.setZoom(15.0)
                Log.d("MapView", "MapView inicializado correctamente")

                // Centrar en el primer punto de la ruta si existe
                if (routeCoordinates.isNotEmpty()) {
                    val firstCoordinate = routeCoordinates.first()
                    mapView.controller.setCenter(GeoPoint(firstCoordinate[1], firstCoordinate[0]))
                    Log.d("MapScreen", "Mapa centrado en: ${firstCoordinate[1]}, ${firstCoordinate[0]}")
                } else {
                    Log.e("MapScreen", "No hay coordenadas disponibles para centrar el mapa")
                }

                // Dibujar la ruta
                drawRoute(mapView, routeCoordinates)

                mapView // Retorna el MapView
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

fun drawRoute(mapView: MapView, routeCoordinates: List<List<Double>>) {
    if (routeCoordinates.isEmpty()) {
        Log.e("Ruta", "No hay coordenadas para dibujar")
        return
    }

    val geoPoints = routeCoordinates.map { GeoPoint(it[1], it[0]) }
    Log.d("Coordenadas", "Puntos de la ruta: $geoPoints")

    val roadOverlay = Polyline().apply {
        setPoints(geoPoints)
        color = Color.BLUE // Personaliza el color de la ruta
    }
    mapView.overlayManager.add(roadOverlay)
    mapView.invalidate() // Refresca la vista
    Log.d("Ruta", "Dibujo completado")
}
