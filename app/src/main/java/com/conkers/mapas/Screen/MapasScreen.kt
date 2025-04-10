package com.conkers.mapas.Screen

import android.graphics.Color
import android.location.Location
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
    routeCoordinates: List<List<Double>>,
    currentLocation: Location?
) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            val mapView = MapView(context)
            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.controller.setZoom(15.0)

            // Centrar en la ubicación actual si no hay coordenadas de ruta
            if (routeCoordinates.isEmpty() && currentLocation != null) {
                val currentGeoPoint = GeoPoint(currentLocation.latitude, currentLocation.longitude)
                mapView.controller.setCenter(currentGeoPoint)
                Log.d("MapScreen", "Mapa centrado en la ubicación actual: ${currentLocation.latitude}, ${currentLocation.longitude}")
            } else if (routeCoordinates.isNotEmpty()) {
                routeCoordinates.firstOrNull()?.let {
                    mapView.controller.setCenter(GeoPoint(it[1], it[0]))
                    Log.d("MapScreen", "Mapa centrado en: ${it[1]}, ${it[0]}")
                }
            }

            // Dibujar la ruta si está disponible
            drawRoute(mapView, routeCoordinates)

            mapView
        },
        modifier = Modifier.fillMaxSize()
    )
}

fun drawRoute(mapView: MapView, routeCoordinates: List<List<Double>>) {
    if (routeCoordinates.isEmpty()) {
        Log.d("Ruta", "No hay ruta para dibujar todavía")
        return
    }

    val geoPoints = routeCoordinates.map { GeoPoint(it[1], it[0]) }
    Log.d("Coordenadas", "Puntos detallados de la ruta: $geoPoints")

    val roadOverlay = Polyline().apply {
        setPoints(geoPoints)
        color = Color.BLUE
    }
    mapView.overlayManager.add(roadOverlay)
    mapView.invalidate()
    Log.d("Ruta", "Ruta dibujada correctamente")
}
